package com.huahua.chaoxing.login.qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.ScanningBean;
import com.huahua.chaoxing.databinding.QrLoginFragmentBinding;
import com.huahua.chaoxing.util.DataUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class QrLoginFragment extends Fragment {

    private String loginUrl = "http://passport2.chaoxing.com/cloudscanlogin?mobiletip=%e7%94%b5%e8%84%91%e7%ab%af%e7%99%bb%e5%bd%95%e7%a1%ae%e8%ae%a4&pcrefer=http://i.chaoxing.com";
    private DataUtil datautil;
    private QrLoginViewModel mViewModel;
    private QrLoginFragmentBinding root;
    private String scanningUrl;
    private HashMap<String, String> dataMap;
    private Runnable runnable;


    public static QrLoginFragment newInstance() {
        return new QrLoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = DataBindingUtil.inflate(inflater, R.layout.qr_login_fragment, container, false);
        return root.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        datautil = new DataUtil(requireActivity());
        mViewModel = ViewModelProviders.of(this).get(QrLoginViewModel.class);
        // TODO: Use the ViewModel
        loadQr();
        root.refreshQr.setOnClickListener(v -> {
            loadQr();
        });
    }


    private void loadQr() {
        Thread thread = new Thread(new Runnable() {
            private Document document;
            private Connection.Response response;

            @Override
            public void run() {
                try {
                    // 1. 开始登录
                    response = Jsoup.connect(loginUrl).method(Connection.Method.GET).execute();
                    Map<String, String> cookies = response.cookies();
                    System.out.println("cookies = " + cookies);
                    mViewModel.setCookies((HashMap<String, String>) cookies);
                    document = response.parse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String uuid = document.select("input[ id =uuid]").attr("value");
                String enc = document.select("input[ id =enc]").attr("value");
                System.out.println("uuid = " + uuid);
                System.out.println(" enc = " + enc);
                // 2. 加载二维码
                scanningUrl = "http://passport2.chaoxing.com/createqr?uuid=" + uuid + "&xxtrefer=&type=1&mobiletip=%E7%94%B5%E8%84%91%E7%AB%AF%E7%99%BB%E5%BD%95%E7%A1%AE%E8%AE%A4";
                dataMap = new HashMap<>();
                dataMap.put("uuid", uuid);
                dataMap.put("enc", enc);
                loadQrUrl(scanningUrl);
            }
        });
        thread.start();

    }

    private void loadQrUrl(String url) {
        //开始加载二维码
        requireActivity().runOnUiThread(() -> Glide.with(requireActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(root.qrImg));
        scanningImg();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        runnable = null;

    }

    public synchronized void scanningImg() {
        // 3. 检测二维码扫描状态
        AtomicBoolean isScanning = new AtomicBoolean(false);
        String url = "http://passport2.chaoxing.com/getauthstatus";
        runnable = () -> {
            isScanning.set(false);
            Connection.Response scanningResponse = null;
            try {
                scanningResponse = Jsoup.connect(url).cookies(mViewModel.getCookies()).data(dataMap).method(Connection.Method.POST).execute();
                mViewModel.setCookies((HashMap<String, String>) Objects.requireNonNull(scanningResponse).cookies());
                System.out.println(scanningResponse.parse());
                Gson gson = new Gson();
                ScanningBean scanningBean = gson.fromJson(Objects.requireNonNull(scanningResponse).body(), ScanningBean.class);
                if (scanningBean.isStatus()) {
                    isScanning.set(true);
                    datautil.saveMap(mViewModel.getCookies());
                    root.setState("登录成功");
                    NavHostFragment.findNavController(QrLoginFragment.this).navigate(R.id.action_qrLoginFragment_to_userInfo);
                    requireActivity().finish();
                    return;
                }
                int type = Integer.parseInt(scanningBean.getType());
                root.setState(scanningBean.getMes());
                if (type == 2 || type == 6) {
                    isScanning.set(true);
                    root.setState("请刷新二维码");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        while (!isScanning.get()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (runnable != null) {
                runnable.run();
            }

        }
    }


}

