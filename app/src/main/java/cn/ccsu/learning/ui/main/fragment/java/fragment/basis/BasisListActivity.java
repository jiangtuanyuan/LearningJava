package cn.ccsu.learning.ui.main.fragment.java.fragment.basis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.MyApp;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.adapter.BasisDetailsListAdapter;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.ToastUtil;

/**
 * 基础知识的列表
 * []
 */
public class BasisListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.iv_add)
    ImageView ivAdd;

    private BasisDetailsListAdapter mBasisDetailsListAdapter;
    private List<BasisBean> mSumList = new ArrayList<>();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BasisListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_basis_list;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolbarBlackNav();
        setToolbarTitle("基础知识");
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color, R.color.black, R.color.color_B3);
        swipeRefreshLayout.setDistanceToTriggerSync(50);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        swipeRefreshLayout.setSize(10);
        //设置刷新监听
        swipeRefreshLayout.setOnRefreshListener(this);


        //设置数据
        mRecyclerview.setLayoutManager(new LinearLayoutManager(MyApp.getContext()));
        mBasisDetailsListAdapter = new BasisDetailsListAdapter(mSumList);
        mBasisDetailsListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_delete:
                        showDetailsDialog("语法");
                        break;
                    case R.id.ll_layout:
                        BasisDetailsActivity.actionStart(BasisListActivity.this);
                        break;
                    default:
                        break;
                }
            }
        });
        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //如果处于第一个  让swipeRefreshLayout获取焦点
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mRecyclerview.setAdapter(mBasisDetailsListAdapter);
        mBasisDetailsListAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(true);
        getData(true);
    }

    @Override
    public void onRefresh() {
        getData(true);
    }

    public void getData(boolean refresh) {
        mSumList.clear();
        BasisBean taskExeBean;
        for (int i = 0; i < 20; i++) {
            taskExeBean = new BasisBean();
            mSumList.add(taskExeBean);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);//设置关闭刷新
                mBasisDetailsListAdapter.notifyDataSetChanged();

                tvNoData.setVisibility(View.GONE);
                mRecyclerview.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }

    @OnClick(R.id.iv_add)
    public void onViewClicked() {
        BasisAddListActivity.actionStart(BasisListActivity.this);
    }

    /**
     * 删除
     */
    private void showDetailsDialog(String title) {
        Dialog scooldialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否删除:" + title)
                .setPositiveButton("确定", (dialog, id) -> {
                    ToastUtil.showToast("删除");
                    IOSDialogUtils.NoCosleDialog(dialog, false);
                })
                .setNegativeButton("取消", (dialog, id) -> IOSDialogUtils.NoCosleDialog(dialog, true))
                .setCancelable(true)
                .create();
        Window window = scooldialog.getWindow();
        window.setWindowAnimations(R.style.BaseDialogWindowStyle);
        scooldialog.setCancelable(false);
        scooldialog.show();
    }
}

