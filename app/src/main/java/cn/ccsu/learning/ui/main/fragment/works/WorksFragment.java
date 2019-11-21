package cn.ccsu.learning.ui.main.fragment.works;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.utils.FastJsonUtils;
import com.hd.commonmodule.utils.ThreadPoolProxyFactory;
import com.lzy.okgo.OkGo;
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
import cn.ccsu.learning.app.MyApp;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetResultCode;
import cn.ccsu.learning.ui.main.fragment.java.adapter.BasisListAdapter;
import cn.ccsu.learning.ui.main.fragment.java.base.JavaBaseFragment;
import cn.ccsu.learning.ui.main.fragment.java.base.LoadingPager;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.BasisAddListActivity;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.BasisDetailsActivity;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.ToastUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作品展示
 */
public class WorksFragment extends JavaBaseFragment implements OnRefreshListener, OnLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener {
    private static final String ARG_MSG = "java_works";
    //1.找到 xml 布局文件里面的所有控件
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.smartrefreshlayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    private BasisListAdapter mBasisListAdapter;

    private List<BasisBean.ListsBean> mSumList = new ArrayList<>();
    private BasisBean mBasisBean;

    public static WorksFragment newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        WorksFragment fragment = new WorksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public LoadingPager.LoadedResult initData() {
        try {
            //2.初始化数据  请求后台获取数据
            getData(true);
            return checkResultData(mSumList);
        } catch (Exception e) {
            e.printStackTrace();
            return LoadingPager.LoadedResult.ERROR;
        }
    }

    @Override
    public View initSuccessView() {
        //3.渲染数据到控件上面
        View view = LayoutInflater.from(MyApp.getContext()).inflate(R.layout.fragment_works, null, false);
        ButterKnife.bind(this, view);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(MyApp.getContext()));
        mBasisListAdapter = new BasisListAdapter(mSumList);
        mBasisListAdapter.setOnItemChildClickListener(this);
        mRecyclerview.setAdapter(mBasisListAdapter);
        mBasisListAdapter.notifyDataSetChanged();
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
        if (User.getInstance().getRid().equals("1")) {
            ivAdd.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void getData(boolean isRefresh) {
        try {
            if (pageNum < 0) {
                pageNum = 0;
            }
            //同步请求
            Map<String, Object> httpParams = new HashMap();
            if (isRefresh) {
                httpParams.put(pageNumStr, "0");
            } else {
                httpParams.put(pageNumStr, String.valueOf(mSumList.size()));
            }
            httpParams.put(pageSizeStr, pageSize);
            httpParams.put(mIdStr, "6");
            Gson gson = new Gson();
            String jsonstr = gson.toJson(httpParams);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);
            Response response = OkGo.post(NetApi.RESOURCE_LIST).upRequestBody(requestBody).execute();
            if (response.isSuccessful()) {
                String str = response.body().string();
                if (!TextUtils.isEmpty(str)) {
                    Logger.json(str);
                    JSONObject jsonObject = new JSONObject(str);
                    int code = jsonObject.optInt(Constant.CODE);
                    String info = jsonObject.optString(Constant.INFO);
                    String data = jsonObject.optString(Constant.DATA);
                    if (code == NetResultCode.CODE200.getCode()) {
                        mBasisBean = FastJsonUtils.toBean(data, BasisBean.class);
                        if (isRefresh) {
                            mSumList.clear();
                        }
                        if (mBasisBean != null && mBasisBean.getLists() != null) {
                            mSumList.addAll(mBasisBean.getLists());
                        } else {

                            ToastUtil.showToast(Constant.DATA_ERROR_PARSING);
                        }
                    } else {

                        ToastUtil.showToast(NetResultCode.getCode(code).getDesc());
                    }
                } else {

                    ToastUtil.showToast(Constant.DATA_ERROR_SERVER);
                }
            } else {

                ToastUtil.showToast(Constant.DATA_ERROR_NET);
            }
        } catch (Exception e) {

            e.printStackTrace();
            ToastUtil.showToast(Constant.DATA_ERROR_SERVER_RETRY);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().remove(mloadRefreshRunnable);
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().remove(mloadMoreRunnable);
    }
    //添加的点击事件
    @OnClick(R.id.iv_add)
    public void onViewClicked() {
        if (mSumList.size() > 0) {
            BasisAddListActivity.actionStart(getActivity(), mSumList.get(0), "6", 625);
        }else {
            //BasisBean.ListsBean listsBean=new BasisBean.ListsBean();
        }
        //showAddListDialog("6", mSmartRefreshLayout);
    }
   //加载的子线程
    private Runnable mloadMoreRunnable = new Runnable() {
        @Override
        public void run() {

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
    //刷新的子线程
    private Runnable mloadRefreshRunnable = new Runnable() {
        @Override
        public void run() {

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

    //每一个子项的点击事件
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.iv_delete:
                showDetailsDialog(mSumList, position, mBasisListAdapter);
                break;
            case R.id.ll_layout:
                BasisDetailsActivity.actionStart(getContext(), mSumList.get(position), "6");
                break;
            default:
                break;
        }
    }

}