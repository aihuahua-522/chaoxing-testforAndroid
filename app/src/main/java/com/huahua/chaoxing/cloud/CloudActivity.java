package com.huahua.chaoxing.cloud;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.databinding.ActivityCloudBinding;
import com.huahua.chaoxing.util.SPUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class CloudActivity extends AppCompatActivity {

    private static final String TAG = "CloudActivity";
    ActivityCloudBinding root;
    CloudViewModel viewModel;
    private CloudModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = DataBindingUtil.setContentView(this, R.layout.activity_cloud);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        viewModel = ViewModelProviders.of(this).get(CloudViewModel.class);
        model = new CloudModel(viewModel);
        root.setViewModel(viewModel);
        getSp();
        observerModel();
        setOnClickListener();
        model.getOneWord();
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("提示")
                .setMessage("很多人都是因为疫情导致的学习通签到问题 \n 如果使用时间小于一个月的账号密码随便填,记得就行 \n 一定要同步课程和图片 \n 如果可以的话 \n 签到地点不要留空 \n 就算你们不拍照签到也请随意上传一张 \n 关于部分人说的信息泄露 \n 账号密码都让你们随便填 \n 泄露什么 \n 一定要查询状态 \n 我会尽量保证服务器的正常运行 \n 如果能醒或者有条件的还是推荐自己使用后电脑版本")
                .addAction("知道了嘻嘻", (dialog, index) -> dialog.dismiss())
                .show();

    }

    private void getSp() {
        try {
            viewModel.setEmail((String) SPUtils.get(this, "email", ""));
            viewModel.setPass((String) SPUtils.get(this, "pass", ""));
            viewModel.setTel((String) SPUtils.get(this, "tel", ""));
            viewModel.setSignPlace(URLDecoder.decode((String) SPUtils.get(this, "signPlace", ""), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Toasty.info(this, Objects.requireNonNull(e.getLocalizedMessage())).show();
            e.printStackTrace();
        }

    }


    /**
     * 监听model
     */
    private void observerModel() {
        viewModel.getCodeLiveData().observe(this, o -> {
            if ("200".equals(o)) {
                Toasty.success(CloudActivity.this, "提交成功").show();
                SPUtils.put(CloudActivity.this, "tel", viewModel.getTel());
                SPUtils.put(CloudActivity.this, "pass", viewModel.getPass());
                SPUtils.put(CloudActivity.this, "email", viewModel.getEmail());
                SPUtils.put(CloudActivity.this, "signPlace", viewModel.getSignPlace());
                root.cloudSubmit.setEnabled(false);
                root.cloudSubmit.setText("已提交");
            }
        });

        viewModel.getNetInfo().observe(this, s -> {
            if (s != null && !"".equals(s)) {
                Toasty.info(this, s).show();
            }
        });
        viewModel.getOneWord().observe(this, s -> {
            if (s != null && !"".equals(s)) {
                try {
                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    ArrayList<String> list = new Gson().fromJson(s, type);
                    root.cloudText.startSimpleRoll(list);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    Toasty.info(this, Objects.requireNonNull(e.getLocalizedMessage())).show();
                }
            }
        });

        viewModel.getState().observe(this, s -> {
            root.cloudState.setText(s);
        });
    }


    private void setOnClickListener() {
        root.cloudSubmit.setOnClickListener(v -> {
            model.submit(this, viewModel.getTel(), viewModel.getPass(), viewModel.getEmail(), viewModel.getSignPlace());
        });

        root.syncPic.setOnClickListener(v -> {
            model.submitPic(this, viewModel.getTel(), viewModel.getPass());
        });

        root.syncCourse.setOnClickListener(v -> {
            model.chooseCourse(this, viewModel.getTel(), viewModel.getPass());
        });


        root.getUserInfo.setOnClickListener(v -> {
            model.getInfo(viewModel.getTel(), viewModel.getPass());
        });

        root.cloudCancel.setOnClickListener(v -> {
            model.cancelCloud(viewModel.getTel(), viewModel.getPass());
        });
    }


}
