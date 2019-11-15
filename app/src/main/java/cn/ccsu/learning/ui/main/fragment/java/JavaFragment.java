package cn.ccsu.learning.ui.main.fragment.java;


import android.os.Bundle;
import android.os.Parcelable;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseFragment;
import cn.ccsu.learning.ui.main.fragment.java.fragment.JavaFragmentAdvance;
import cn.ccsu.learning.ui.main.fragment.java.fragment.JavaFragmentBasis;
import cn.ccsu.learning.ui.main.fragment.java.fragment.JavaFragmentExpand;
import cn.ccsu.learning.ui.main.fragment.java.fragment.JavaFragmentInstance;
import cn.ccsu.learning.ui.main.fragment.java.fragment.JavaFragmentResources;
import cn.ccsu.learning.utils.LogUtil;

public class JavaFragment extends BaseFragment {
    private static final String ARG_MSG = "java_msg";
    @BindView(R.id.ll_layout)
    LinearLayout ll_layout;
    @BindView(R.id.tablatout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    public ViewPager mViewPager;

    public JavaFragmentBasis mJavaFragmentBasis;//基础知识
    public JavaFragmentAdvance mJavaFragmentAdvance;//进阶知识
    public JavaFragmentInstance mJavaFragmentInstance;//实例教程
    public JavaFragmentExpand mJavaFragmentExpand;//拓展知识

    public JavaFragmentResources mJavaFragmentResources;//相关资源

    private List<Fragment> mFragmentList = new ArrayList<>();
    public DemandAdapter mDemandAdapter;

    public static JavaFragment newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        JavaFragment fragment = new JavaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_java;
    }

    @Override
    protected void setFragmentData() {
        setAdapterStatusBar(ll_layout);
        initFragment();
    }

    /**
     * 加载Fragment
     */
    private void initFragment() {
        mJavaFragmentBasis = JavaFragmentBasis.newInstance("");
        mJavaFragmentAdvance = JavaFragmentAdvance.newInstance("");
        mJavaFragmentInstance = JavaFragmentInstance.newInstance("");
        mJavaFragmentExpand = JavaFragmentExpand.newInstance("");
        mJavaFragmentResources = JavaFragmentResources.newInstance("");

        mFragmentList.clear();
        //基础知识
        mFragmentList.add(mJavaFragmentBasis);
        //进阶知识
        mFragmentList.add(mJavaFragmentAdvance);
        //扩展知识
        mFragmentList.add(mJavaFragmentExpand);

        //实例教程
        mFragmentList.add(mJavaFragmentInstance);
        //资源下载
        mFragmentList.add(mJavaFragmentResources);

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mDemandAdapter = new DemandAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mDemandAdapter);
        mViewPager.setOffscreenPageLimit(4);//预加载3个

        //TabLayout加载viewpager
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("基础知识");
        mTabLayout.getTabAt(1).setText("进阶知识");
        mTabLayout.getTabAt(2).setText("拓展知识");

        mTabLayout.getTabAt(3).setText("实例教程");
        mTabLayout.getTabAt(4).setText("相关资源");

        //监听ViewPager页面的切换
        final TaskVpChangeListener taskVpChangeListener = new TaskVpChangeListener();
        mViewPager.addOnPageChangeListener(taskVpChangeListener);
        mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                taskVpChangeListener.onPageSelected(0);
                mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * VP的滑动事件
     */
    class TaskVpChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //滑动的位置 position
            LogUtil.e("JavaFragment", "滑动到了:" + position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * VP适配
     */
    public class DemandAdapter extends FragmentStatePagerAdapter {
        public DemandAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        //嵌套 不保存状态
        @Override
        public Parcelable saveState() {
            return null;
        }

        //刷新指定位置的Fragment
        public void notifyFragmentByPosition(int position) {
            notifyDataSetChanged();
        }
    }
}
