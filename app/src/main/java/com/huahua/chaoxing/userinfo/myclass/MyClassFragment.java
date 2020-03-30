package com.huahua.chaoxing.userinfo.myclass;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.CourseBean;
import com.huahua.chaoxing.bean.PicBean;
import com.huahua.chaoxing.databinding.FragmentMyClassListBinding;
import com.huahua.chaoxing.userinfo.ui.main.PageViewModel;
import com.huahua.chaoxing.util.SPUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MyClassFragment extends Fragment {

    // TODO: Customize parameter argument names
    private OnListFragmentInteractionListener mListener;
    private FragmentMyClassListBinding root;
    private PageViewModel viewModel;
    private ArrayList<CourseBean> classBeans = new ArrayList<>();
    private MyItemRecyclerViewAdapter adapter;

    public MyClassFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = DataBindingUtil.inflate(inflater, R.layout.fragment_my_class_list, container, false);
        return root.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set the adapter
        viewModel = ViewModelProviders.of(requireActivity()).get(PageViewModel.class);
        if (root.getRoot() instanceof RecyclerView) {
            Context context = root.getRoot().getContext();
            RecyclerView recyclerView = (RecyclerView) root.getRoot();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyItemRecyclerViewAdapter(classBeans, mListener);
            recyclerView.setAdapter(adapter);
            getAllClass();
        }
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
        viewModel.setTemp("pic", picList);
    }

    private void getAllClass() {
        new Thread(() -> {
            String getName = "http://i.chaoxing.com/base";
            try {
                Map<String, String> cookies = viewModel.getCookies();
                Elements select = Jsoup.connect(getName).cookies(cookies).timeout(30000).get().select(".user-name");
                String text = select != null ? select.text() : "获取失败";
                System.out.println("name = " + text);
                viewModel.setTemp("name", text);
                classBeans = getClassBeans((HashMap<String, String>) cookies);
                requireActivity().runOnUiThread(() -> {
                    viewModel.setTemp("classBeans", classBeans);
                    viewModel.setRefresh();
                    viewModel.setTemp("signPlace", SPUtils.get(requireActivity(), "signPlace", ""));
                    viewModel.setTemp("work", SPUtils.get(requireActivity(), "work", false));
                    viewModel.setTemp("answer", SPUtils.get(requireActivity(), "answer", false));
                    System.out.println("数据条数" + classBeans.size());
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toasty.error(requireActivity(), Objects.requireNonNull(e.getMessage())).show());
            }
        }).start();
    }

    private ArrayList<CourseBean> getClassBeans(HashMap<String, String> cookies) throws Exception {
        // 4. 登录成功-->将课程封装list
        String getClassUrl = "http://mooc1-2.chaoxing.com/visit/courses";
        Document classDocument = Jsoup.connect(getClassUrl).cookies(cookies).method(Connection.Method.GET)
                .timeout(30000).get();
        if (classDocument.title().contains("用户登录")) {
            // TODO 这里可以发送邮件 重新登录
            Toasty.error(requireActivity(), "cookie可能失效,推荐重新提交").show();
        }
        Elements allClassElement = classDocument.select(".ulDiv > ul > li[style]");
        for (Element classElement : allClassElement) {
            String courseId = classElement.select("[name = courseId]").attr("value");
            String href = classElement.select(" .Mcon1img > a").attr("href");
            String enc = href.split("enc=")[1];
            String cpi = href.split("cpi=")[1];
            String classId = classElement.select("[name = classId]").attr("value");
            //课程名
            String courseName = classElement.select(".clearfix > a ").attr("title");
            //班级名
            String className = classElement.select(".Mconright > p ").get(2).attr("title");
            //教师
            String teacher = classElement.select(".Mconright > p ").get(0).attr("title");
            //任务地址
            String signUrl = "https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=" + courseId + "&jclassId=" + classId;

            String workUrl = "https://mooc1-1.chaoxing.com/work/getAllWork?classId=" + classId + "&courseId=" + courseId + "&isdisplaytable=2&mooc=1&ut=s&enc=" + enc + "&cpi=" + cpi;

            classBeans.add(new CourseBean(null, signUrl, courseId, className, courseName, classId, teacher, workUrl));
        }
        requireActivity().runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            adapter.notifyItemChanged(0);
        });
        SPUtils.put(requireActivity(), "class", new Gson().toJson(classBeans));
        return classBeans;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = this::onListFragmentInteraction;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onListFragmentInteraction(CourseBean item) {
        Toasty.info(requireActivity(), item.toString()).show();
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(CourseBean item);
    }
}
