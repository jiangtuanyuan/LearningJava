package cn.ccsu.learning.ui.main.fragment.java.fragment.basis;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.custom.FullyGridLayoutManager;
import cn.ccsu.learning.ui.main.fragment.java.adapter.GridAddImageAdapter;
import cn.ccsu.learning.utils.PictureSelectUtils;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

/**
 * 新增基础知识
 * jiang
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
    @BindView(R.id.bt_login)
    Button btLogin;
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

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BasisAddListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_basis_add_list;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initToolbarBlackNav();
        setToolbarTitle("新增");
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


    @OnClick({R.id.tv_add_attachment, R.id.bt_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            default:
            case R.id.tv_add_attachment:
                FilePickerBuilder.getInstance()
                        .setMaxCount(1)
                        .setSelectedFiles(FilePaths)
                        .setActivityTheme(R.style.LibAppTheme)
                        .setActivityTitle("选择附件")
                        .enableImagePicker(false)//禁止选择图片
                        .pickFile(this);
                break;
            case R.id.bt_login:
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
                        lengstr = lengstr + fileNames[fileNames.length - 1] + ",";
                    }
                    tvAddAttachment.setText("共" + FilePaths.size() + "个文件:" + lengstr);
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
}
