package cn.ccsu.learning.ui.main.fragment.mine.logs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.db.DataDBUtils;
import cn.ccsu.learning.db.ShowLogs;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.BasisDetailsActivity;
import cn.ccsu.learning.utils.ToastUtil;

/**
 * 浏览记录
 */
public class MyLogActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.pager_empty_iv)
    ImageView pagerEmptyIv;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;

    private LogListAdapter mLogListAdapter;
    private List<ShowLogs> mSumList = new ArrayList<>();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MyLogActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_my_log;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolbarBlackNav();
        setToolbarTitle("浏览记录");
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mLogListAdapter = new LogListAdapter(mSumList);
        mLogListAdapter.setOnItemChildClickListener(this);
        mRecyclerview.setAdapter(mLogListAdapter);
        getDBData();
    }

    private void getDBData() {
        mSumList.clear();
        mSumList.addAll(DataDBUtils.getUserShowLogsAll(User.getInstance().getUserId()));
        if (mSumList.size() == 0) {
            pagerEmptyIv.setVisibility(View.VISIBLE);

            mRecyclerview.setVisibility(View.GONE);
            progressbar.setVisibility(View.GONE);

        } else {
            mRecyclerview.setVisibility(View.VISIBLE);

            pagerEmptyIv.setVisibility(View.GONE);
            progressbar.setVisibility(View.GONE);
            mLogListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.iv_delete:
                ShowLogs delogs = mSumList.get(position);
                if (delogs.delete() > 0) {
                    ToastUtil.showToast("删除记录成功!");
                    getDBData();
                } else {
                    ToastUtil.showToast("删除记录失败,请重试!");
                }
                break;
            case R.id.ll_layout:
                ShowLogs logs = mSumList.get(position);
                BasisBean.ListsBean mListsBean = new BasisBean.ListsBean();

                mListsBean.setResId(logs.getResId());//资源ID
                mListsBean.setUserName(logs.getUserName());
                mListsBean.setUserRealname(logs.getUserRealname());
                mListsBean.setResTitle(logs.getResTitle());
                mListsBean.setResContent(logs.getResContent());
                mListsBean.setIsLeaf(logs.getIsLeaf());
                mListsBean.setIsDel(logs.getIsDel());
                mListsBean.setCreateTime(logs.getCreateTime());
                mListsBean.setUpdateTime(logs.getUpdateTime());
                mListsBean.setPid(logs.getPid());
                mListsBean.setTitle(logs.getTitle());
                mListsBean.setMid(logs.getMid());

                BasisDetailsActivity.actionStart(this, mListsBean, mSumList.get(position).getmIdStrID());
                break;
            default:
                break;
        }
    }
}
