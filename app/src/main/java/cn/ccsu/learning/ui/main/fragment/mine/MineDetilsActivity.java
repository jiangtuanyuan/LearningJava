package cn.ccsu.learning.ui.main.fragment.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.MyApp;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.custom.CustomDialog;
import cn.ccsu.learning.ui.login.LoginActivity;
import cn.ccsu.learning.utils.ActivityCollector;
import cn.ccsu.learning.utils.ETChangedUtlis;
import cn.ccsu.learning.utils.GlideUtils;
import cn.ccsu.learning.utils.IOSDialogUtils;
import cn.ccsu.learning.utils.LogUtil;
import cn.ccsu.learning.utils.SPUtil;
import cn.ccsu.learning.utils.ToastUtil;
import cn.ccsu.learning.utils.Validator;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人资料详情
 */
public class MineDetilsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.head_iv)
    CircleImageView headIv;
    @BindView(R.id.ll_head_image_layout)
    LinearLayout llHeadImageLayout;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_name_layout)
    LinearLayout llNameLayout;
    @BindView(R.id.tv_password)
    TextView tvPassword;
    @BindView(R.id.ll_pwd_layout)
    LinearLayout llPwdLayout;
    @BindView(R.id.tv_school_name)
    TextView tvSchoolName;
    @BindView(R.id.ll_school_layout)
    LinearLayout llSchoolLayout;
    @BindView(R.id.bt_exit_login)
    Button btExitLogin;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MineDetilsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_mine_detils;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolbarBlackNav();
        setToolbarTitle("个人资料");
    }


    @OnClick({R.id.ll_head_image_layout, R.id.ll_name_layout, R.id.ll_pwd_layout, R.id.ll_school_layout, R.id.bt_exit_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_head_image_layout:
                // 进入相册 以下是例子：用不到的api可以不写
                PictureSelector.create(MineDetilsActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.ll_name_layout:
                showUpdateUserNameDialog();
                break;
            case R.id.ll_pwd_layout:
                showUpdatePWdDialog();
                break;
            case R.id.ll_school_layout:
                showUpdateSchoolNameDialog();
                break;
            case R.id.bt_exit_login:
                isExitLogin();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (selectList.size() > 0) {
                        GlideUtils.loadImage(this, selectList.get(0).getPath(), headIv);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 显示修改密码的弹框
     */

    private String jpwd = "";
    private String newpwd = "";
    private String repwd = "";
    private String User_Pwd = "";//用户密码 默认本地缓存的密码

    private void showUpdatePWdDialog() {
        User_Pwd = SPUtil.getInstance().getString(SPUtil.USER_PWD);//用户密码 默认本地缓存的密码
        LinearLayout LinOvershootView = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_update_pwd, null);
        final EditText etPassword = LinOvershootView.findViewById(R.id.et_password);//用户旧密码
        final CheckBox checkShow = LinOvershootView.findViewById(R.id.check_show);//旧密码的显示隐藏
        final EditText etNewPassword = LinOvershootView.findViewById(R.id.et_new_password);//用户新密码
        final CheckBox checkNewShow = LinOvershootView.findViewById(R.id.check_new_show);//新密码的显示隐藏
        final EditText etRePassword = LinOvershootView.findViewById(R.id.et_re_password);///确定密码
        final CheckBox checkReShow = LinOvershootView.findViewById(R.id.check_re_show);//确定密码的显示隐藏
        ETChangedUtlis.EditTextChangedListener(etPassword, checkShow);
        ETChangedUtlis.EditTextChangedListener(etNewPassword, checkNewShow);
        ETChangedUtlis.EditTextChangedListener(etRePassword, checkReShow);
        Dialog pwddialog= new AlertDialog.Builder(this)
                .setView(LinOvershootView)
                .setPositiveButton("确定", (dialog, id) -> {
                    jpwd = etPassword.getText().toString();//旧密码
                    newpwd = etNewPassword.getText().toString();//新密码
                    repwd = etRePassword.getText().toString();//确定密码
                    //1 先判断用户有没有输入新密码 如果输入了新密码 就去验证旧密码和确定密码
                    if (!TextUtils.isEmpty(newpwd)) {
                        //如果新密码不为空 1 验证旧密码是否输入
                        if (checkOldPwd(jpwd)) {
                            //如果旧密码符合 再去验证确定密码
                            if (checkPWD(newpwd, repwd)) {
                                ToastUtil.showToast("更新密码");
                                // UpdatePwd(newpwd, dialog);
                            } else {
                                IOSDialogUtils.NoCosleDialog(dialog, false);
                            }
                        } else {
                            IOSDialogUtils.NoCosleDialog(dialog, false);
                        }
                    } else {
                        IOSDialogUtils.NoCosleDialog(dialog, false);
                    }
                })
                .setNegativeButton("取消", (dialog, id) -> IOSDialogUtils.NoCosleDialog(dialog, true))
                .setCancelable(true)
                .create();
        Window window = pwddialog.getWindow();
        window.setWindowAnimations(R.style.BaseDialogWindowStyle);
        pwddialog.setCancelable(false);
        pwddialog.show();
    }

    /**
     * 修改昵称
     */
    private void showUpdateUserNameDialog() {
        LinearLayout LinOvershootView = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_update_user_name, null);
        final EditText etUserName = LinOvershootView.findViewById(R.id.et_user_name);
        Dialog userdialog = new AlertDialog.Builder(this)
                .setView(LinOvershootView)
                .setPositiveButton("确定", (dialog, id) -> {
                    ToastUtil.showToast("昵称:" + etUserName.getText().toString());
                    IOSDialogUtils.NoCosleDialog(dialog, true);
                })
                .setNegativeButton("取消", (dialog, id) -> IOSDialogUtils.NoCosleDialog(dialog, true))
                .setCancelable(true)
                .create();
        Window window = userdialog.getWindow();
        window.setWindowAnimations(R.style.BaseDialogWindowStyle);
        userdialog.setCancelable(false);
        userdialog.show();
    }

    /**
     * 修改学校
     */
    private void showUpdateSchoolNameDialog() {
        LinearLayout LinOvershootView = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_update_user_school, null);
        final EditText etUserSchool = LinOvershootView.findViewById(R.id.et_user_school);
        Dialog scooldialog = new AlertDialog.Builder(this)
                .setView(LinOvershootView)
                .setPositiveButton("确定", (dialog, id) -> {
                    ToastUtil.showToast("学校:" + etUserSchool.getText().toString());
                    IOSDialogUtils.NoCosleDialog(dialog, false);
                })
                .setNegativeButton("取消", (dialog, id) -> IOSDialogUtils.NoCosleDialog(dialog, true))
                .setCancelable(true)
                .create();
        Window window = scooldialog.getWindow();
        window.setWindowAnimations(R.style.BaseDialogWindowStyle);
        scooldialog.setCancelable(false);
        scooldialog.show();
    }


    /**
     * 效验旧密码
     */
    private boolean checkOldPwd(String oldpwd) {
        if (TextUtils.isEmpty(oldpwd)) {
            ToastUtil.showToast("请输入旧密码进行验证!");
            return false;
        }
        if (User_Pwd.equals(oldpwd))
            return true;
        else {
            ToastUtil.showToast("旧密码错误！");
            return false;
        }
    }

    /**
     * 校验两个密码
     *
     * @param pwd
     * @param pwd1
     * @return
     */
    private boolean checkPWD(String pwd, String pwd1) {
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showToast("密码不能为空");
            return false;
        }
        if (pwd.length() < 6) {
            ToastUtil.showToast("密码长度不能少于6位");
            return false;
        }
        boolean ok = Validator.isPassword(pwd);
        if (!ok) {
            ToastUtil.showToast("密码格式错误");
            return false;
        }

        if (TextUtils.isEmpty(pwd1)) {
            ToastUtil.showToast("请再次输入密码");
            return false;
        }
        if (!pwd.equals(pwd1)) {
            ToastUtil.showToast("两次密码不一致");
            return false;
        } else {
            ToastUtil.showToast("修改信息!");
            return true;
        }
    }

    /**
     * 退出登录
     */
    private void isExitLogin() {
        IOSDialogUtils.IOSDialogBean iosDialogBean = new IOSDialogUtils.IOSDialogBean();
        iosDialogBean.setmTitle("温馨提示");
        iosDialogBean.setCancelable(false);
        iosDialogBean.setmMgs("确定退出登录吗？");
        iosDialogBean.setOnButtonClickListener(new IOSDialogUtils.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(CustomDialog dialog) {
                dialog.dismiss();
                ActivityCollector.finishAll();
                LoginActivity.actionStart(MyApp.getContext());
            }

            @Override
            public void onCancelButtonClick(CustomDialog dialog) {
                dialog.dismiss();
            }
        });
        IOSDialogUtils.getInstance().showDialogIOS(this, iosDialogBean);
    }

}
