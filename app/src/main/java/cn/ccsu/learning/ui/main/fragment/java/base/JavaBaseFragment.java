package cn.ccsu.learning.ui.main.fragment.java.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.hd.commonmodule.utils.FastJsonUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetUtil;
import cn.ccsu.learning.ui.main.fragment.java.adapter.BasisListAdapter;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 基类fragment
 * jiang
 */
public abstract class JavaBaseFragment extends Fragment {
    private LoadingPager mLoadingPager;
    protected Context mContext = null;
    //自己修改
    public int pageNum = 0;//分页要查询的数据当前页码，默认为1
    public String pageSize = "10";//分页每页查询的数据量，默认10

    public String mIdStr = "mId";
    public String pageNumStr = "pageNum";
    public String pageSizeStr = "pageSize";


    private CompositeDisposable mCompositeDisposable;//Rxjava订阅

    private KProgressHUD kProgressHUD;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            mContext = getContext();
            mLoadingPager = new LoadingPager(mContext) {
                @Override
                public LoadedResult initData() {
                    return JavaBaseFragment.this.initData();
                }

                @Override
                public View initSuccessView() {
                    return JavaBaseFragment.this.initSuccessView();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLoadingPager.triggerLoadData();
        return mLoadingPager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除所有未完成的订阅 防止内存泄漏
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    /**
     * 添加订阅
     *
     * @param disposable
     */
    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    //加载对话框相关
    public KProgressHUD getProgressDialog() {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        } else {
            kProgressHUD = new KProgressHUD(mContext);
        }
        return kProgressHUD;
    }

    public void showProgressDialog(String message) {
        if (kProgressHUD == null) {
            kProgressHUD = new KProgressHUD(mContext);
        }
        kProgressHUD.setLabel(message);
        if (!kProgressHUD.isShowing()) {
            kProgressHUD.show();
        }
    }

    public void showProgressDialog(String message, boolean cancelable) {
        if (kProgressHUD == null) {
            kProgressHUD = new KProgressHUD(mContext);
        }
        kProgressHUD.setCancellable(cancelable);
        kProgressHUD.setLabel(message);
        if (!kProgressHUD.isShowing()) {
            kProgressHUD.show();
        }
    }

    public void closeProgressDialog() {
        if (kProgressHUD != null && kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }

    public abstract LoadingPager.LoadedResult initData();

    public abstract View initSuccessView();

    public abstract void getData(boolean refresh);

    /**
     * @des 校验请求回来的数据
     */
    public LoadingPager.LoadedResult checkResultData(Object resObj) {
        if (resObj == null) {
            return LoadingPager.LoadedResult.EMPTY;
        }
        //resObj -->List
        if (resObj instanceof List) {
            if (((List) resObj).size() == 0) {
                return LoadingPager.LoadedResult.EMPTY;
            }
        }
        //resObj -->Map
        if (resObj instanceof Map) {
            if (((Map) resObj).size() == 0) {
                return LoadingPager.LoadedResult.EMPTY;
            }
        }
        return LoadingPager.LoadedResult.SUCCESS;
    }

    /**
     * 新增一个大栏目
     */
    public void showAddListDialog(String mId, RefreshLayout refreshLayout) {
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_add_basis_item, null);
        final TextView mTvTitle = view.findViewById(R.id.tv_title);
        final EditText mEtTitle = view.findViewById(R.id.et_title);
        final EditText mDetails = view.findViewById(R.id.et_details);
        mTvTitle.setText("新增");
        Dialog scooldialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(mEtTitle.getText().toString()) || TextUtils.isEmpty(mDetails.getText().toString())) {
                            ToastUtil.showToast("标题和介绍不能为空..");
                            IOSDialogUtils.NoCosleDialog(dialog, false);
                        } else {
                            addRes(mId, refreshLayout, mEtTitle.getText().toString(), mDetails.getText().toString(), dialog);
                            IOSDialogUtils.NoCosleDialog(dialog, true);
                        }
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
     * 新增父级列表
     */
    private void addRes(String mId, RefreshLayout refreshLayout, String resTitle, String resContent, DialogInterface dialog) {
        Map<String, Object> httpParams = new HashMap();
        httpParams.put("mId", mId);
        httpParams.put("createUser", User.getInstance().getUserId());
        httpParams.put("resTitle", resTitle);
        httpParams.put("resContent", resContent);

        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);

        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.RESOURCE_ADD, requestBody, new NetUtil.DataListener() {
            @Override
            public void showDialogLoading() {
                showProgressDialog("新增中,请稍后..");
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
                refreshLayout.autoRefresh();
            }
        });
    }


    /**
     * 删除弹出框
     *
     * @param mSumList
     * @param postion
     * @param mBasisListAdapter
     */
    public void showDetailsDialog(List<BasisBean.ListsBean> mSumList, int postion, BasisListAdapter mBasisListAdapter) {
        Dialog scooldialog = new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("是否删除:" + mSumList.get(postion).getTitle())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteResID(mSumList, postion, mBasisListAdapter);
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
     * 删除父级的资源
     *
     * @param mSumList
     * @param postion
     * @param mBasisListAdapter
     */
    private void deleteResID(List<BasisBean.ListsBean> mSumList, int postion, BasisListAdapter mBasisListAdapter) {
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
                mBasisListAdapter.notifyItemRemoved(postion);
                mBasisListAdapter.notifyItemRangeChanged(postion, mSumList.size() - postion);
            }
        });
    }


}

