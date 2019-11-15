package cn.ccsu.learning.ui.main.fragment.works;
import android.os.Bundle;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;
public class WorksFragment extends BaseFragment {
    private static final String ARG_MSG = "java_works";

    public static WorksFragment newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        WorksFragment fragment = new WorksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_works;
    }

    @Override
    protected void setFragmentData() {

    }
}