package cn.ccsu.learning.ui.main.fragment.test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hd.commonmodule.utils.FastJsonUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetUtil;
import cn.ccsu.learning.ui.main.fragment.test.adapter.TestDetailsAdapter;
import cn.ccsu.learning.ui.main.fragment.test.bean.TestBean;
import cn.ccsu.learning.ui.main.fragment.test.bean.TestDetailsBean;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TestDetailsActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.smartrefreshlayout)
    SmartRefreshLayout smartrefreshlayout;
    @BindView(R.id.bt_commit)
    Button btCommit;
    @BindView(R.id.bt_again)
    Button btAgain;
    @BindView(R.id.tv_titme)
    TextView tvTitme;

    private TestDetailsAdapter mTestDetailsAdapter;
    private List<TestDetailsBean> mSumList = new ArrayList<>();

    private TestBean mTestBean;

    public static void actionStart(Context context, TestBean mTestBean) {
        Intent intent = new Intent(context, TestDetailsActivity.class);
        intent.putExtra("mTestBean", mTestBean);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_test_details;
    }

    @Override
    protected void initVariables() {
        if (getIntent() != null) {
            mTestBean = getIntent().getParcelableExtra("mTestBean");
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (mTestBean == null) {
            ToastUtil.showToast("数据异常!");
            finish();
            return;
        }
        initToolbarBlackNav();
        setToolbarTitle(mTestBean.getTypeName());
        smartrefreshlayout.setEnableRefresh(false);//禁止刷新
        smartrefreshlayout.setEnableLoadMore(false);

        //设置数据
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mTestDetailsAdapter = new TestDetailsAdapter(mSumList);
        //mTestDetailsAdapter.setOnItemChildClickListener(this);
        mRecyclerview.setAdapter(mTestDetailsAdapter);
        mRecyclerview.setItemViewCacheSize(500);
        mTestDetailsAdapter.notifyDataSetChanged();
        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        Map<String, Object> httpParams = new HashMap();
        httpParams.put("typeId", mTestBean.getTypeId());
        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);

        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.EXAM_TYPE, requestBody, new NetUtil.DataListener() {
            @Override
            public void showDialogLoading() {
                showProgressDialog("加载,请稍后..", false);
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
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    mSumList.clear();
                    TestDetailsBean bean;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        bean = FastJsonUtils.toBean(jsonArray.optString(i), TestDetailsBean.class);
                        mSumList.add(bean);
                    }
                    if (mSumList.size() == 0) {
                        mRecyclerview.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerview.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        mTestDetailsAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast("数据解析异常!");
                }
            }
        });
    }

    @OnClick({R.id.bt_again, R.id.bt_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_again:
                AlertDialog.Builder Agbuilder = new AlertDialog.Builder(this);
                Agbuilder.setTitle("提示");
                Agbuilder.setMessage("是否重新测试!");
                Agbuilder.setCancelable(false);
                Agbuilder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Agbuilder.setPositiveButton("重测", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getData();
                        dialog.dismiss();
                    }
                });
                Agbuilder.show();
                break;
            case R.id.bt_commit:
                //判断是否所有都已经选中
                if (!checkIsOK()) {
                    ToastUtil.showToast("您还有未选择答案的题目!");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("是否交卷!");
                builder.setCancelable(false);
                builder.setNegativeButton("不交了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("交卷", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commitData();
                        dialog.dismiss();
                    }
                });
                builder.show();

                break;
            default:
                break;
        }
    }

    private boolean checkIsOK() {
        boolean tf = true;
        for (TestDetailsBean bean : mSumList) {
            if (bean.getCheckString().equals("N")) {
                tf = false;
                break;
            }
        }
        return tf;
    }

    private void commitData() {
        showProgressDialog("交卷中..", true);
        int sum = 0;
        int my_sum = 0;
        for (TestDetailsBean bean : mSumList) {
            bean.setCommit(true);
            sum += Integer.parseInt(bean.getExGrade());
            if (bean.getCheckString().equals(bean.getExAnswer())) {
                my_sum += Integer.parseInt(bean.getExGrade());
            }
        }
        mTestDetailsAdapter.notifyDataSetChanged();
        // 显示正确答案
        tvTitme.setText("共" + sum + "分  您此次得分" + my_sum + "分(" + getStringS(my_sum) + ")");
        btCommit.setEnabled(false);
        btCommit.setBackgroundResource(R.color.bg_gray);
        closeProgressDialog();
    }

    private String getStringS(int mySum) {
        if (0 <= mySum && mySum < 60) {
            return "不及格";
        } else if (60 <= mySum && mySum < 70) {
            return "良好";
        } else if (70 <= mySum && mySum < 80) {
            return "一般";
        } else if (80 <= mySum && mySum < 90) {
            return "优秀";
        } else if (mySum > 90) {
            return "完美";
        } else {
            return "完美";
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("不继续测试了吗!");
            builder.setCancelable(false);
            builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("不测了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
