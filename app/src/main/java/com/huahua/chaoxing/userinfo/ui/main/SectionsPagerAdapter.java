package com.huahua.chaoxing.userinfo.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.huahua.chaoxing.R;
import com.huahua.chaoxing.userinfo.myclass.MyClassFragment;
import com.huahua.chaoxing.userinfo.setting.SettingFragment;
import com.huahua.chaoxing.userinfo.sign.SignText;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};

    private final Context mContext;
    private List<Fragment> fragments;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        fragments = new ArrayList<>();
        fragments.add(new MyClassFragment());
        fragments.add(new SignText());
        fragments.add(new SettingFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
//        return MyClassFragment.newInstance(position + 1);
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return fragments.size();
    }
}