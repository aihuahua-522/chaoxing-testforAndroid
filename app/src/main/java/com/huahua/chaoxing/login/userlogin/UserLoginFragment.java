package com.huahua.chaoxing.login.userlogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.huahua.chaoxing.R;
import com.huahua.chaoxing.databinding.UserLoginFragmentBinding;
import com.huahua.chaoxing.util.DataUtil;
import com.huahua.chaoxing.util.SPUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class UserLoginFragment extends Fragment {

    private DataUtil dataUtil;
    private UserLoginViewModel mViewModel;
    private UserLoginFragmentBinding root;

    public static UserLoginFragment newInstance() {
        return new UserLoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = DataBindingUtil.inflate(inflater, R.layout.user_login_fragment, container, false);
        return root.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserLoginViewModel.class);
        String username = (String) SPUtils.get(requireActivity(), "username", "");
        String password = (String) SPUtils.get(requireActivity(), "password", "");
        root.username.setText(username);
        root.password.setText(password);
        dataUtil = new DataUtil(requireActivity());
        // TODO: Use the ViewModel
        dataUtil.clearMap();
        root.loginBtn.setOnClickListener(v -> new Thread(() -> {
            try {
                if (!Objects.requireNonNull(root.username.getText()).toString().isEmpty() && !Objects.requireNonNull(root.password.getText()).toString().isEmpty()) {
                    SPUtils.put(requireActivity(), "username", root.username.getText().toString());
                    SPUtils.put(requireActivity(), "password", root.password.getText().toString());
                    String loginUrl = "http://i.chaoxing.com/vlogin?passWord=" + root.password.getText() + "&userName=" + root.username.getText();
                    Connection.Response response = Jsoup.connect(loginUrl).method(Connection.Method.GET).execute();
                    if (response.parse().body().toString().contains("true")) {
                        Map<String, String> cookies = response.cookies();
                        dataUtil.saveMap((HashMap<String, String>) cookies);
                        NavHostFragment.findNavController(UserLoginFragment.this).navigate(R.id.action_userLoginFragment_to_userInfo);
                        requireActivity().finish();
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                Toasty.info(requireActivity(), response.parse().toString()).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());
    }

}
