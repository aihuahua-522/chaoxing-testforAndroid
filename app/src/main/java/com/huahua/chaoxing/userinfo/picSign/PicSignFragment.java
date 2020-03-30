package com.huahua.chaoxing.userinfo.picSign;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.PicBean;
import com.huahua.chaoxing.bean.TokenBean;
import com.huahua.chaoxing.databinding.PicSignFragmentBinding;
import com.huahua.chaoxing.util.HttpUtil;
import com.huahua.chaoxing.util.SPUtils;
import com.huahua.chaoxing.util.SSLSocketClient;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PicSignFragment extends AppCompatActivity {

    private PicSignViewModel mViewModel;
    private PicSignFragmentBinding root;
    private PictureAdapter adapter;
    private HashMap<String, String> cookies;
    private ArrayList<PicBean> picBeans = new ArrayList<>();
    private MaterialDialog show;

    private void getOldPic() {
        String pic = (String) SPUtils.get(this, "pic", "");
        if (pic == null || pic.isEmpty()) {
            Toasty.error(this, "你还没有保存过图片").show();
            return;
        }

        Type typeToken = new TypeToken<List<PicBean>>() {
        }.getType();


        ArrayList<PicBean> picList = new Gson().fromJson(pic, typeToken);
        picBeans.addAll(picList);
        adapter.notifyItemRangeChanged(0, picBeans.size());
        adapter.notifyDataSetChanged();
    }

    private void saveImg(List<LocalMedia> arrayList) {

        // 到这里一定登录成功（bug除外）
        String getTokenUrl = "https://pan-yz.chaoxing.com/api/token/uservalid";

        startLoad();

        new Thread(() -> {
            try {
                HttpUtil.trustEveryone();
                String tokenResult = Jsoup.connect(getTokenUrl).method(Connection.Method.GET).cookies(cookies).execute().body();
                System.out.println("tokenResult ----> " + "\n" + tokenResult);
                Gson gson = new Gson();
                TokenBean tokenBean = gson.fromJson(tokenResult, TokenBean.class);
                if (tokenBean == null || "".equals(tokenBean.get_token())) {
                    Toasty.error(PicSignFragment.this, "token获取失败").show();
                }
                String sendUidUrl = "https://pan-yz.chaoxing.com/api/crcstatus?puid=" + cookies.get("UID") + "&crc=bfb2e7968005665f8ac0d0465099a9d7&_token=" + tokenBean.get_token();
                HttpUtil.trustEveryone();
                String sendUidResult = Jsoup.connect(sendUidUrl).method(Connection.Method.GET).cookies(cookies).execute().body();

                System.out.println("sendUidResult ---->" + "\n" + sendUidResult);

                String uploadUrl = "https://pan-yz.chaoxing.com/upload?_token=" + tokenBean.get_token();

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("puid", cookies.get("UID"));

                for (int i = 0; i < arrayList.size(); i++) {
                    File file;

                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        file = new File(arrayList.get(i).getAndroidQToPath());
                    } else {
                        file = new File(arrayList.get(i).getPath());
                    }

                    postFile(uploadUrl, hashMap, file, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            PicBean picBean = new Gson().fromJson(response.body().string(), PicBean.class);
                            picBeans.add(picBean);
//                            System.out.println(response.body().string());
                            endLoad();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toasty.error(PicSignFragment.this, Objects.requireNonNull(e.getLocalizedMessage())).show();
            }
        }).start();


    }

    private void startLoad() {
        show = new MaterialDialog.Builder(this)
                .iconRes(R.drawable.placeholder)
                .limitIconToDefaultSize()
                .title("上传中")
                .content("请稍后")
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .negativeText("取消")
                .show();
    }

    private void endLoad() {
        runOnUiThread(() -> {
            String json = new Gson().toJson(picBeans);
            SPUtils.put(this, "pic", json);
            show.dismiss();
            Toasty.info(this, "上传成功").show();
            adapter.notifyItemRangeChanged(0, picBeans.size());
            adapter.notifyDataSetChanged();
        });
    }

    public void postFile(final String url, final Map<String, String> map, File file, Callback callback) {
        // form 表单形式上传
        //创建OkHttpClient对象(前提是导入了okhttp.jar和okio.jar)
        OkHttpClient client = new OkHttpClient().newBuilder()
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置    //忽略验证证书
                .build();

        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", filename, body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
//                Log.d("HttpUtils", "key=="+key+"value=="+value);
                requestBody.addFormDataPart(key, value);
            }
        }

        Request request = new Request.Builder().url(url).header("Cookie", cookies.toString()).post(requestBody.build()).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(30000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(callback);
//        client.newCall(request).enqueue(callback);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        List<LocalMedia> localMedia = new ArrayList<>();
        if (requestCode == PictureConfig.CHOOSE_REQUEST && data != null) {
            // 图片选择结果回调
//            localMedia.addAll();
            if (cookies == null) {
                Toasty.error(PicSignFragment.this, "你的cookies丢失").show();
                return;
            }
            saveImg(PictureSelector.obtainMultipleResult(data));
//            System.out.println(localMedia.get(0).getPath());
//            System.out.println(localMedia.get(0).getAndroidQToPath());

            // 例如 LocalMedia 里面返回三种 path
            // 1.media.getPath(); 为原图 path
            // 2.media.getCutPath();为裁剪后 path，需判断 media.isCut();是否为 true
            // 3.media.getCompressPath();为压缩后 path，需判断 media.isCompressed();是否为 true
            // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//            adapter.setList(selectList);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = DataBindingUtil.setContentView(this, R.layout.pic_sign_fragment);
        mViewModel = ViewModelProviders.of(this).get(PicSignViewModel.class);
        ImmersionBar.with(this).statusBarDarkFont(true).init();
        XUI.initTheme(this);
        cookies = (HashMap<String, String>) getIntent().getSerializableExtra("cookies");
        setRecycle();
        getOldPic();
        setOnclickListener();
    }

    private void setRecycle() {
        adapter = new PictureAdapter(this, picBeans);
        //设置layoutManager
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        root.picRecycle.setLayoutManager(manager);
        root.picRecycle.setAdapter(adapter);
    }

    private void setOnclickListener() {
        root.deletImg.setOnClickListener(v -> {
            new MaterialDialog.Builder(this)
                    .content("善意提示")
                    .positiveText("确认")
                    .negativeText("取消")
                    .onPositive((dialog, which) -> {
                        picBeans.clear();
                        SPUtils.remove(this, "pic");
                        adapter.notifyDataSetChanged();
                        Toasty.error(PicSignFragment.this, "删除成功").show();
                    })
                    .show();


        });
        root.updateImg.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //未授权，申请授权(从相册选择图片需要读取存储卡的权限)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            } else {
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考 Demo GlideEngine.java
                        .maxSelectNum(9)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });
    }
}
