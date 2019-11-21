package cn.ccsu.learning.ui.main.fragment.test;

import android.content.Intent;
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

import org.json.JSONArray;
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
import cn.ccsu.learning.base.BaseFragment;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetResultCode;
import cn.ccsu.learning.ui.main.fragment.java.adapter.BasisListAdapter;
import cn.ccsu.learning.ui.main.fragment.java.base.JavaBaseFragment;
import cn.ccsu.learning.ui.main.fragment.java.base.LoadingPager;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.BasisDetailsActivity;
import cn.ccsu.learning.ui.main.fragment.test.adapter.TestListAdapter;
import cn.ccsu.learning.ui.main.fragment.test.bean.TestBean;
import cn.ccsu.learning.ui.main.fragment.works.WorksFragment;
import cn.ccsu.learning.ui.web.WebViewActivity;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.ToastUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 在线测试
 */
public class TestFragment extends JavaBaseFragment implements OnRefreshListener, OnLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener {
    private static final String ARG_MSG = "java_test";
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.smartrefreshlayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.iv_add)
    ImageView ivAdd;

    private TestListAdapter mTestListAdapter;
    private List<TestBean> mSumList = new ArrayList<>();

    public static TestFragment newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public LoadingPager.LoadedResult initData() {
        try {
            getData(true);
            return checkResultData(mSumList);
        } catch (Exception e) {
            e.printStackTrace();
            return LoadingPager.LoadedResult.ERROR;
        }
    }

    @Override
    public View initSuccessView() {
        View view = LayoutInflater.from(MyApp.getContext()).inflate(R.layout.fragment_test, null, false);
        ButterKnife.bind(this, view);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(MyApp.getContext()));
        mTestListAdapter = new TestListAdapter(mSumList);
        mTestListAdapter.setOnItemChildClickListener(this);
        mRecyclerview.setAdapter(mTestListAdapter);
        mTestListAdapter.notifyDataSetChanged();
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
       /* if (User.getInstance().getRid().equals("1")) {

        }*/
        ivAdd.setVisibility(View.GONE);

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
            //httpParams.put(mIdStr, "6");
            Gson gson = new Gson();
            String jsonstr = gson.toJson(httpParams);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);
            Response response = OkGo.post(NetApi.EXAM_TEST).upRequestBody(requestBody).execute();
            if (response.isSuccessful()) {
                String str = response.body().string();
                if (!TextUtils.isEmpty(str)) {
                    Logger.json(str);
                    JSONObject jsonObject = new JSONObject(str);
                    int code = jsonObject.optInt(Constant.CODE);
                    String info = jsonObject.optString(Constant.INFO);
                    JSONArray data = jsonObject.optJSONArray(Constant.DATA);
                    if (code == NetResultCode.CODE200.getCode()) {
                        if (isRefresh) {
                            mSumList.clear();
                        }
                        mSumList.clear();
                        TestBean bean;
                        for (int i = 0; i < data.length(); i++) {
                            bean = FastJsonUtils.toBean(data.optString(i), TestBean.class);
                            mSumList.add(bean);
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

    @OnClick(R.id.iv_add)
    public void onViewClicked() {
        showAddListDialog("4", mSmartRefreshLayout);
    }

    private Runnable mloadMoreRunnable = new Runnable() {
        @Override
        public void run() {
            getData(false);
            mSmartRefreshLayout.finishLoadMore();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTestListAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private Runnable mloadRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            getData(true);
            mSmartRefreshLayout.finishRefresh();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTestListAdapter.notifyDataSetChanged();
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
            case R.id.tv_start:
                if (!TextUtils.isEmpty(mSumList.get(position).getTypeUrl())) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra("url", mSumList.get(position).getTypeUrl());
                    intent.putExtra("title", mSumList.get(position).getTypeName());
                    getActivity().startActivity(intent);
                } else {
                    TestDetailsActivity.actionStart(getContext(), mSumList.get(position));
                }
                break;
            default:
                break;
        }
    }

}