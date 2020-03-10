package com.huahua.chaoxing.userinfo.myclass;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.ClassBean;
import com.huahua.chaoxing.databinding.FragmentMyClassBinding;
import com.huahua.chaoxing.userinfo.myclass.MyClassFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ClassBean} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<ClassBean> mValues;
    private final OnListFragmentInteractionListener mListener;
    private FragmentMyClassBinding root;

    public MyItemRecyclerViewAdapter(List<ClassBean> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        root = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_my_class, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.root.classname.setText(mValues.get(position).getClassName());
        holder.root.classmate.setText(mValues.get(position).getClassmate());
        holder.root.teacher.setText(mValues.get(position).getTeacher());
        holder.root.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(mValues.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        FragmentMyClassBinding root;

        ViewHolder(FragmentMyClassBinding binding) {
            super(binding.getRoot());
            this.root = binding;
        }
    }
}
