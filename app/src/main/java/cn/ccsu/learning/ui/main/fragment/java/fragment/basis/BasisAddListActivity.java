package cn.ccsu.learning.ui.main.fragment.java.fragment.basis;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
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
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.custom.FullyGridLayoutManager;
import cn.ccsu.learning.custom.NumberProgressBar;
import cn.ccsu.learning.entity.UserBean;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetResultCode;
import cn.ccsu.learning.net.NetUtil;
import cn.ccsu.learning.ui.login.LoginActivity;
import cn.ccsu.learning.ui.main.MainActivity;
import cn.ccsu.learning.ui.main.fragment.java.adapter.GridAddImageAdapter;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;
import cn.ccsu.learning.ui.main.fragment.java.bean.FilesBean;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.ETChangedUtlis;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.LogUtil;
import cn.ccsu.learning.utils.PictureSelectUtils;
import cn.ccsu.learning.utils.SPUtil;
import cn.ccsu.learning.utils.ToastUtil;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 新增基础知识
 *
 * 模块
 */
public class BasisAddListActivity extends BaseActivity implements GridAddImageAdapter.GridImageListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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

    //附件
    private ArrayList<String> FilePaths = new ArrayList<>();
    //Adapter
    private GridAddImageAdapter mImageAdapter;//资质证书适配器
    //
    private List<LocalMedia> mCerifiImages = new ArrayList<>();//图片集合
    //图片最大选择数量
    private static final int LOCALEMED_RETURN_CODE = 0x559;//图片选择返回Code
    private static final int CERIFI_SELECT_MAX_NUMBER = 0b1001;//9最大的选择数量
    //上传成功的文件集合
    private List<FilesBean> mOkFiles = new ArrayList<>();

    private BasisBean.ListsBean mListsBean;//上一级相关
    private String mIdStrID = "";//Mid

    public static void actionStart(Activity activity, BasisBean.ListsBean mListsBean, String mIdStrID, int code) {
        Intent intent = new Intent(activity, BasisAddListActivity.class);
        intent.putExtra("mListsBean", mListsBean);
        intent.putExtra("mIdStrID", mIdStrID);
        activity.startActivityForResult(intent, code);
    }


    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_basis_add_list;
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initToolbarBlackNav();
        setToolbarTitle("新增");
        ETChangedUtlis.EditTextChangedListener(etTitle, ivTitleClose);
        ETChangedUtlis.EditTextChangedListener(etContent, ivContentClose);
        initRecyclerView();
    }

    private void initRecyclerView() {
        //初始化资质证书选择
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mImageAdapter = new GridAddImageAdapter(this, this);
        mImageAdapter.setList(mCerifiImages);
        mImageAdapter.setSelectMax(CERIFI_SELECT_MAX_NUMBER);//资质证书上传图片数量：9张。
        mRecyclerView.setAdapter(mImageAdapter);
    }


    @OnClick({R.id.tv_add_attachment, R.id.bt_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_add_attachment:
                FilePickerBuilder.getInstance()
                        .setMaxCount(99)
                        .setSelectedFiles(FilePaths)
                        .setActivityTheme(R.style.LibAppTheme)
                        .setActivityTitle("选择附件")
                        .enableImagePicker(false)//禁止选择图片
                        .pickFile(this);
                break;
            case R.id.bt_add:
                if (TextUtils.isEmpty(etTitle.getText().toString())) {
                    ToastUtil.showToast("请添加标题");
                    return;
                }
                if (TextUtils.isEmpty(etContent.getText().toString())) {
                    ToastUtil.showToast("请添加内容");
                    return;
                }
                if (mCerifiImages.size() == 0) {
                    ToastUtil.showToast("请选择图片");
                    return;
                }
                if (FilePaths.size() == 0) {
                    ToastUtil.showToast("请添加附件");
                    return;
                }
                upLoadImageData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //附件
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == RESULT_OK && data != null) {
                    if (!FilePaths.isEmpty()) {
                        FilePaths.clear();
                    }
                    FilePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    String lengstr = "";
                    for (String s : FilePaths) {
                        String[] fileNames = s.split("/");
                        lengstr = lengstr + fileNames[fileNames.length - 1] + "\n";
                    }
                    tvAddAttachment.setText("共" + FilePaths.size() + "个文件:\n" + lengstr);
                }
                break;
            case LOCALEMED_RETURN_CODE://资质证书
                mCerifiImages.clear();
                mCerifiImages.addAll(PictureSelector.obtainMultipleResult(data));
                mImageAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        if (mCerifiImages.size() > 0) {
            LocalMedia media = mCerifiImages.get(position);
            String pictureType = media.getPictureType();
            int mediaType = PictureMimeType.pictureToVideo(pictureType);
            switch (mediaType) {
                case 1:
                    // 预览图片 可自定长按保存路径
                    PictureSelector.create(this).themeStyle(R.style.picture_PWJ_style).openExternalPreview(position, mCerifiImages);
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
                PictureConfig.MULTIPLE, mCerifiImages, LOCALEMED_RETURN_CODE);
    }

    @Override
    public void onDelPicClick(List<LocalMedia> list) {
        mCerifiImages = list;
    }

    /**
     * 上传图片
     */
    private String NET_IMAGE_TAG = "upload_images";

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

        List<File> files = new ArrayList<>();
        for (LocalMedia image : mCerifiImages) {
            files.add(new File(image.getPath()));
        }
        for (String fj : FilePaths) {
            files.add(new File(fj));
        }
        OkGo.<String>post(NetApi.RESOURCE_FILE_UPLOAD)
                .tag(NET_IMAGE_TAG)
                .addFileParams("files", files)
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
                        String downloadLength = Formatter.formatFileSize(BasisAddListActivity.this, progress.currentSize);
                        String totalLength = Formatter.formatFileSize(BasisAddListActivity.this, progress.totalSize);
                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String speed = Formatter.formatFileSize(BasisAddListActivity.this, progress.speed);
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


    /**
     * 新增父级列表
     */
    private void addRes() {
        Map<String, Object> httpParams = new HashMap();
        httpParams.put("mId", mIdStrID);
        httpParams.put("createUser", User.getInstance().getUserId());
        httpParams.put("resTitle", etTitle.getText().toString());
        httpParams.put("resContent", etContent.getText().toString());
        if (!mIdStrID.equals("6")){
            httpParams.put("pId", mListsBean.getResId());
        }

        httpParams.put("files", mOkFiles);

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
                setResult(RESULT_OK);
                finish();
            }
        });
    }

}
