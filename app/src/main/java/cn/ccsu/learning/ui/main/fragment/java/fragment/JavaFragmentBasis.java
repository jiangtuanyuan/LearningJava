package cn.ccsu.learning.ui.main.fragment.java.fragment;

import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;


/**
 * 基础知识
 */
public class JavaFragmentBasis extends BaseFragment {
    private static final String ARG_MSG = "java_basis";

    public static JavaFragmentBasis newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        JavaFragmentBasis fragment = new JavaFragmentBasis();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_java_basis;
    }

    @Override
    protected void setFragmentData() {

    }
}