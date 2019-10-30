package cn.ccsu.learning.utils;

import android.view.Gravity;
import android.widget.Toast;

import cn.ccsu.learning.app.MyApp;

public class ToastUtil {
    private static Toast toast;

    public static void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MyApp.getContext(), msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();
    }

    public static void showToast(int resId) {
        if (toast == null) {
            toast = Toast.makeText(MyApp.getContext(), resId, Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(resId);
        toast.show();
    }

    public static void setResultToToast(final String string) {
        MyApp.mFpvHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApp.getContext(), string + "", Toast.LENGTH_LONG).show();
            }
        });
    }


    public static void cancleToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
