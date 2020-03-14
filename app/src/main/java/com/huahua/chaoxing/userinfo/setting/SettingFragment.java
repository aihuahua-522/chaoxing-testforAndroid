package com.huahua.chaoxing.userinfo.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.huahua.chaoxing.MainActivity;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.databinding.SettingFragmentBinding;
import com.huahua.chaoxing.userinfo.ui.main.PageViewModel;
import com.huahua.chaoxing.util.DataUtil;
import com.huahua.chaoxing.util.SPUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.tencent.bugly.beta.Beta;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import es.dmoral.toasty.Toasty;

/**
 * @author Administrator
 */
public class SettingFragment extends Fragment {

    private PageViewModel mViewModel;
    private SettingFragmentBinding root;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = DataBindingUtil.inflate(inflater, R.layout.setting_fragment, container, false);
        return root.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(PageViewModel.class);
        // TODO: Use the ViewModel
        root.talkMe.setOnClickListener(v -> {
            try {
                //第二种方式：可以跳转到添加好友，如果qq号是好友了，直接聊天
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=1986754601";//uin是发送过去的qq号码
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception e) {
                Toasty.info(requireActivity(), "请先安装qq", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        root.signPlace.setOnClickListener(v -> {
            String signPlace = (String) SPUtils.get(requireActivity(), "signPlace", "");
            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
            builder.setTitle("输入你需要的定位地址")
                    .setPlaceholder(URLDecoder.decode(signPlace))
                    .setInputType(InputType.TYPE_CLASS_TEXT)
                    .addAction("取消", (dialog, index) -> dialog.dismiss())
                    .addAction("确定", (dialog, index) -> {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null) {
                            try {
                                SPUtils.put(requireActivity(), "signPlace", URLEncoder.encode(text.toString(), "utf-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                Toasty.error(requireActivity(), e.getMessage()).show();
                            }
                            mViewModel.setTemp("signPlace", text);
                            Toasty.info(getActivity(), text + "\n 自动签到时重启生效", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .show();
        });

        root.signTime.setOnClickListener(v -> {
            int signTime = Integer.parseInt((String) SPUtils.get(requireActivity(), "signTime", "60"));
            int selectIndex = 1;
            if (signTime == 30) {
                selectIndex = 0;
            } else if (signTime == 180) {
                selectIndex = 2;
            }
            final String[] items = new String[]{"30", "60", "180", "300", "600"};
            final QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(requireActivity());
            builder.setTitle("请选择扫描时间(单位为秒)")
                    .setCheckedIndex(selectIndex)
                    .addItems(items, (dialog, which) -> builder.setCheckedIndex(which))
                    .addAction("确定", (dialog, index) -> {
                        Toasty.info(getActivity(), "你选择了 " + items[builder.getCheckedIndex()] + "\n 自动签到时重启生效", Toast.LENGTH_SHORT).show();
                        SPUtils.put(requireActivity(), "signTime", items[builder.getCheckedIndex()]);
                        mViewModel.setTemp("signTime", items[builder.getCheckedIndex()]);
                        dialog.dismiss();
                    }).show();
        });

        root.update.setOnClickListener(v -> {
            Beta.checkUpgrade();
        });


        root.signOut.setOnClickListener(v -> {
            new DataUtil(requireActivity()).clearMap();
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        });

        root.github.setOnClickListener(v -> {
            // 打开网址 这个是通过打开android自带的浏览器进行的打开网址
            Uri uri = Uri.parse("https://github.com/aihuahua-522/chaoxing-test");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
                Toasty.info(requireActivity(), "点个爱心可好", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
