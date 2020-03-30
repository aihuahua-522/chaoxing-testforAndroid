package com.huahua.chaoxing.userinfo.sign;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.SignBean;
import com.huahua.chaoxing.databinding.SignTextItemBinding;

import java.util.List;

/**
 * Author: Administrator
 * Created on 2020/3/10
 */
public class SignAdapter extends RecyclerView.Adapter<SignAdapter.ViewHolder> {

    private android.content.Context mContext;
    private List<SignBean> mDataList;
    private ClickListener clickListener;
    private SignTextItemBinding root;

    public SignAdapter(Context context, List<SignBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = DataBindingUtil.inflate(android.view.LayoutInflater.from(mContext), R.layout.sign_text_item, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (position < mDataList.size()) {
            final SignBean entity = mDataList.get(position);
            root.setSignBean(entity);
            if (clickListener != null) {
                root.getRoot().setOnClickListener(v -> clickListener.onClick(holder.mBinding.getRoot(), position));
            }
        }
    }


    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public interface ClickListener {
        void onClick(android.view.View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SignTextItemBinding mBinding;

        public ViewHolder(SignTextItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }

}
