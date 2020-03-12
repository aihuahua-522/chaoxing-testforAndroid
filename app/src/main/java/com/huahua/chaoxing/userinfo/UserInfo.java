package com.huahua.chaoxing.userinfo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.databinding.ActivityUserInfoBinding;
import com.huahua.chaoxing.userinfo.ui.main.PageViewModel;
import com.huahua.chaoxing.userinfo.ui.main.SectionsPagerAdapter;
import com.huahua.chaoxing.util.DataUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.IOException;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class UserInfo extends AppCompatActivity {

    DataUtil dataUtil;
    private ActivityUserInfoBinding root;
    private PageViewModel pageViewModel;
    private HashMap<String, String> map;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = DataBindingUtil.setContentView(this, R.layout.activity_user_info);
//        ImmersionBar.with(this).statusBarDarkFont(true).init();
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        dataUtil = new DataUtil(this);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        try {
            map = dataUtil.getMap();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        setNotification();
        if (map != null) {
            Toasty.info(this, "自动登陆中(无数据尝试退出登录)").show();
        }
        pageViewModel.setCookies(map);
        root.viewPager.setAdapter(sectionsPagerAdapter);
        root.tabs.setupWithViewPager(root.viewPager);
        root.viewPager.setOffscreenPageLimit(2);
        root.fab.setOnClickListener(view -> Snackbar.make(view, "等待添加", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toasty.error(this, "当前无权限，请授权").show();
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
        }
    }

    private void setNotification() {

        String id = "1";
        String name = "爱花花";

        /**
         *  创建通知栏管理工具
         */

        NotificationManager notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);

        /**
         *  实例化通知栏构造器
         */

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        /**
         *  设置Builder
         */
        //设置标题
        mBuilder.setContentTitle("爱花花")
                //设置内容
                .setContentText("正在运行中")
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.placeholder))
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            mBuilder.setChannelId(id);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(1, mBuilder.build());
            return;
        }
        notificationManager.notify(1, mBuilder.build());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toasty.error(this, "授权失败").show();
                } else {
                    Toasty.success(this, "授权成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}