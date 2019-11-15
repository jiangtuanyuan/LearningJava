package cn.ccsu.learning.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.security.SecurityPermission;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.ui.main.MainActivity;
import cn.ccsu.learning.ui.register.RegisterActivity;
import cn.ccsu.learning.utils.SPUtil;
import cn.ccsu.learning.utils.ToastUtil;

public class SetIPActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SetIPActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_set_ip;
    }

    @Override
    protected void initVariables() {

    }

    private final String thisIP = "192.168.43.85";
    private final String thisPort = "8081";

    private void initData() {
        etIp.setText(SPUtil.getInstance().getString(SPUtil.IP, thisIP));
        etPort.setText(SPUtil.getInstance().getString(SPUtil.PORT, thisPort));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolbarBlackNav();
        setToolbarTitle("IP设置");
        initData();

    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        if (checkIP(etIp.getText().toString())) {
            showDialog();
        } else {
            ToastUtil.showToast("请输入正确的IP格式!");
        }
    }

    /**
     * 效验IP 正则表达式验证
     */
    private boolean checkIP(String ip) {
        if (!TextUtils.isEmpty(ip)) {
            String[] ips = ip.split("\\.");
            if (ips.length == 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * 显示是否更改IP地址 确定的话需要重启APP 取消不执行写入SP操作
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("更改IP地址和端口需要重启APP!");
        builder.setCancelable(false);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtil.getInstance().putString(SPUtil.IP, etIp.getText().toString());
                SPUtil.getInstance().putString(SPUtil.PORT, etPort.getText().toString());
                System.exit(0);
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();


    }
}
