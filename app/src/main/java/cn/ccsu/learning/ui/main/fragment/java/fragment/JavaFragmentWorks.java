package cn.ccsu.learning.ui.main.fragment.java.fragment;

import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;
import cn.ccsu.learning.ui.main.fragment.java.JavaFragment;


/**
 * 学生作品展示
 */
public class JavaFragmentWorks extends BaseFragment {
    private static final String ARG_MSG = "java_works";
    public static JavaFragmentWorks newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        JavaFragmentWorks fragment = new JavaFragmentWorks();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_java_works;
    }

    @Override
    protected void setFragmentData() {

    }
}