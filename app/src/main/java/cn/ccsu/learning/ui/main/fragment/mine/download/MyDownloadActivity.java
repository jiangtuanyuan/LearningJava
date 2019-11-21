package cn.ccsu.learning.ui.main.fragment.mine.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.utils.FastJsonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.custom.CustomDialog;
import cn.ccsu.learning.custom.NumberProgressBar;
import cn.ccsu.learning.db.ShowLogs;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetUtil;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.BasisDetailsActivity;
import cn.ccsu.learning.ui.main.fragment.mine.logs.LogListAdapter;
import cn.ccsu.learning.ui.main.fragment.mine.logs.MyLogActivity;
import cn.ccsu.learning.ui.main.fragment.mine.user.UserBean;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.MapTable;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 我的下载
 */
public class MyDownloadActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.pager_empty_iv)
    ImageView pagerEmptyIv;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;


    private List<MyDownLoadBean> mSumList = new ArrayList<>();
    private MyDownloadListAdapter mMyDownloadListAdapter;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MyDownloadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_my_download;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolbarBlackNav();
        setToolbarTitle("我的下载");
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mMyDownloadListAdapter = new MyDownloadListAdapter(mSumList);
        mMyDownloadListAdapter.setOnItemChildClickListener(this);
        mRecyclerview.setAdapter(mMyDownloadListAdapter);
        getMyDownLoad();
    }


    private void getMyDownLoad() {
        mRecyclerview.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        pagerEmptyIv.setVisibility(View.GONE);


        Map<String, Object> httpParams = new HashMap();
        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);
        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.RECORD_FINDALL, requestBody, new NetUtil.DataListener() {
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
                    MyDownLoadBean bean;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        bean = FastJsonUtils.toBean(jsonArray.optString(i), MyDownLoadBean.class);
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
                        mMyDownloadListAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast("数据解析异常!");
                }
            }
        });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.iv_delete:
                showDetails(mSumList.get(position));
                break;
            case R.id.tv_file_name://继续下载
                postDownload(mSumList.get(position).getFileId(), mSumList.get(position).getFilePath(), mSumList.get(position).getFileAlias(), mSumList.get(position).getFileAlias());
                break;
            default:
                break;
        }
    }

    //通过Post 下载附件
    private void postDownload(String fileid, String filePath, String fileAlias, String name) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_down, null);
        final TextView downStatus = (TextView) view.findViewById(R.id.down_status);
        final TextView tvDownloadSize = (TextView) view.findViewById(R.id.downloadSize);
        final TextView tvProgress = (TextView) view.findViewById(R.id.tvProgress);
        final TextView tvNetSpeed = (TextView) view.findViewById(R.id.netSpeed);
        final NumberProgressBar pbProgress = (NumberProgressBar) view.findViewById(R.id.pbProgress);
        final NumberFormat numberFormat;
        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);
        builder.setView(view);
        builder.setCancelable(false);
        builder.create();
        final AlertDialog alertDialog = builder.show();


        Map<String, Object> httpParams = new HashMap();
        httpParams.put("fileId", fileid);
        httpParams.put("filePath", filePath);
        httpParams.put("fileAlias", fileAlias);
        httpParams.put("userId", User.getInstance().getUserId());

        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);

        OkGo.<File>post(NetApi.RESOURCE_FILE_DOWNLOAD)
                .upRequestBody(requestBody)
                .execute(new FileCallback(name) {
                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        downStatus.setText("正在打开:" + name);
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        alertDialog.dismiss();

                        MapTable.openFile(MyDownloadActivity.this, response.body().getPath());
                    }

                    @Override
                    public void onError(Response<File> response) {
                        alertDialog.dismiss();
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        String downloadLength = Formatter.formatFileSize(MyDownloadActivity.this, progress.currentSize);
                        String totalLength = Formatter.formatFileSize(MyDownloadActivity.this, progress.totalSize);
                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String speed = Formatter.formatFileSize(MyDownloadActivity.this, progress.speed);
                        tvNetSpeed.setText(String.format("%s/s", speed));
                        tvProgress.setText(numberFormat.format(progress.fraction));
                        pbProgress.setMax(10000);
                        pbProgress.setProgress((int) (progress.fraction * 10000));
                    }
                });
    }

    private void showDetails(MyDownLoadBean bean) {
        IOSDialogUtils.IOSDialogBean iosDialogBean = new IOSDialogUtils.IOSDialogBean();
        iosDialogBean.setmTitle("温馨提示");
        iosDialogBean.setCancelable(false);
        iosDialogBean.setmMgs("确定删除[" + bean.getFileAlias() + "]码");
        iosDialogBean.setOnButtonClickListener(new IOSDialogUtils.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(CustomDialog dialog) {
                dialog.dismiss();
                DeleteMyDownLoad(bean);
            }

            @Override
            public void onCancelButtonClick(CustomDialog dialog) {
                dialog.dismiss();
            }
        });
        IOSDialogUtils.getInstance().showDialogIOS(this, iosDialogBean);
    }

    private void DeleteMyDownLoad(MyDownLoadBean bean) {
        Map<String, Object> httpParams = new HashMap();
        httpParams.put("dwId", bean.getDwId());
        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);
        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.RECORD_DELETE, requestBody, new NetUtil.DataListener() {
            @Override
            public void showDialogLoading() {
                showProgressDialog("删除中,请稍后..", true);
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
                getMyDownLoad();
            }
        });
    }
}
