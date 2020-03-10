package com.huahua.chaoxing.userinfo.sign;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.ClassBean;
import com.huahua.chaoxing.bean.SignBean;
import com.huahua.chaoxing.databinding.SignTextFragmentBinding;
import com.huahua.chaoxing.userinfo.ui.main.PageViewModel;
import com.huahua.chaoxing.util.SPUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class SignText extends Fragment implements SignAdapter.ClickListener {

    Handler handler = new Handler();
    private List<SignBean> signBeans = new ArrayList<>();
    private PageViewModel mViewModel;
    private SignTextFragmentBinding root;
    private SignAdapter signAdapter;
    private Runnable runnable;
    private Thread thread;

    public static SignText newInstance() {
        return new SignText();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = DataBindingUtil.inflate(inflater, R.layout.sign_text_fragment, container, false);
        return root.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(PageViewModel.class);
        // TODO: Use the ViewModel
        String uid = mViewModel.getCookies().get("_uid");
        System.out.println("uid = " + uid);
        String myImg = "http://photo.chaoxing.com/p/" + uid + "_180";
        mViewModel.getRefresh().observe(getViewLifecycleOwner(), aBoolean -> {
            String name = (String) mViewModel.getTemp().get("name");
            root.name.setText(name);
            ArrayList<ClassBean> classBeans = (ArrayList<ClassBean>) mViewModel.getTemp().get("classBeans");
            try {
                startSign(classBeans);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        signAdapter = new SignAdapter(requireActivity(), signBeans);
        signAdapter.setOnClickListener(this);
        root.signRecycle.setLayoutManager(new LinearLayoutManager(requireActivity()));
        root.signRecycle.setAdapter(signAdapter);
        Glide.with(requireActivity())
                .load(myImg)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .into(root.imageView);

    }

    private synchronized void startSign(ArrayList<ClassBean> classBeans) throws InterruptedException, IOException {
        if (classBeans == null) {
            return;
        }
        runnable = new Runnable() {
            private Elements elements;

            @Override
            public void run() {
                while (true) {
                    System.out.println("签到运行");
                    signBeans.clear();
                    for (int i = 0; i < classBeans.size(); i++) {
                        String url = classBeans.get(i).getUrl();
                        try {
                            Connection.Response response = Jsoup.connect(url).cookies(mViewModel.getCookies()).method(Connection.Method.GET).execute();
                            Document document = response.parse();
                            elements = document.select("#startList div .Mct");
                            if (elements == null || elements.size() == 0) {
                                System.out.println("无签到活动");
                            }
                            for (Element ele : elements) {
                                String onclick = ele.attr("onclick");
                                System.out.println(onclick);
                                if (onclick != null && onclick.length() > 0) {
                                    String split = onclick.split("\\(")[1];
                                    String activeId = split.split(",")[0];
                                    System.out.println(split);
                                    System.out.println(activeId);
                                    System.out.println("保存的数据" + mViewModel.getTemp().get(activeId));
                                    if (mViewModel.getTemp().get(activeId) != null) {
                                        SignBean signBean = new SignBean();
                                        signBean.setSignClass(classBeans.get(i).getClassName());
                                        signBean.setSignName(classBeans.get(i).getClassmate());
                                        signBean.setSignState("签到成功");
                                        signBean.setSignTime(ele.select(".Color_Orang").text());
                                        signBeans.add(signBean);
                                        continue;
                                    }
                                    String signUrl = "https://mobilelearn.chaoxing.com/pptSign/stuSignajax?name="
                                            + URLDecoder.decode(mViewModel.getTemp().get("name").toString())
                                            + "&address="
                                            + mViewModel.getTemp().get("signPlace")
                                            + "&activeId="
                                            + activeId
                                            + "&uid="
                                            + mViewModel.getCookies().get("_uid")
                                            + "&clientip=&latitude=-1&longitude=-1&fid="
                                            + mViewModel.getCookies().get("fid")
                                            + "&appType=15&ifTiJiao=1";
                                    System.out.println(signUrl);
                                    System.out.println("==============" + activeId + "签到中=================");
                                    Connection.Response signResponse = Jsoup.connect(signUrl).cookies(mViewModel.getCookies()).method(Connection.Method.GET).execute();
                                    Element element = signResponse.parse().body();
                                    System.out.println("签到状态" + element.getElementsByTag("body").text());
                                    SignBean signBean = new SignBean();
                                    signBean.setSignClass(classBeans.get(i).getClassName());
                                    signBean.setSignName(classBeans.get(i).getClassmate());
                                    signBean.setSignState(element.getElementsByTag("body").text());
                                    signBean.setSignTime(ele.select(".Color_Orang").text());
                                    if (signBean.getSignState().equals("您已签到过了")) {
                                        mViewModel.setTemp(activeId, "签到成功");
                                    }
                                    signBeans.add(signBean);
                                    Thread.sleep(1000);
                                }
                            }
//                            System.out.println(signBeans);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        requireActivity().runOnUiThread(() -> {
                            signAdapter.notifyItemChanged(0);
                        });
                        long signTime = Long.parseLong((String) SPUtils.get(requireActivity(), "signTime", "60")) * 1000;
                        System.out.println(signTime);
                        Thread.sleep(signTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }


    @Override
    public void onClick(View view, int position) {
        Toast.makeText(requireActivity(), "emmmmmmm", Toast.LENGTH_SHORT).show();
    }
}
