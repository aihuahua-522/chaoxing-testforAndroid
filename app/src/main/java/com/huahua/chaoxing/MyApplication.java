package com.huahua.chaoxing;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/8.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "adea638331", true);
        Beta.autoCheckUpgrade = true;
    }
}