package com.huahua.chaoxing.cloud;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.huahua.chaoxing.R;

import java.util.Objects;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/27.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

public class myAttributes {
    private static final String TAG = "myAttributes";

    @BindingAdapter("app:imgUrl")
    public static void setImgUrl(ImageView imageView, String url) {
        Glide.with(imageView)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Objects.requireNonNull(e).printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(false)
                .placeholder(R.drawable.placeholder)
                .into(imageView);
    }
}
