package com.huahua.chaoxing.cloud;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.huahua.chaoxing.Domain;
import com.huahua.chaoxing.bean.CourseBean;
import com.huahua.chaoxing.bean.PicBean;
import com.huahua.chaoxing.bean.PicVo;
import com.huahua.chaoxing.util.DataUtil;
import com.huahua.chaoxing.util.HttpUtil;
import com.huahua.chaoxing.util.SPUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/26.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

class CloudModel {
    private static final String TAG = "CloudModel";
    private CloudViewModel model;

    CloudModel(CloudViewModel model) {
        this.model = model;
    }


    void submit(Context context, String tel, String password, String email, String signPlace) {

        FormBody.Builder body = new FormBody.Builder();
        body.add("tel", tel);
        body.add("pass", password);
        body.add("email", email);
        body.add("signPlace", signPlace);
        FormBody formBody = body.build();
        Log.i(TAG, "submit: " + formBody);
        HashMap<String, String> map = null;
        try {
            map = new DataUtil(context).getMap();
        } catch (IOException | ClassNotFoundException e) {
            model.setNetInfo(e.getLocalizedMessage());
            e.printStackTrace();
        }
        Request.Builder builder = new Request.Builder().url(Domain.DOMAIN + "LoginServlet").addHeader("Cookie", "huahua=123").post(formBody);
        Set<String> keySets = map.keySet();
        for (String keySet : keySets) {
            builder.addHeader("Cookie", keySet + "=" + map.get(keySet));
        }
        Request request = builder.build();
        Call call = HttpUtil.getClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                model.setNetInfo(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String string = Objects.requireNonNull(response.body()).string();
                    JsonElement jsonElement = JsonParser.parseString(string);
                    int code = jsonElement.getAsJsonObject().get("code").getAsInt();
                    String msg = jsonElement.getAsJsonObject().get("msg").getAsString();
                    model.setCode(String.valueOf(code));
                    model.setNetInfo(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    model.setNetInfo(e.getLocalizedMessage());
                }

            }
        });

    }

    void getOneWord() {
        OkHttpClient client = HttpUtil.getClient();
        Request request = new Request.Builder().url(Domain.DOMAIN + "OneWord").get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                model.setNetInfo(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                model.setOneWord(Objects.requireNonNull(response.body()).string());

            }
        });
    }

    void chooseCourse(Context context, String tel, String pass) {
        String courseBeansSrtr = (String) SPUtils.get(context, "class", "");
        if (courseBeansSrtr == null || "".equals(courseBeansSrtr)) {
            model.setNetInfo("未检测到课程信息");
            return;
        }
        ArrayList<CourseBean> updateCourseBean = new ArrayList<>();
        Type type = new TypeToken<ArrayList<CourseBean>>() {
        }.getType();
        ArrayList<CourseBean> courseBeans = new Gson().fromJson(courseBeansSrtr, type);
        final String[] items = new String[courseBeans.size()];
        int[] array = new int[courseBeans.size()];
        for (int i = 0; i < courseBeans.size(); i++) {
            items[i] = courseBeans.get(i).getClassName() + courseBeans.get(i).getCourseName();
            array[i] = i;
        }
        final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(context)
                .setCheckedItems(array)
                .addItems(items, (dialog, which) -> {
                });
        builder.addAction("取消", (dialog, index) -> dialog.dismiss());
        builder.addAction("提交", (dialog, index) -> {
            for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
                System.out.println(builder.getCheckedItemIndexes()[i] + "huahua");
                updateCourseBean.add(courseBeans.get(builder.getCheckedItemIndexes()[i]));
            }
            updateCourse(tel, pass, updateCourseBean);
            dialog.dismiss();
        });
        builder.show();

    }

    private void updateCourse(String tel, String pass, ArrayList<CourseBean> updateCourseBean) {

        String json = new Gson().toJson(updateCourseBean);
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("tel", tel);
        builder.add("pass", pass);
        builder.add("course", json);
        FormBody body = builder.build();
        Request request = new Request.Builder().url(Domain.DOMAIN + "CourseServlet").post(body).build();
        HttpUtil.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                model.setNetInfo(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String msg = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject().get("msg").getAsString();
                    model.setNetInfo(msg);
                } catch (Exception e) {
                    model.setNetInfo(e.getLocalizedMessage());
                }
            }
        });
    }

    void submitPic(Context context, String tel, String pass) {

        // 这里为什么这样转呢
        // 因为和服务器的PicBean不一样
        // 为了改更少的代码
        // 本地处理比较好
        // TODO 留坑

        FormBody.Builder builder = new FormBody.Builder();
        String picBeansString = (String) Objects.requireNonNull(SPUtils.get(context, "pic", ""));
        Type type = new TypeToken<ArrayList<PicBean>>() {
        }.getType();
        ArrayList<PicBean> o = new Gson().fromJson(picBeansString, type);
        ArrayList<PicVo> strings = new ArrayList<>();
        if (o == null) {
            model.setNetInfo("请先添加图片");
            return;
        }
        for (int i = 0; i < o.size(); i++) {
            PicBean picBean = o.get(i);
            PicVo picVo = new PicVo(0, picBean.getObjectId(), BigInteger.valueOf(Long.parseLong(model.getTel())));
            strings.add(picVo);
        }
        builder.add("picBeans", new Gson().toJson(strings));
        builder.add("tel", tel);
        builder.add("pass", pass);
        FormBody body = builder.build();

        Request request = new Request.Builder().url(Domain.DOMAIN + "SetPicServlet").post(body).build();
        Call call = HttpUtil.getClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                model.setNetInfo(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String string = Objects.requireNonNull(response.body()).string();
                    JsonObject asJsonObject = JsonParser.parseString(string).getAsJsonObject();
                    model.setNetInfo(asJsonObject.get("msg").getAsString());
                } catch (Exception e) {
                    model.setNetInfo(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    void getInfo(String tel, String pass) {

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("tel", tel);
        builder.add("pass", pass);
        FormBody formBody = builder.build();
        Request request = new Request.Builder().url(Domain.DOMAIN + "GetInfoServlet").post(formBody).build();
        HttpUtil.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                model.setNetInfo(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    model.setState(Objects.requireNonNull(response.body()).string());
                } catch (JsonSyntaxException e) {
                    model.setNetInfo(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    void cancelCloud(String tel, String pass) {

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("tel", tel);
        builder.add("pass", pass);
        FormBody formBody = builder.build();
        Request request = new Request.Builder().url(Domain.DOMAIN + "DeleteServlet").post(formBody).build();
        HttpUtil.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                model.setNetInfo(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String msg = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject().get("msg").getAsString();
                    model.setNetInfo(msg);
                } catch (JsonSyntaxException | IOException e) {
                    model.setNetInfo(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });


    }
}
