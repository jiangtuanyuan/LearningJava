package cn.ccsu.learning.ui.main.fragment.java.fragment;

import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;


/**
 * 拓展知识
 */
public class JavaFragmentExpand extends BaseFragment {
    private static final String ARG_MSG = "java_expand";

    public static JavaFragmentExpand newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        JavaFragmentExpand fragment = new JavaFragmentExpand();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_java_expand;
    }

    @Override
    protected void setFragmentData() {

    }
}