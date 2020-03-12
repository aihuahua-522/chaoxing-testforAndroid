package com.huahua.chaoxing.userinfo.sign;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.ClassBean;
import com.huahua.chaoxing.bean.SignBean;
import com.huahua.chaoxing.util.DateUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SignService extends IntentService {

    private static final String ACTION_SIGN = "com.huahua.startSign";
    private static TextView signLogText;
    private static WindowManager.LayoutParams layoutParams;
    private boolean wantSign = true;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            signLogText.setText(msg.obj.toString());
        }
    };

    public SignService() {
        super("signService");
    }

    public static void startAction(Context context, HashMap<String, String> cookies, HashMap<String, String> temp, ArrayList<ClassBean> classBeans) {
        // 获取WindowManager服务
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        // 设置LayoutParam
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = MATCH_PARENT;
        layoutParams.height = WRAP_CONTENT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        // 将悬浮窗控件添加到WindowManager
        View view = LayoutInflater.from(context).inflate(R.layout.float_view, null);
        view.setAlpha((float) 0.6);
        View floatImg = view.findViewById(R.id.floatImg);
        View signLogMain = view.findViewById(R.id.signLogMain);
        signLogText = view.findViewById(R.id.signLog);
        view.setOnTouchListener(new View.OnTouchListener() {
            private int x;
            private int y;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        layoutParams.x = layoutParams.x + movedX;
                        layoutParams.y = layoutParams.y + movedY;

                        // 更新悬浮窗控件布局
                        if (windowManager != null) {
                            windowManager.updateViewLayout(view, layoutParams);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (signLogMain.getVisibility() == View.VISIBLE) {
                            signLogMain.setVisibility(View.GONE);
                            layoutParams.width = WRAP_CONTENT;
                            if (windowManager != null) {
                                windowManager.updateViewLayout(view, layoutParams);
                            }
                            break;
                        }
                        layoutParams.width = MATCH_PARENT;
                        signLogMain.setVisibility(View.VISIBLE);
                        if (windowManager != null) {
                            windowManager.updateViewLayout(view, layoutParams);
                        }
                    default:
                        break;
                }
                return false;
            }

        });
        windowManager.addView(view, layoutParams);
        Intent intent = new Intent(context, SignService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cookies", cookies);
        bundle.putSerializable("temp", temp);
        bundle.putSerializable("classBeans", classBeans);
        intent.putExtras(bundle);
        intent.setAction(ACTION_SIGN);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            HashMap<String, String> cookies = (HashMap<String, String>) intent.getSerializableExtra("cookies");
            HashMap<String, String> temp = (HashMap<String, String>) intent.getSerializableExtra("temp");
            ArrayList<ClassBean> classBeans = (ArrayList<ClassBean>) intent.getSerializableExtra("classBeans");
            if (ACTION_SIGN.equals(action)) {
                try {
                    handleAction(cookies, temp, classBeans);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void handleAction(HashMap<String, String> cookies, HashMap<String, String> temp, ArrayList<ClassBean> classBeans) throws Exception {
        StringBuilder sb = new StringBuilder();
        while (wantSign) {
            ArrayList<SignBean> signBeans = new ArrayList<>();
            if (classBeans != null && classBeans.size() != 0) {
                sb.append("开始签到").append(DateUtil.getThisTime()).append("\n");
                int success = 0;
                for (int i = 0; i < classBeans.size(); i++) {
                    String url = classBeans.get(i).getUrl();
                    Connection.Response response = Jsoup.connect(url).cookies(cookies).method(Connection.Method.GET).execute();
                    Document document = response.parse();
                    Elements elements = document.select("#startList div .Mct");
                    if (elements == null || elements.size() == 0) {
                        sb.append(classBeans.get(i).getClassName()).append("无签到活动").append("\n");
                        continue;
                    }
                    for (Element ele : elements) {
                        String onclick = ele.attr("onclick");
                        System.out.println(onclick);
                        if (onclick != null && onclick.length() > 0) {
                            String split = onclick.split("\\(")[1];
                            String activeId = split.split(",")[0];
                            System.out.println(split);
                            System.out.println(activeId);
                            System.out.println("保存的数据" + temp.get(activeId));
                            if (temp.get(activeId) != null) {
                                SignBean signBean = new SignBean();
                                signBean.setSignClass(classBeans.get(i).getClassName());
                                signBean.setSignName(classBeans.get(i).getClassmate());
                                success++;
                                signBean.setSignState("签到成功");
                                signBean.setSignTime(ele.select(".Color_Orang").text());
                                signBeans.add(signBean);
                                continue;
                            }
                            String signUrl = "https://mobilelearn.chaoxing.com/pptSign/stuSignajax?name="
                                    + URLDecoder.decode(temp.get("name"))
                                    + "&address="
                                    + temp.get("signPlace")
                                    + "&activeId="
                                    + activeId
                                    + "&uid="
                                    + cookies.get("_uid")
                                    + "&clientip=&latitude=-1&longitude=-1&fid="
                                    + cookies.get("fid")
                                    + "&appType=15&ifTiJiao=1";
                            System.out.println(signUrl);
                            System.out.println("==============" + activeId + "签到中=================");
                            Connection.Response signResponse = Jsoup.connect(signUrl).cookies(cookies).method(Connection.Method.GET).timeout(30000).execute();
                            Element element = signResponse.parse().body();
                            String state = element.getElementsByTag("body").text();
                            System.out.println("签到状态" + state);
                            if ("success".equalsIgnoreCase(state)) {
                                success++;
                            }
                            SignBean signBean = new SignBean();
                            signBean.setSignClass(classBeans.get(i).getClassName());
                            signBean.setSignName(classBeans.get(i).getClassmate());
                            signBean.setSignState(element.getElementsByTag("body").text());
                            signBean.setSignTime(ele.select(".Color_Orang").text());
                            if (signBean.getSignState().equals("您已签到过了")) {
                                temp.put(activeId, "签到成功");
                                success++;
                            }
                            signBeans.add(signBean);
                            Thread.sleep(1000);
                        }
                    }
                }
                for (int i = 0; i < signBeans.size(); i++) {
                    sb.append(signBeans.get(i).getSignClass() + signBeans.get(i).getSignState() + signBeans.get(i).getSignTime()).append("\n");
                }
                sb.append(DateUtil.getThisTime()).append("扫描完成 正在进行的个数").append(signBeans.size()).append("\n").append("成功签到个数").append(success).append("\n");
                sb.append("扫描周期" + Long.parseLong(temp.get("signTime"))).append("s").append("\n");
                Message message = new Message();
                message.obj = sb.toString();
                handler.sendMessage(message);
            }
            Thread.sleep(Long.parseLong(temp.get("signTime")) * 1000);
//            Thread.sleep(6000);


        }
    }

    public void setWantSign(boolean wantSign) {
        this.wantSign = wantSign;
    }

}
