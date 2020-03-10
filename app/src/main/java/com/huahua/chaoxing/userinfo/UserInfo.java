package com.huahua.chaoxing.userinfo;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
        if (map != null) {
            Toast.makeText(this, "自动登陆中(无数据尝试退出登录)", Toast.LENGTH_SHORT).show();
        }
        pageViewModel.setCookies(map);
        root.viewPager.setAdapter(sectionsPagerAdapter);
        root.tabs.setupWithViewPager(root.viewPager);
        root.viewPager.setOffscreenPageLimit(2);
        root.fab.setOnClickListener(view -> Snackbar.make(view, "等待添加", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

    }


}