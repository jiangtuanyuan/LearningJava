package cn.ccsu.learning.ui.main.fragment.java.fragment;

import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;


/**
 * 实例教程
 */
public class JavaFragmentInstance extends BaseFragment {
    private static final String ARG_MSG = "java_instance";

    public static JavaFragmentInstance newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        JavaFragmentInstance fragment = new JavaFragmentInstance();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_java_instance;
    }

    @Override
    protected void setFragmentData() {

    }
}