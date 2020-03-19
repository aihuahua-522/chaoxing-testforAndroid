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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huahua.chaoxing.R;
import com.huahua.chaoxing.bean.ClassBean;
import com.huahua.chaoxing.bean.PicBean;
import com.huahua.chaoxing.databinding.FragmentMyClassListBinding;
import com.huahua.chaoxing.userinfo.ui.main.PageViewModel;
import com.huahua.chaoxing.util.SPUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
        classBeans.clear();
        new Thread(() -> {
            String getClassUrl = "http://mooc1-2.chaoxing.com/visit/courses";
            String getName = "http://i.chaoxing.com/base";

            try {
                if (viewModel == null) {
                    viewModel = ViewModelProviders.of(requireActivity()).get(PageViewModel.class);
                    return;
                }
                Map<String, String> cookies = viewModel.getCookies();
                Elements select = Jsoup.connect(getName).cookies(cookies).timeout(30000).get().select(".user-name");
                String text = select != null ? select.text() : "获取失败";
                System.out.println("name = " + text);
                viewModel.setTemp("name", text);

                Document classDocument = Jsoup.connect(getClassUrl).timeout(30000).cookies(cookies).get();

                if (classDocument == null) {
                    requireActivity().runOnUiThread(() -> Toasty.error(requireActivity(), "未检测到课程信息").show());
                    return;
                }

                if (classDocument.title().contains("用户登录")) {
                    requireActivity().runOnUiThread(() -> Toasty.info(requireActivity(), "cookies失效,请重新登录").show());
                    return;
                }

                Elements classElements = classDocument.select(".ulDiv > ul > li[style]");
                if (classElements == null) {
                    requireActivity().runOnUiThread(() -> Toasty.error(requireActivity(), "classElements未空").show());
                    return;
                }
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

                    ///mycourse/studentcourse?courseId=206190313&clazzid=12344429&vc=1&cpi=68128013&enc=1700c5697f8739971e4cb50b63adfc86
                    //https://mooc1-1.chaoxing.com/work/getAllWork?classId=21340000&courseId=210431164&isdisplaytable=2&mooc=1&ut=s&enc=c8ba685957e54c6b39b88798da1fa240&cpi=68128013&openc=5f049b99df4aab447690238b323f0329


                    // 作业地址
                    String workUrl = "https://mooc1-1.chaoxing.com/" + classElement.select(" li > div.Mcon1img.httpsClass > a:nth-child(1)").attr("href");


                    classBean.setWorkUrl(workUrl);
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
                    viewModel.setTemp("work", SPUtils.get(requireActivity(), "work", false));
                    viewModel.setTemp("answer", SPUtils.get(requireActivity(), "answer", false));
                    //System.out.println("数据条数" + classBeans.size());
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toasty.error(requireActivity(), Objects.requireNonNull(e.getMessage())).show());
            }
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
