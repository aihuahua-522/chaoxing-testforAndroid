package com.huahua.chaoxing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huahua.chaoxing.databinding.FragmentFirstBinding;
import com.huahua.chaoxing.util.DataUtil;

import java.io.IOException;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding root;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        root = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false);
        return root.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root.goQrLogin.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_qrLoginFragment);
        });
        root.goUserLogin.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_userLoginFragment);
        });
        Glide.with(requireActivity())
                .load("https://api.ixiaowai.cn/mcapi/mcapi.php")
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(root.appImage);

        HashMap<String, String> map = null;
        try {
            map = new DataUtil(requireActivity()).getMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (map != null) {
            Toasty.info(requireActivity(), "自动登录中", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_userInfo);
            requireActivity().finish();
        }
    }

}
