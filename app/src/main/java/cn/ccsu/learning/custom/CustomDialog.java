package cn.ccsu.learning.custom;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import cn.ccsu.learning.R;


/**
 * 自定义内容Dialog
 */
public class CustomDialog extends Dialog {
    private int resLayoutId;

    public CustomDialog(Context context, int resLayoutId) {
        super(context, R.style.CustomDialog);
        this.resLayoutId = resLayoutId;
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setWindowAnimations(R.style.BaseDialogWindowStyle);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resLayoutId);
    }
}