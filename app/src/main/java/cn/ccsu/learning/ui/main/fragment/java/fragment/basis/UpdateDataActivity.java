package cn.ccsu.learning.ui.main.fragment.java.fragment.basis;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.utils.FastJsonUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
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
import cn.ccsu.learning.custom.FullyGridLayoutManager;
import cn.ccsu.learning.custom.NumberProgressBar;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetResultCode;
import cn.ccsu.learning.net.NetUtil;
import cn.ccsu.learning.ui.main.fragment.java.adapter.GridAddImageAdapter;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.bean.FilesBean;
import cn.ccsu.learning.ui.main.fragment.java.bean.FilesDetailsBean;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.adapter.BasisDownLoadItemAdapter;
import cn.ccsu.learning.ui.main.fragment.java.fragment.basis.adapter.BasisFileUpdateItemAdapter;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.ETChangedUtlis;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.LogUtil;
import cn.ccsu.learning.utils.PictureSelectUtils;
import cn.ccsu.learning.utils.ToastUtil;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 修改
 */
public class UpdateDataActivity extends BaseActivity implements GridAddImageAdapter.GridImageListener {
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.iv_title_close)
    ImageView ivTitleClose;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.iv_content_close)
    ImageView ivContentClose;
    @BindView(R.id.tv_add_attachment)
    TextView tvAddAttachment;
    @BindView(R.id.bt_add)
    Button btAdd;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.recyclerView_file)
    RecyclerView recyclerViewFile;

    //上一级的数据
    private BasisBean.ListsBean mListsBean;//上一级相关
    private String mIdStrID = "";//Mid
    private List<FilesDetailsBean> mImages;
    private List<FilesDetailsBean> mFiles;

    //图片Adapter
    private GridAddImageAdapter mImageAdapter;
    private List<LocalMedia> mShowImages = new ArrayList<>();//显示的图片集合
    private static final int CERIFI_SELECT_MAX_NUMBER = 0b1001;//最大的选择数量 9
    private static final int LOCALEMED_RETURN_CODE = 0x559;//图片选择返回Code

    //附件
    private BasisFileUpdateItemAdapter mBasisFileUpdateItemAdapter;
    private ArrayList<String> FilePaths = new ArrayList<>();


    public static void actionStart(Activity activity, BasisBean.ListsBean mListsBean, String mIdStrID, List<FilesDetailsBean> mImages, List<FilesDetailsBean> mFiles, int returnCode) {
        Intent intent = new Intent(activity, UpdateDataActivity.class);
        intent.putExtra("mListsBean", mListsBean);
        intent.putExtra("mIdStrID", mIdStrID);
        //图片
        intent.putParcelableArrayListExtra("mImages", (ArrayList<? extends Parcelable>) mImages);
        //附件
        intent.putParcelableArrayListExtra("mFiles", (ArrayList<? extends Parcelable>) mFiles);
        activity.startActivityForResult(intent, returnCode);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_update_data;
    }

    @Override
    protected void initVariables() {
        if (getIntent() != null) {
            mListsBean = getIntent().getParcelableExtra("mListsBean");
            mIdStrID = getIntent().getStringExtra("mIdStrID");
            mImages = getIntent().getParcelableArrayListExtra("mImages");
            mFiles = getIntent().getParcelableArrayListExtra("mFiles");
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (mListsBean == null || mImages == null || mFiles == null) {
            ToastUtil.showToast("数据异常！");
            finish();
            return;
        }
        initToolbarBlackNav();
        setToolbarTitle("修改" + mListsBean.getTitle());
        initDataView();
    }

    private void initDataView() {
        ETChangedUtlis.EditTextChangedListener(etTitle, ivTitleClose);
        ETChangedUtlis.EditTextChangedListener(etContent, ivContentClose);
        //标题
        etTitle.setText(mListsBean.getTitle());
        //内容
        etContent.setText(mListsBean.getResContent());
        //图片
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mImageAdapter = new GridAddImageAdapter(this, this);
        mImageAdapter.setList(mShowImages);
        mImageAdapter.setSelectMax(CERIFI_SELECT_MAX_NUMBER);//资质证书上传图片数量：5张。
        mRecyclerView.setAdapter(mImageAdapter);
        LocalMedia media;
        mShowImages.clear();
        for (FilesDetailsBean d : mImages) {
            media = new LocalMedia();
            String path = NetApi.HIP + d.getFilePath()
                    .replace("D:", "")
                    .replace("\\", "/");
            media.setPath(path);

            media.setCutPath(d.getFilePath());//服务器的文件路径
            media.setPictureType(d.getFileAlias());//别名
            media.setCompressPath(d.getFileName());//文件名

            media.setMimeType(1);
            media.setDuration(-99);//这边标识是代表线上的图片
            mShowImages.add(media);
        }
        mImageAdapter.notifyDataSetChanged();
        //附件
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewFile.setLayoutManager(mLinearLayoutManager);
        //scrollView 嵌套RecyclerView滑动卡顿处理
        recyclerViewFile.setHasFixedSize(true);
        recyclerViewFile.setNestedScrollingEnabled(false);
        recyclerViewFile.setFocusable(false);
        mBasisFileUpdateItemAdapter = new BasisFileUpdateItemAdapter(mFiles);
        mBasisFileUpdateItemAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mFiles.remove(position);
                mBasisFileUpdateItemAdapter.notifyItemRemoved(position);
                mBasisFileUpdateItemAdapter.notifyItemRangeChanged(position, mFiles.size() - position);
            }
        });
        recyclerViewFile.setAdapter(mBasisFileUpdateItemAdapter);
        mBasisFileUpdateItemAdapter.notifyDataSetChanged();


    }

    @Override
    public void onItemClick(int position, View v) {
        if (mShowImages.size() > 0) {
            LocalMedia media = mShowImages.get(position);
            String pictureType = media.getPictureType();
            int mediaType = PictureMimeType.pictureToVideo(pictureType);
            switch (mediaType) {
                case 1:
                    // 预览图片 可自定长按保存路径
                    PictureSelector.create(this)
                            .themeStyle(R.style.picture_PWJ_style)
                            .openExternalPreview(position, mShowImages);
                    break;
                case 2:
                    // 预览视频
                    PictureSelector.create(this).externalPictureVideo(media.getPath());
                    break;
                case 3:
                    // 预览音频
                    PictureSelector.create(this).externalPictureAudio(media.getPath());
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onAddPicClick() {
        PictureSelectUtils.pictureSelectorFormPhoto(this,
                PictureMimeType.ofImage(), CERIFI_SELECT_MAX_NUMBER, false,
                PictureConfig.MULTIPLE, mShowImages, LOCALEMED_RETURN_CODE);

    }

    @Override
    public void onDelPicClick(List<LocalMedia> list) {
        mShowImages = list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FilePickerConst.REQUEST_CODE_DOC:
                    if (resultCode == RESULT_OK && data != null) {
                        if (!FilePaths.isEmpty()) {
                            FilePaths.clear();
                        }
                        FilePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                        FilesDetailsBean bean;
                        for (String s : FilePaths) {
                            bean = new FilesDetailsBean();
                            String[] fileNames = s.split("/");
                            String name = fileNames[fileNames.length - 1];//文件名
                            if (!isContine(name)) {
                                //重新设置进来
                                bean.setFileAlias(name);
                                bean.setFilePath(s);//
                                bean.setFileId("-99");//说明是本地新增的
                                mFiles.add(bean);
                            } else {
                                ToastUtil.showToast(name + "已经存在,无法继续添加!");
                            }
                        }
                        mBasisFileUpdateItemAdapter.notifyDataSetChanged();
                    }
                    break;
                case LOCALEMED_RETURN_CODE://资质证书
                    mShowImages = PictureSelector.obtainMultipleResult(data);
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < mShowImages.size(); i++) {
                        list.add(mShowImages.get(i).getPath());
                    }
                    mImageAdapter.setList(mShowImages);
                    mImageAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 判断选择的文件名是否重复
     *
     * @param fifleName
     * @return
     */
    private boolean isContine(String fifleName) {
        boolean tf = false;
        for (FilesDetailsBean bean : mFiles) {
            if (bean.getFileAlias().equals(fifleName)) {
                tf = true;
                break;
            }
        }
        return tf;
    }

    /**
     * 按返回键,增加退出提示
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("您未完成修改,确定退出码");
            builder.setCancelable(false);
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.tv_add_attachment, R.id.bt_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_add_attachment://新增附件
                FilePickerBuilder.getInstance()
                        .setMaxCount(99 - mFiles.size())
                        .setSelectedFiles(FilePaths)
                        .setActivityTheme(R.style.LibAppTheme)
                        .setActivityTitle("选择附件")
                        .enableImagePicker(false)//禁止选择图片
                        .pickFile(this);
                break;
            case R.id.bt_add://修改
                if (checkUploadData()) {
                    //判断有没有选择图片
                    if (mShowImages.size() == 0) {
                        ToastUtil.showToast("请选择图片!");
                        return;
                    }
                    if (mFiles.size() == 0) {
                        ToastUtil.showToast("请选择附件!");
                        return;
                    }
                    //判断图片有没有修改
                    boolean imageTF = isCheckImage();
                    boolean filesTF = isCheckFiles();
                    if (imageTF && filesTF) {
                        //说明附件和图片都没更改  直接提交 标题和内容的 数据
                        commitImageFile();
                    } else {
                        commitNoImageFile();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 判断图片是否全是服务器上面的图片
     *
     * @return
     */
    private boolean isCheckImage() {
        boolean tf = true;
        for (LocalMedia media : mShowImages) {
            if (media.getDuration() != -99) {
                //说明 不是服务器上面的图片
                tf = false;
                break;
            }
        }
        return tf;
    }

    /**
     * 判断附件是否全是服务器上面的附件
     *
     * @return
     */
    private boolean isCheckFiles() {
        boolean tf = true;
        for (FilesDetailsBean bean : mFiles) {
            if (bean.getFileId().equals("-99")) {
                //说明 不是服务器上面的图片
                tf = false;
                break;
            }
        }
        return tf;
    }


    /**
     * 修改了 图片 或者附件  找出来 修改的文件
     */
    private List<File> isUploadList = new ArrayList<>();//需要上传的文件

    private void commitNoImageFile() {
        mOkFiles.clear();
        isUploadList.clear();
        //找出图片是 -99的 是服务器上面的图片
        FilesBean filesBean;
        for (LocalMedia media : mShowImages) {
            if (media.getDuration() == -99) {
                //说明 是服务器上面的图片
                filesBean = new FilesBean();
                filesBean.setFilePath(media.getCutPath());
                filesBean.setFileAlias(media.getPictureType());
                filesBean.setFileName(media.getCompressPath());
                mOkFiles.add(filesBean);
            } else {
                isUploadList.add(new File(media.getPath()));
            }
        }
        //找出附件不是 -99 的
        for (FilesDetailsBean bean : mFiles) {
            if (bean.getFileId().equals("-99")) {
                isUploadList.add(new File(bean.getFilePath()));
            } else {
                //网络的的附件
                filesBean = new FilesBean();
                filesBean.setFileAlias(bean.getFileAlias());
                filesBean.setFilePath(bean.getFilePath());
                filesBean.setFileName(bean.getFileName());
                mOkFiles.add(filesBean);
            }
        }

        if (isUploadList.size() > 0) {
            //上传文件
            upLoadImageData();
        } else {
            ToastUtil.showToast("文件差异计算出错,请重试!");
        }
    }

    /**
     * 既没有改动图片 也没有改动附件 上传参数
     */
    private void commitImageFile() {
        mOkFiles.clear();
        FilesBean filesBean;

        for (LocalMedia media : mShowImages) {
            if (media.getDuration() == -99) {
                //说明 是服务器上面的图片
                filesBean = new FilesBean();
                filesBean.setFilePath(media.getCutPath());
                filesBean.setFileAlias(media.getPictureType());
                filesBean.setFileName(media.getCompressPath());
                mOkFiles.add(filesBean);
            }
        }

       /* for (FilesDetailsBean bean : mImages) {
            filesBean = new FilesBean();
            filesBean.setFileAlias(bean.getFileAlias());
            filesBean.setFilePath(bean.getFilePath());
            filesBean.setFileName(bean.getFileName());
            mOkFiles.add(filesBean);
        }*/
        for (FilesDetailsBean bean : mFiles) {
            filesBean = new FilesBean();
            filesBean.setFileAlias(bean.getFileAlias());
            filesBean.setFilePath(bean.getFilePath());
            filesBean.setFileName(bean.getFileName());
            mOkFiles.add(filesBean);
        }
        addRes();
    }

    /**
     * 检验上传参数
     *
     * @return
     */
    private boolean checkUploadData() {
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            ToastUtil.showToast("请输入标题!");
            return false;
        }
        if (TextUtils.isEmpty(etContent.getText().toString())) {
            ToastUtil.showToast("请输入内容！");
            return false;
        }
        return true;
    }


    /**
     * 上传图片
     */
    private String NET_IMAGE_TAG = "upload_images";

    //上传成功的文件集合
    private List<FilesBean> mOkFiles = new ArrayList<>();

    public void upLoadImageData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_upload_progress, null);
        final TextView downStatus = (TextView) view.findViewById(R.id.down_status);
        final TextView tvDownloadSize = (TextView) view.findViewById(R.id.downloadSize);
        final TextView tvProgress = (TextView) view.findViewById(R.id.tvProgress);
        final TextView tvNetSpeed = (TextView) view.findViewById(R.id.netSpeed);
        final NumberProgressBar pbProgress = (NumberProgressBar) view.findViewById(R.id.pbProgress);
        final TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);

        final NumberFormat numberFormat;
        downStatus.setText("上传中..");

        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);
        builder.setView(view);
        builder.setCancelable(false);
        builder.create();
        final AlertDialog alertDialog = builder.show();
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkGo.getInstance().cancelTag(NET_IMAGE_TAG);
                alertDialog.dismiss();
                ToastUtil.showToast("取消成功!");
            }
        });

        OkGo.<String>post(NetApi.RESOURCE_FILE_UPLOAD)
                .tag(NET_IMAGE_TAG)
                .addFileParams("files", isUploadList)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> stringResponse) {
                        alertDialog.dismiss();
                        try {
                            closeProgressDialog();
                            if (stringResponse.isSuccessful()) {
                                String s = stringResponse.body();
                                LogUtil.e("请求信息", s);
                                Logger.json(s);
                                if (!TextUtils.isEmpty(s)) {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int code = jsonObject.optInt(Constant.CODE);
                                    // String info = jsonObject.optString(Constant.INFO);
                                    JSONObject data = new JSONObject(jsonObject.optString(Constant.DATA));
                                    if (code == NetResultCode.CODE200.getCode()) {
                                        JSONArray jsonArray = data.optJSONArray("files");
                                        FilesBean filesBean;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            filesBean = FastJsonUtils.toBean(jsonArray.optString(i), FilesBean.class);
                                            mOkFiles.add(filesBean);
                                        }
                                        if (mOkFiles.size() > 0) {
                                            addRes();
                                        } else {
                                            ToastUtil.showToast("上传成功数量为0,请重试!");
                                        }
                                    } else {
                                        ToastUtil.showToast(jsonObject.optString("info"));
                                    }
                                } else {
                                    ToastUtil.showToast("未获取到返回数据!");
                                }
                            } else {
                                ToastUtil.showToast("请求失败!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast("解析异常!");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        alertDialog.dismiss();
                        closeProgressDialog();
                        showImageDiaLog("网络不佳，点击重试");
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        String downloadLength = Formatter.formatFileSize(UpdateDataActivity.this, progress.currentSize);
                        String totalLength = Formatter.formatFileSize(UpdateDataActivity.this, progress.totalSize);
                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String speed = Formatter.formatFileSize(UpdateDataActivity.this, progress.speed);
                        tvNetSpeed.setText(String.format("%s/s", speed));
                        tvProgress.setText(numberFormat.format(progress.fraction));
                        pbProgress.setMax(10000);
                        pbProgress.setProgress((int) (progress.fraction * 10000));
                    }
                });
    }

    /**
     * 重试
     *
     * @param msg
     */
    private void showImageDiaLog(String msg) {
        Dialog scooldialog = new android.app.AlertDialog.Builder(this)
                .setTitle("网络不佳")
                .setMessage("网络不佳,点击重试!")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        upLoadImageData();
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

    private void addRes() {
        Map<String, Object> httpParams = new HashMap();
        httpParams.put("mId", mIdStrID);
        httpParams.put("resId", mListsBean.getResId());
        httpParams.put("createUser", User.getInstance().getUserId());
        httpParams.put("resTitle", etTitle.getText().toString());
        httpParams.put("resContent", etContent.getText().toString());
        httpParams.put("pId", mListsBean.getPid());

        httpParams.put("files", mOkFiles);

        Gson gson = new Gson();
        String jsonstr = gson.toJson(httpParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonstr);

        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.RESOURCE_UPDATE, requestBody, new NetUtil.DataListener() {
            @Override
            public void showDialogLoading() {
                showProgressDialog("修改中,请稍后..");
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
                closeProgressDialog();
                ToastUtil.showToast(data);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
