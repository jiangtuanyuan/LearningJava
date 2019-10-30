package com.hd.commonmodule.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hd.commonmodule.R;

/**
 * des:加载页面内嵌提示
 * 加载状态 四种状态
 *
 * @author LiuTao
 * @date 2019/10/18/029
 */
public class PagerLoadingView extends LinearLayout {

    private ImageView img_tip_logo;
    private TextView tv_tips;
    private TextView bt_operate;
    private String errorMsg = "糟糕，网络开小差了，刷新试试";
    private onReloadListener onReloadListener;

    public LinearLayout mProgress;
    public LinearLayout mOtherStatusLayout;

    public PagerLoadingView(Context context) {
        super(context);
        initView(context);
    }

    public PagerLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PagerLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PagerLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    //加载失败，数据为空、加载中、完成四种状态
    public enum LoadStatus {
        error, empty, loading, finish,
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.common_dialog_loading_tip, this);
        mOtherStatusLayout = (LinearLayout) findViewById(R.id.other_status_layout);
        img_tip_logo = (ImageView) findViewById(R.id.img_tip_logo);
        mProgress = (LinearLayout) findViewById(R.id.loading_progress_layout);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        bt_operate = (TextView) findViewById(R.id.bt_operate);
        //重新尝试
        bt_operate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onReloadListener != null) {
                    onReloadListener.reload();
                }
            }
        });
        setVisibility(View.GONE);
    }

    public void setTips(String tips) {
        if (tv_tips != null) {
            tv_tips.setText(tips);
        }
    }

    /**
     * 根据状态显示不同的提示
     *
     * @param loadStatus
     */
    public void setLoadingTip(LoadStatus loadStatus) {
        switch (loadStatus) {
            case empty:
                bt_operate.setVisibility(INVISIBLE);
                setVisibility(View.VISIBLE);
                img_tip_logo.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
                mOtherStatusLayout.setVisibility(VISIBLE);
                tv_tips.setText("暂无数据");
                img_tip_logo.setImageResource(R.drawable.common_data_empty);
                break;
            case error:
                setVisibility(View.VISIBLE);
                mOtherStatusLayout.setVisibility(View.VISIBLE);
                img_tip_logo.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
                if (TextUtils.isEmpty(errorMsg)) {
                    tv_tips.setText(getContext().getText(R.string.common_net_error).toString());
                } else {
                    tv_tips.setText(errorMsg);
                }
                bt_operate.setVisibility(VISIBLE);
                bt_operate.setText("重新尝试");
                img_tip_logo.setImageResource(R.drawable.common_data_error);
                break;
            case loading:
                mOtherStatusLayout.setVisibility(GONE);
                bt_operate.setVisibility(GONE);
                setVisibility(View.VISIBLE);
                img_tip_logo.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                tv_tips.setText(getContext().getText(R.string.common_loading).toString());
                break;
            case finish:
                mProgress.setVisibility(View.GONE);
                bt_operate.setVisibility(GONE);
                setVisibility(View.GONE);
                break;
        }
    }


    public void setOnReloadListener(onReloadListener onReloadListener) {
        this.onReloadListener = onReloadListener;
    }

    /**
     * 重新尝试接口
     */
    public interface onReloadListener {
        void reload();
    }


}

