package cn.ccsu.learning.ui.main.fragment.java.fragment;

import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;


/**
 * 进阶知识
 */
public class JavaFragmentAdvance extends BaseFragment {
    private static final String ARG_MSG = "java_advance";

    public static JavaFragmentAdvance newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        JavaFragmentAdvance fragment = new JavaFragmentAdvance();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_java_advance;
    }

    @Override
    protected void setFragmentData() {

    }
}