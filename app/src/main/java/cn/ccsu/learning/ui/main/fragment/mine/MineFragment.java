package cn.ccsu.learning.ui.main.fragment.mine;


import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;

/**
 * Description : MineFragment
 *
 * @author yuanyi
 * @date 2019/9/23/023
 * @describe :我的模块
 */


public class MineFragment extends BaseFragment {
    private static final String ARG_MSG = "mine";

    public static MineFragment newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void setFragmentData() {
    }
}
