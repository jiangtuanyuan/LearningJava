package cn.ccsu.learning.ui.main.fragment.mine.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.utils.FastJsonUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;

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
import cn.ccsu.learning.utils.ETChangedUtlis;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 用户管理
 * 学生: 不能新增
 * 老师: 能新增
 */
public class UserManageActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rb_studio)
    RadioButton rbStudio;
    @BindView(R.id.rb_teacher)
    RadioButton rbTeacher;
    @BindView(R.id.rg_line)
    RadioGroup rgLine;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.pager_empty_iv)
    ImageView pagerEmptyIv;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.iv_add_teacher)
    ImageView ivAddTeacher;
    @BindView(R.id.search_edt)
    EditText mSearchEdt;//搜索框
    @BindView(R.id.search_edt_clear)
    ImageView mSearchEdtClear;//搜索框的清除按钮

    private List<UserBean> mSumList = new ArrayList<>();

    private List<UserBean> mSumStuList = new ArrayList<>();//学生
    private List<UserBean> mSumTeaList = new ArrayList<>();//教师

    private UserListAdapter mStuUserListAdapter;
    private UserListAdapter mTeaUserListAdapter;


    private List<UserBean> mStuShowList = new ArrayList<>();//学生目前显示的
    private List<UserBean> mTeaShowList = new ArrayList<>();//教师目前显示的

    private int isCheckStu = 1;//1 2

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UserManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_user_manage;
    }

    @Override
    protected void initVariables() {

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolbarBlackNav();
        setToolbarTitle("用户管理");
        //设置数据
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        getUserData();
        ETChangedUtlis.EditTextChangedListener(mSearchEdt, mSearchEdtClear);
    }


    @OnClick({R.id.rb_studio, R.id.rb_teacher, R.id.iv_add_teacher})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_studio://学生
                ivAddTeacher.setVisibility(View.GONE);
                mRecyclerview.setAdapter(mStuUserListAdapter);
                isCheckStu = 1;
                break;
            case R.id.rb_teacher://老师
                ivAddTeacher.setVisibility(View.VISIBLE);
                mRecyclerview.setAdapter(mTeaUserListAdapter);

                isCheckStu = 2;
                break;
            case R.id.iv_add_teacher://添加老师
                AddTeacherActivity.actionStart(this, ADD_RETURN_CODE);
                break;
            default:
                break;
        }
    }

    /**
     * 获取所有用户信息
     */
    private void getUserData() {
        Map<String, Object> httpParams = new HashMap();
        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);
        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.USER_ALL, requestBody, new NetUtil.DataListener() {
            @Override
            public void showDialogLoading() {
                showProgressDialog("加载中,请稍后..", false);
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
                    UserBean bean;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        bean = FastJsonUtils.toBean(jsonArray.optString(i), UserBean.class);
                        mSumList.add(bean);
                    }

                    if (mSumList.size() == 0) {
                        mRecyclerview.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);
                        pagerEmptyIv.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerview.setVisibility(View.VISIBLE);
                        pagerEmptyIv.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);
                        initDataView();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast("数据解析异常!");
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void initDataView() {
        mSumStuList.clear();
        mSumTeaList.clear();

        mStuShowList.clear();
        mTeaShowList.clear();
        for (UserBean bean : mSumList) {
            if (bean.getrId().equals("1")) {
                //学生
                mSumStuList.add(bean);
            } else if (bean.getrId().equals("2")) {
                //老师
                mSumTeaList.add(bean);
            }
        }
        mStuShowList.addAll(mSumStuList);
        mTeaShowList.addAll(mSumTeaList);

        mStuUserListAdapter = new UserListAdapter(mStuShowList);
        mStuUserListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                showDetailsDialog(mStuShowList.get(position));

            }
        });
        mRecyclerview.setAdapter(mStuUserListAdapter);
        rbStudio.setChecked(true);

        mTeaUserListAdapter = new UserListAdapter(mTeaShowList);
        mTeaUserListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                showDetailsDialog(mTeaShowList.get(position));
            }
        });


        //添加监听
        RxTextView.textChanges(mSearchEdt)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {

                        if (charSequence.length() > 0) {
                            mSearchEdtClear.setVisibility(View.VISIBLE);
                            if (isCheckStu == 1) {
                                SearchStu(charSequence.toString() + "");
                            } else {
                                SearchTea(charSequence.toString() + "");
                            }
                        } else {
                            if (isCheckStu == 1) {
                                mStuShowList.clear();
                                mStuShowList.addAll(mSumStuList);
                                mStuUserListAdapter.notifyDataSetChanged();
                            } else {
                                mTeaShowList.clear();
                                mTeaShowList.addAll(mSumTeaList);
                                mTeaUserListAdapter.notifyDataSetChanged();
                            }
                            mSearchEdtClear.setVisibility(View.INVISIBLE);

                        }
                    }
                });
    }

    /**
     * 在总数据里面搜索用户名或者电话 mSumList
     */
    private void SearchStu(String search) {
        mStuShowList.clear();
        for (UserBean user : mSumStuList) {
            if (user.getUserName().contains(search) || user.getUserTel().contains(search)) {
                mStuShowList.add(user);
            }
        }
        mStuUserListAdapter.notifyDataSetChanged();
    }

    /**
     * 老师
     *
     * @param search
     */
    private void SearchTea(String search) {
        mTeaShowList.clear();
        for (UserBean user : mSumTeaList) {
            if (user.getUserName().contains(search) || user.getUserTel().contains(search)) {
                mTeaShowList.add(user);
            }
        }
        mTeaUserListAdapter.notifyDataSetChanged();
    }

    /**
     * 删除
     *
     * @param bean
     * @param
     */
    public void showDetailsDialog(UserBean bean) {
        Dialog scooldialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否删除用户:" + bean.getUserName())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteResID(bean);
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
     * 删除
     *
     * @param bean
     * @param
     */
    private void deleteResID(UserBean bean) {
        Map<String, Object> httpParams = new HashMap();
        httpParams.put("userId", bean.getUserId());
        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);

        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.USER_DELATE, requestBody, new NetUtil.DataListener() {
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
                ToastUtil.showToast(info);
                getUserData();
            }
        });
    }

    /**
     * 收到成功新增 刷新列表
     */
    private int ADD_RETURN_CODE = 0X653;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //确定完成同步
        if (requestCode == ADD_RETURN_CODE && resultCode == RESULT_OK) {
            getUserData();
        }
    }
}
