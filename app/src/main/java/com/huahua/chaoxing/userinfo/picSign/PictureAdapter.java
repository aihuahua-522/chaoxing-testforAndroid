package com.huahua.chaoxing.userinfo.picSign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.PicBean;
import com.huahua.chaoxing.databinding.PictureItemBinding;

import java.util.List;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/18.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    private Context context;
    private List<PicBean> localMedia;
    private PictureItemBinding root;

    public PictureAdapter(Context context, List<PicBean> localMedia) {
        this.context = context;
        this.localMedia = localMedia;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.picture_item, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(localMedia.get(position).getData().getThumbnail())
                .dontAnimate()
                .into(root.ivSelectPic);
        root.ivSelectPic.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(position);
                System.out.println("onItemClickListener 中的position === " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localMedia.size();
    }

    interface OnItemClickListener {
        void onClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull PictureItemBinding itemView) {
            super(itemView.getRoot());
        }
    }
}
