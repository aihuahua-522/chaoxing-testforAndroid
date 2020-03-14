package com.huahua.chaoxing.userinfo.myclass;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.ClassBean;
import com.huahua.chaoxing.databinding.FragmentMyClassListBinding;
import com.huahua.chaoxing.userinfo.ui.main.PageViewModel;
import com.huahua.chaoxing.util.SPUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
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
    private ArrayList<ClassBean> classBeans = new ArrayList<>();
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
    }

    private void getAllClass() {
        classBeans.clear();
        new Thread(() -> {
            String getClassUrl = "http://mooc1-2.chaoxing.com/visit/courses";
            String getName = "http://i.chaoxing.com/base";
            Document classDocument = null;
            try {
                if (viewModel == null) {
                    viewModel = ViewModelProviders.of(requireActivity()).get(PageViewModel.class);
                    return;
                }
                Map<String, String> cookies = viewModel.getCookies();
                String text = Jsoup.connect(getName).cookies(cookies).get().select(".user-name").text();
                System.out.println("name = " + text);
                viewModel.setTemp("name", text);
                classDocument = Jsoup.connect(getClassUrl).cookies(cookies).get();
                if (classDocument == null) {
                    Toasty.error(requireActivity(), "未检测到课程信息").show();
                    return;
                }
//                System.out.println(classDocument);
                if (classDocument.title().contains("用户登录")) {
                    Toasty.info(requireActivity(), "cookies失效,请重新登录").show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toasty.error(requireActivity(), Objects.requireNonNull(e.getMessage())).show();
            }
            Elements classElements = classDocument.select(".ulDiv > ul > li[style]");
            for (Element classElement : classElements) {
                ClassBean classBean = new ClassBean();
                String courseId = classElement.select("[name = courseId]").attr("value");
                String classId = classElement.select("[name = classId]").attr("value");
                //课程名
                String className = classElement.select(".clearfix > a ").attr("title");
                //班级名
                String classmate = classElement.select(".Mconright > p ").get(2).attr("title");
                //教师
                String teacher = classElement.select(".Mconright > p ").get(0).attr("title");
                //任务地址
                String url = "https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=" + courseId + "&jclassId=" + classId;
                classBean.setCourseId(courseId);
                classBean.setClassId(classId);
                classBean.setClassName(className);
                classBean.setTeacher(teacher);
                classBean.setUrl(url);
                classBean.setClassmate(classmate);
                classBeans.add(classBean);
            }
            requireActivity().runOnUiThread(() -> {
                adapter.notifyItemChanged(0);
                viewModel.setTemp("classBeans", classBeans);
                viewModel.setRefresh();
                viewModel.setTemp("signPlace", SPUtils.get(requireActivity(), "signPlace", ""));
                //System.out.println("数据条数" + classBeans.size());
            });
        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = this::onListFragmentInteraction;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onListFragmentInteraction(ClassBean item) {
        Toasty.info(requireActivity(), item.toString()).show();
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ClassBean item);
    }
}
