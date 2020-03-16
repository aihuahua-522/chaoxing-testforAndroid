package com.huahua.chaoxing.userinfo.sign;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/16.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

public class SignLayoutManager extends LinearLayoutManager {
    /**
     * Creates a vertical LinearLayoutManager
     *
     * @param context Current context, will be used to access resources.
     */
    public SignLayoutManager(Context context) {
        super(context);
    }

    /**
     * @param context       Current context, will be used to access resources.
     * @param orientation   Layout orientation. Should be {@link #HORIZONTAL} or {@link
     *                      #VERTICAL}.
     * @param reverseLayout When set to true, layouts from end to start.
     */
    public SignLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    /**
     * Constructor used when layout manager is set in XML by RecyclerView attribute
     * "layoutManager". Defaults to vertical orientation.
     * <p>
     * {@link android.R.attr#orientation}
     * {@link androidx.recyclerview.R.attr#reverseLayout}
     * {@link androidx.recyclerview.R.attr#stackFromEnd}
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public SignLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

}
