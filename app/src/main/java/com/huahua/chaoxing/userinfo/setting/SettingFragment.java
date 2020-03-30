package com.huahua.chaoxing.userinfo.setting;

import android.content.Intent;
import android.didikee.donate.AlipayDonate;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huahua.chaoxing.MainActivity;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.PicBean;
import com.huahua.chaoxing.cloud.CloudActivity;
import com.huahua.chaoxing.databinding.SettingFragmentBinding;
import com.huahua.chaoxing.userinfo.picSign.PicSignFragment;
import com.huahua.chaoxing.userinfo.ui.main.PageViewModel;
import com.huahua.chaoxing.util.DataUtil;
import com.huahua.chaoxing.util.SPUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.tencent.bugly.beta.Beta;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
        if (root != null) {
            try {
                setOnClickListener();

            } catch (Exception e) {
                Toasty.error(requireActivity(), Objects.requireNonNull(e.getMessage())).show();
            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        initPic();
    }

    private void initPic() {
        Type typeToken = new TypeToken<List<PicBean>>() {
        }.getType();
        String pic = (String) SPUtils.get(requireActivity(), "pic", "");
        if (pic == null || pic.isEmpty()) {
            Toasty.error(requireActivity(), "你还没有保存过图片").show();
            return;
        }
        ArrayList<PicBean> picList = new Gson().fromJson(pic, typeToken);
        mViewModel.setTemp("pic", picList);
    }

    private void setOnClickListener() {
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
            } else if (signTime == 300) {
                selectIndex = 3;
            } else if (signTime == 600) {
                selectIndex = 4;
            } else if (signTime == 10) {
                selectIndex = 5;
            } else if (signTime == 5) {
                selectIndex = 6;
            }
            final String[] items = new String[]{"30", "60", "180", "300", "600", "10", "5"};
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

        root.payload.setOnClickListener(v -> {
            donateAlipay();
        });

        root.setPic.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PicSignFragment.class);
            intent.putExtra("cookies", (HashMap<String, String>) mViewModel.getCookies());
            startActivity(intent);
        });

        root.worker.setOnClickListener(v -> {
            boolean work = (boolean) SPUtils.get(requireActivity(), "work", false);
            int selectIndex = 0;
            if (work) {
                selectIndex = 1;
            }

            final String[] items = new String[]{"关闭", "开启"};
            final QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(requireActivity());
            builder.setTitle("请选择作业提醒")
                    .setCheckedIndex(selectIndex)
                    .addItems(items, (dialog, which) -> builder.setCheckedIndex(which))
                    .addAction("确定", (dialog, index) -> {
                        Toasty.info(Objects.requireNonNull(getActivity()), "你选择了 " + items[builder.getCheckedIndex()] + "\n 自动签到时重启生效", Toast.LENGTH_SHORT).show();
                        SPUtils.put(requireActivity(), "work", "开启".equals(items[builder.getCheckedIndex()]));
                        mViewModel.setTemp("work", "开启".equals(items[builder.getCheckedIndex()]));
                        dialog.dismiss();
                    }).show();
        });

        root.pcAnswer.setOnClickListener(v -> {

            boolean answer = (boolean) SPUtils.get(requireActivity(), "answer", false);
            int selectIndex = 0;
            if (answer) {
                selectIndex = 1;
            }

            final String[] items = new String[]{"关闭", "开启"};
            final QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(requireActivity());
            builder.setTitle("请选择是否抢答")
                    .setCheckedIndex(selectIndex)
                    .addItems(items, (dialog, which) -> builder.setCheckedIndex(which))
                    .addAction("确定", (dialog, index) -> {
                        Toasty.info(Objects.requireNonNull(getActivity()), "你选择了 " + items[builder.getCheckedIndex()] + "\n 自动签到时重启生效", Toast.LENGTH_SHORT).show();
                        SPUtils.put(requireActivity(), "answer", "开启".equals(items[builder.getCheckedIndex()]));
                        mViewModel.setTemp("answer", "开启".equals(items[builder.getCheckedIndex()]));
                        dialog.dismiss();
                    }).show();
        });

        root.yum.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), CloudActivity.class));
        });


    }


    /**
     * 支付宝支付
     * <p>
     * payCode = stx00187oxldjvyo3ofaw60
     * 注：不区分大小写
     */
    private void donateAlipay() {
        boolean hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(requireActivity());
        if (hasInstalledAlipayClient) {
            new QMUIDialog.MessageDialogBuilder(requireActivity())
                    .setTitle("捐赠")
                    .setMessage("因为加入云签到的缘故 \n 新增加一位作者 所以捐赠有两个人.随" +
                            "意就行")
                    .addAction("给花花", (dialog, index) -> {
                        AlipayDonate.startAlipayClient(requireActivity(), "fkx08925ggrze94jzf3cs68");
                        dialog.dismiss();
                    })
                    .addAction("给小妖", (dialog, index) -> {
                        AlipayDonate.startAlipayClient(requireActivity(), "fkx06216gw3hly0ugiiyx49");
                        dialog.dismiss();
                    })
                    .show();
        }
    }


}
