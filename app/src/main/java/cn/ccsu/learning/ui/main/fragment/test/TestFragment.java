package cn.ccsu.learning.ui.main.fragment.test;

import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;

/**
 * 在线测试
 */
public class TestFragment extends BaseFragment {
    private static final String ARG_MSG = "java_test";

    public static TestFragment newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_test;
    }

    @Override
    protected void setFragmentData() {

    }
}