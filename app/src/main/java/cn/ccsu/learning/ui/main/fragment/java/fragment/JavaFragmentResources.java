package cn.ccsu.learning.ui.main.fragment.java.fragment;

import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;


/**
 * 相关资源
 */
public class JavaFragmentResources extends BaseFragment {
    private static final String ARG_MSG = "java_resources";

    public static JavaFragmentResources newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        JavaFragmentResources fragment = new JavaFragmentResources();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_java_resources;
    }

    @Override
    protected void setFragmentData() {

    }
}