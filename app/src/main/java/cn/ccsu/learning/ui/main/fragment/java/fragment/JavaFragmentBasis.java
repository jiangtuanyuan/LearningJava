package cn.ccsu.learning.ui.main.fragment.java.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.utils.ThreadPoolProxyFactory;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.MyApp;
import cn.ccsu.learning.ui.main.fragment.java.adapter.BasisListAdapter;
import cn.ccsu.learning.ui.main.fragment.java.base.JavaBaseFragment;
import cn.ccsu.learning.ui.main.fragment.java.base.LoadingPager;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.BasisListActivity;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.ToastUtil;
import okhttp3.Response;


/**
 * 基础知识
 */
public class JavaFragmentBasis extends JavaBaseFragment implements OnRefreshListener, OnLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener {
    private static final String ARG_MSG = "java_basis";
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.smartrefreshlayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    private BasisListAdapter mBasisListAdapter;
    private List<BasisBean> mSumList = new ArrayList<>();

    public static JavaFragmentBasis newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        JavaFragmentBasis fragment = new JavaFragmentBasis();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public LoadingPager.LoadedResult initData() {
        try {
            return checkResultData(mSumList);
        } catch (Exception e) {
            e.printStackTrace();
            return LoadingPager.LoadedResult.ERROR;
        }
    }

    @Override
    public View initSuccessView() {
        View view = LayoutInflater.from(MyApp.getContext()).inflate(R.layout.fragment_java_basis, null, false);
        ButterKnife.bind(this, view);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(MyApp.getContext()));
        mBasisListAdapter = new BasisListAdapter(mSumList);
        mBasisListAdapter.setOnItemChildClickListener(this);
        mRecyclerview.setAdapter(mBasisListAdapter);
        mBasisListAdapter.notifyDataSetChanged();
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
        return view;
    }

    @Override
    public void getData(boolean refresh) {

       /* try {
            if (pageNum < 1) {
                pageNum = 1;
            }
            //同步请求
            HttpParams httpParams = new HttpParams();
            httpParams.put(pageNumStr, pageNum);
            httpParams.put(pageSizeStr, pageSize);
            Response response = OkGo.get(NetApi.URL_TASK_GET_EXE_LIST).params(httpParams).execute();
            if (response.isSuccessful()) {
                String str = response.body().string();
                if (!TextUtils.isEmpty(str)) {
                    Logger.json(str);
                    JSONObject jsonObject = new JSONObject(str);
                    int code = jsonObject.optInt(Constant.CODE);
                    String info = jsonObject.optString(Constant.INFO);
                    if (code == NetResultCode.CODE10000.getCode()) {
                        mTaskExeBean = FastJsonUtil.toBean(info, TaskExeBean.class);
                        if (isRefresh) {
                            mSumList.clear();
                        }
                        if (mTaskExeBean != null && mTaskExeBean.getResult() != null) {
                            mSumList.addAll(mTaskExeBean.getResult());
                        } else {
                            --pageSize;
                            ToastUtil.showToast(Constant.DATA_ERROR_PARSING);
                        }
                    } else {
                        --pageNum;
                        ToastUtil.showToast(NetResultCode.getCode(code).getDesc());
                    }
                } else {
                    --pageNum;
                    ToastUtil.showToast(Constant.DATA_ERROR_SERVER);
                }
            } else {
                --pageNum;
                ToastUtil.showToast(Constant.DATA_ERROR_NET);
            }
        } catch (Exception e) {
            --pageNum;
            e.printStackTrace();
            ToastUtil.showToast(Constant.DATA_ERROR_SERVER_RETRY);
        }*/


        mSumList.clear();
        BasisBean taskExeBean;
        for (int i = 0; i < 20; i++) {
            taskExeBean = new BasisBean();
            mSumList.add(taskExeBean);
        }
    }

    @OnClick(R.id.iv_add)
    public void onViewClicked() {
        // ToastUtil.showToast("新增基础知识");
        showAddListDialog();
    }

    private Runnable mloadMoreRunnable = new Runnable() {
        @Override
        public void run() {
            pageNum++;
            getData(false);
            mSmartRefreshLayout.finishLoadMore();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBasisListAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private Runnable mloadRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            pageNum = 1;
            getData(true);
            mSmartRefreshLayout.finishRefresh();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBasisListAdapter.notifyDataSetChanged();
                }
            });
        }
    };


    /**
     * 刷新
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().submit(mloadRefreshRunnable);
    }

    /**
     * 加载
     *
     * @param refreshLayout
     */
    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().submit(mloadMoreRunnable);
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.iv_delete:
                //ToastUtil.showToast("删除");
                showDetailsDialog("Java简介");
                break;
            case R.id.ll_layout:
                BasisListActivity.actionStart(getContext());
                break;
            default:
                break;
        }
    }

    /**
     * 新增一个大栏目
     */
    private void showAddListDialog() {
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_add_basis_item, null);
        final TextView mTvTitle = view.findViewById(R.id.tv_title);
        final EditText mTitle = view.findViewById(R.id.et_title);
        final EditText mDetails = view.findViewById(R.id.et_details);
        mTvTitle.setText("新增基础知识栏目");
        Dialog scooldialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.showToast("标题:" + mTitle.getText().toString());
                        IOSDialogUtils.NoCosleDialog(dialog, false);
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
     * 删除
     */
    private void showDetailsDialog(String title) {
        Dialog scooldialog = new AlertDialog.Builder(getActivity())
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