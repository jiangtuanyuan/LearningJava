package cn.ccsu.learning.ui.main.fragment.java.fragment.basis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.utils.FastJsonUtils;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;
import org.json.JSONException;

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
import cn.ccsu.learning.custom.NumberProgressBar;
import cn.ccsu.learning.db.DataDBUtils;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetUtil;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.bean.FilesDetailsBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.adapter.BasisDownLoadItemAdapter;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.MapTable;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 详情
 */
public class BasisDetailsActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, Toolbar.OnMenuItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.NoScrollGridView)
    NineGridImageView mImagNoScrollGridView;
    @BindView(R.id.tv_filenumber)
    TextView tvFileNumber;
    @BindView(R.id.recyclerView_file)
    RecyclerView recyclerViewFile;

    private BasisBean.ListsBean mListsBean;//上一级相关
    private String mIdStrID = "";//Mid

    private List<FilesDetailsBean> mFileSums = new ArrayList<>();//资源集合

    private BasisDownLoadItemAdapter mBasisDownLoadItemAdapter;

    public static void actionStart(Activity activity, BasisBean.ListsBean mListsBean, String mIdStrID, int returoncode) {
        Intent intent = new Intent(activity, BasisDetailsActivity.class);
        intent.putExtra("mListsBean", mListsBean);
        intent.putExtra("mIdStrID", mIdStrID);
        activity.startActivityForResult(intent, returoncode);
    }

    public static void actionStart(Context constant, BasisBean.ListsBean mListsBean, String mIdStrID) {
        Intent intent = new Intent(constant, BasisDetailsActivity.class);
        intent.putExtra("mListsBean", mListsBean);
        intent.putExtra("mIdStrID", mIdStrID);
        constant.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_basis_details;
    }

    @Override
    protected void initVariables() {
        if (getIntent() != null) {
            mListsBean = getIntent().getParcelableExtra("mListsBean");
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
        //保存到数据库
        try {
            DataDBUtils.addShowLog(mListsBean, mIdStrID, User.getInstance().getUserId());//保存到数据库
        } catch (Exception e) {
            e.printStackTrace();
        }

        initToolbarBlackNav();
        setToolbarTitle(mListsBean.getTitle() + "的详情");
        if (!mIdStrID.equals("6")) {
            //学生作品不修改
            setInflateMenu(R.menu.update, this);
        } else if (!User.getInstance().getRid().equals("1")) {
            if (User.getInstance().getRid().equals("2")) {
                if (mListsBean.getUserName().equals(User.getInstance().getUserName())) {
                    setInflateMenu(R.menu.update, this);
                }
            } else {
                setInflateMenu(R.menu.update, this);
            }
        }else {

        }

        tvTitle.setText(mListsBean.getTitle());
        tvContent.setText(mListsBean.getResContent());
        getresIdFifle();
    }


    /**
     * 获取资源下面的文件
     * 包括图片和文件
     */
    private void getresIdFifle() {
        Map<String, Object> httpParams = new HashMap();
        httpParams.put("resId", mListsBean.getResId());
        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);

        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.RESOURCE_FILE_SHOW, requestBody, new NetUtil.DataListener() {
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
                    mFileSums.clear();
                    FilesDetailsBean detailsBean;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        detailsBean = FastJsonUtils.toBean(jsonArray.optString(i), FilesDetailsBean.class);
                        mFileSums.add(detailsBean);
                    }
                    intDtaView();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToast("数据解析异常!");
                }
            }
        });
    }

    /**
     * 渲染数据
     */
    private List<FilesDetailsBean> mImages = new ArrayList<>();
    private List<FilesDetailsBean> mFiles = new ArrayList<>();

    private void intDtaView() {
        //筛选
        mImages.clear();
        mFiles.clear();
        for (FilesDetailsBean d : mFileSums) {
            if (d.getFilePath().contains("img")
                    || d.getFilePath().contains("png")
                    || d.getFilePath().contains("jpg")
                    || d.getFilePath().contains("jpeg")
                    || d.getFilePath().contains("webp")) {
                //图片
                mImages.add(d);
            } else {
                mFiles.add(d);
            }
        }
        //图片
        mImagNoScrollGridView.setAdapter(mAdapter);
        mImagNoScrollGridView.setGap(8);
        mImagNoScrollGridView.setImagesData(mImages, NineGridImageView.STYLE_GRID);//设置图片数据
        //文件
        tvFileNumber.setText("附件资源(共" + mFiles.size() + "个附件)");
        //设置附件
        initRecyclerview();
    }

    /**
     * 初始化
     */
    private void initRecyclerview() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewFile.setLayoutManager(mLinearLayoutManager);
        //scrollView 嵌套RecyclerView滑动卡顿处理
        recyclerViewFile.setHasFixedSize(true);
        recyclerViewFile.setNestedScrollingEnabled(false);
        recyclerViewFile.setFocusable(false);
        mBasisDownLoadItemAdapter = new BasisDownLoadItemAdapter(mFiles);
        mBasisDownLoadItemAdapter.setOnItemChildClickListener(this);
        recyclerViewFile.setAdapter(mBasisDownLoadItemAdapter);
        mBasisDownLoadItemAdapter.notifyDataSetChanged();
    }


    private NineGridImageViewAdapter<FilesDetailsBean> mAdapter = new NineGridImageViewAdapter<FilesDetailsBean>() {
        @Override
        protected void onDisplayImage(Context context, ImageView imageView, FilesDetailsBean d) {
            //加载图片
            String path = NetApi.HIP + d.getFilePath()
                    .replace("D:", "")
                    .replace("\\", "/");

            Glide.with(context)
                    .load(path)
                    .placeholder(R.mipmap.logo)
                    .error(R.mipmap.logo)
                    .into(imageView);//加载头像
        }

        @Override
        protected ImageView generateImageView(Context context) {
            return super.generateImageView(context);
        }

        @Override
        protected void onItemImageClick(Context context, ImageView imageView, int index, List<FilesDetailsBean> list) {
            ShowImagesActivity.actionStart(BasisDetailsActivity.this, (ArrayList<FilesDetailsBean>) list, index);
        }

        @Override
        protected boolean onItemImageLongClick(Context context, ImageView imageView, int index, List<FilesDetailsBean> list) {
            //ToastUtil.showToast("长按了第" + index + "张的图片!共" + list.size() + "张!");
            return true;
        }
    };

    //选择了哪一项
    private int mSelectPosition = -1;

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        mSelectPosition = position;
        if (view.getId() == R.id.tv_download) {
            String path = NetApi.HIP + mFiles.get(position).getFilePath()
                    .replace("D:", "")
                    .replace("\\", "/");

            //download(path, mFiles.get(position).getFileAlias());
            postDownload(mFiles.get(position).getFileId(), mFiles.get(position).getFilePath(), mFiles.get(position).getFileAlias(), mFiles.get(position).getFileAlias());
        }
    }


    private void download(String url, final String name) {
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


        OkGo.<File>get(url)
                .tag(this)
                .execute(new FileCallback(name) {
                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        downStatus.setText("正在下载:" + name);
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        alertDialog.dismiss();

                        MapTable.openFile(BasisDetailsActivity.this, response.body().getPath());
                    }

                    @Override
                    public void onError(Response<File> response) {
                        alertDialog.dismiss();
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        String downloadLength = Formatter.formatFileSize(BasisDetailsActivity.this, progress.currentSize);
                        String totalLength = Formatter.formatFileSize(BasisDetailsActivity.this, progress.totalSize);
                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String speed = Formatter.formatFileSize(BasisDetailsActivity.this, progress.speed);
                        tvNetSpeed.setText(String.format("%s/s", speed));
                        tvProgress.setText(numberFormat.format(progress.fraction));
                        pbProgress.setMax(10000);
                        pbProgress.setProgress((int) (progress.fraction * 10000));
                    }
                });
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
                        downStatus.setText("正在下载:" + name);
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        alertDialog.dismiss();

                        MapTable.openFile(BasisDetailsActivity.this, response.body().getPath());
                    }

                    @Override
                    public void onError(Response<File> response) {
                        alertDialog.dismiss();
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        String downloadLength = Formatter.formatFileSize(BasisDetailsActivity.this, progress.currentSize);
                        String totalLength = Formatter.formatFileSize(BasisDetailsActivity.this, progress.totalSize);
                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String speed = Formatter.formatFileSize(BasisDetailsActivity.this, progress.speed);
                        tvNetSpeed.setText(String.format("%s/s", speed));
                        tvProgress.setText(numberFormat.format(progress.fraction));
                        pbProgress.setMax(10000);
                        pbProgress.setProgress((int) (progress.fraction * 10000));
                    }
                });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_update://修改
                if (mListsBean != null) {
                    UpdateDataActivity.actionStart(this, mListsBean, mIdStrID, mImages, mFiles, 365);
                } else {
                    ToastUtil.showToast("未获取到数据,无法修改！");
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //确定完成同步
        if (requestCode == 365 && resultCode == RESULT_OK) {
            ToastUtil.showToast("修改成功!");
            setResult(RESULT_OK);
            finish();
        }
    }
}
