package cn.ccsu.learning.ui.main.fragment.java.fragment.basis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.utils.FastJsonUtils;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetResultCode;
import cn.ccsu.learning.net.NetUtil;
import cn.ccsu.learning.ui.main.fragment.java.adapter.BasisListAdapter;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.adapter.BasisDetailsListAdapter;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 基础知识的列表
 * []
 */
public class BasisListActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.smartrefreshlayout)
    SmartRefreshLayout smartrefreshlayout;

    private BasisDetailsListAdapter mBasisDetailsListAdapter;
    private List<BasisBean.ListsBean> mSumList = new ArrayList<>();

    private BasisBean.ListsBean mListsBean;//上一级相关
    private String mTitle = "";//标题
    private String mIdStrID = "";//mid  1 2 3 4 5

    private BasisBean mBasisBean;//当前的

    private int ADD_RETURN_CODE = 0X653;

    public static void actionStart(Context context, BasisBean.ListsBean mListsBean, String mIdStrID, String mTitle) {
        Intent intent = new Intent(context, BasisListActivity.class);
        intent.putExtra("mListsBean", mListsBean);
        intent.putExtra("mTitle", mTitle);
        intent.putExtra("mIdStrID", mIdStrID);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_basis_list;
    }

    @Override
    protected void initVariables() {
        if (getIntent() != null) {
            mListsBean = getIntent().getParcelableExtra("mListsBean");
            mTitle = getIntent().getStringExtra("mTitle");
            mIdStrID = getIntent().getStringExtra("mIdStrID");
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (mListsBean == null) {
            ToastUtil.showToast("数据异常！");
            finish();
            return;
        }
        initToolbarBlackNav();
        setToolbarTitle(mTitle);

        //设置数据
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mBasisDetailsListAdapter = new BasisDetailsListAdapter(mSumList);
        mBasisDetailsListAdapter.setOnItemChildClickListener(this);
        mRecyclerview.setAdapter(mBasisDetailsListAdapter);
        mBasisDetailsListAdapter.notifyDataSetChanged();
        smartrefreshlayout.setOnRefreshListener(this);
        smartrefreshlayout.setOnLoadMoreListener(this);
        if (User.getInstance().getRid().equals("1")) {
            ivAdd.setVisibility(View.GONE);
        }else {
            ivAdd.setVisibility(View.VISIBLE);
        }
        getData(true);
    }

    /**
     * 获取数据
     *
     * @param isRefresh
     */
    public void getData(boolean isRefresh) {
        Map<String, Object> httpParams = new HashMap();
        if (isRefresh) {
            httpParams.put(pageNumStr, "0");
        } else {
            httpParams.put(pageNumStr, String.valueOf(mSumList.size()));
        }
        httpParams.put(pageSizeStr, pageSize);
        httpParams.put(mIdStr, mIdStrID);
        httpParams.put(pIdStr, mListsBean.getResId());//resId

        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);

        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.RESOURCE_LIST, requestBody, new NetUtil.DataListener() {
            @Override
            public void showDialogLoading() {
                //showProgressDialog("加载中", false);
            }

            @Override
            public void onSubScribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();

            }

            @Override
            public void onFaild() {
                closeProgressDialog();
            }

            @Override
            public void dissDialogmissLoad() {
                closeProgressDialog();
            }

            @Override
            public void onSuccess(String info, String data) {
                mBasisBean = FastJsonUtils.toBean(data, BasisBean.class);
                if (isRefresh) {
                    mSumList.clear();
                }
                if (mBasisBean != null && mBasisBean.getLists() != null) {
                    mSumList.addAll(mBasisBean.getLists());
                } else {
                    ToastUtil.showToast(Constant.DATA_ERROR_PARSING);
                }
                if (mSumList.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                    mRecyclerview.setVisibility(View.GONE);

                } else {
                    tvNoData.setVisibility(View.GONE);
                    mRecyclerview.setVisibility(View.VISIBLE);
                    mBasisDetailsListAdapter.notifyDataSetChanged();
                }


            }
        });


    }

    //新增
    @OnClick(R.id.iv_add)
    public void onViewClicked() {
        BasisAddListActivity.actionStart(BasisListActivity.this, mListsBean, mIdStrID, ADD_RETURN_CODE);
    }


    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        getData(false);
        refreshLayout.finishLoadMore(1700);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getData(true);
        refreshLayout.finishRefresh(1700);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.iv_delete:
                showDetailsDialog(position);
                break;
            case R.id.ll_layout:
                BasisDetailsActivity.actionStart(BasisListActivity.this, mSumList.get(position), mIdStrID,ADD_RETURN_CODE);
                break;
            default:
                break;
        }
    }

    /**
     * 删除弹出框
     *
     * @param postion
     */
    public void showDetailsDialog(int postion) {
        Dialog scooldialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否删除:" + mSumList.get(postion).getTitle())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteResID(postion);
                        IOSDialogUtils.NoCosleDialog(dialog, true);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IOSDialogUtils.NoCosleDialog(dialog, true);
                    }
                })
                .setCancelable(true)
                .create();
        Window window = scooldialog.getWindow();
        window.setWindowAnimations(R.style.BaseDialogWindowStyle);
        scooldialog.setCancelable(false);
        scooldialog.show();
    }

    /**
     * 删除子级的资源
     *
     * @param postion
     */
    private void deleteResID(int postion) {
        Map<String, Object> httpParams = new HashMap();
        httpParams.put("resId", mSumList.get(postion).getResId());
        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);

        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.RESOURCE_DELETE, requestBody, new NetUtil.DataListener() {
            @Override
            public void showDialogLoading() {
                showProgressDialog("删除中,请稍后..", false);
            }

            @Override
            public void onSubScribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
            }

            @Override
            public void onFaild() {
                closeProgressDialog();
            }

            @Override
            public void dissDialogmissLoad() {
                closeProgressDialog();
            }

            @Override
            public void onSuccess(String info, String data) {
                ToastUtil.showToast(data);
                //移除相关
                mSumList.remove(postion);
                mBasisDetailsListAdapter.notifyItemRemoved(postion);
                mBasisDetailsListAdapter.notifyItemRangeChanged(postion, mSumList.size() - postion);
                if (mSumList.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                    mRecyclerview.setVisibility(View.GONE);

                } else {
                    tvNoData.setVisibility(View.GONE);
                    mRecyclerview.setVisibility(View.VISIBLE);
                    mBasisDetailsListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //确定完成同步
        if (requestCode == ADD_RETURN_CODE && resultCode == RESULT_OK) {
            smartrefreshlayout.autoRefresh();
        }
    }
}

