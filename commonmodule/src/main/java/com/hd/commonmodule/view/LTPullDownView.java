package com.hd.commonmodule.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hd.commonmodule.R;
import com.hd.commonmodule.utils.RecyclerViewUtils;


/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/10/29/029
 */


public class LTPullDownView extends RelativeLayout implements View.OnClickListener {

    private Object[] mTS;
    private int id;
    private TextView mContentText;
    private ImageView mPullImage;
    private RelativeLayout mLtPullViewLly;
    private int pwposition;
    private String text = null;
    private int backgroundColor = R.color.common_white;
    private int image = R.drawable.ic_common_pull_down;
    private OnAdapterPositionClickListener listener;

    public LTPullDownView(Context context) {
        super(context);
        initUI(context);
    }

    public LTPullDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LTPullDownView);
        text = array.getString(R.styleable.LTPullDownView_text);
        backgroundColor = array.getInt(R.styleable.LTPullDownView_background_color, backgroundColor);
        image = array.getInt(R.styleable.LTPullDownView_pull_image_icon, image);
        array.recycle();
        initUI(context);
    }


    private void initUI(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.common_item_lt_pull_down_view, this);
        view.setBackgroundColor(context.getResources().getColor(backgroundColor));
        mLtPullViewLly = view.findViewById(R.id.lt_pull_view_lly);
        mContentText = view.findViewById(R.id.content_text);
        mPullImage = view.findViewById(R.id.pull_image);
        mLtPullViewLly.setOnClickListener(this);
        mPullImage.setImageDrawable(context.getResources().getDrawable(image));
        if (text != null && !TextUtils.isEmpty(text)) {
            mContentText.setText(text + "");
        }

    }

    public void setDatas(final Object[] ts, final int position, int pwposition) {
        this.mTS = ts;
        this.pwposition = pwposition;
        if (mContentText != null && ts != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mContentText.setText(ts[position] + "");
                }
            });

        }
    }

    public void updateView(final int position) {
        if (mContentText != null && mTS != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mContentText.setText(mTS[position] + "");
                }
            });

        }
    }

    private boolean isShowList = true;

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.lt_pull_view_lly) {
            showPuPw();
        }
    }


    private void showPuPw() {
        final PopupWindow mActionTypePW;
        mPullImage.setRotation(0);
        RecyclerView recyclerView = RecyclerViewUtils.createLinearLayoutManagerRecyclerView(getContext());
        GeneralAdapter generalAdapter = new GeneralAdapter(getContext(), mTS);
        recyclerView.setAdapter(generalAdapter);
        mActionTypePW = new PopupWindow(recyclerView, mLtPullViewLly.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT, false);
        generalAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mActionTypePW.dismiss();
                if (mTS != null) {
                    mContentText.setText(mTS[position] + "");
                }

                mPullImage.setRotation(0);
                listener.onPositionClick(id, position);
            }
        });
        mActionTypePW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mActionTypePW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mActionTypePW.setOutsideTouchable(true);
        mActionTypePW.setFocusable(true);
        mActionTypePW.getContentView().
                measure(makeDropDownMeasureSpec(mActionTypePW.getWidth()),
                        makeDropDownMeasureSpec(mActionTypePW.getHeight()));

        int height = mActionTypePW.getContentView().getMeasuredHeight();
        int with = mActionTypePW.getContentView().getMeasuredWidth();
        //下面正常弹出
        if (pwposition == 0) {
            mActionTypePW.showAsDropDown(mLtPullViewLly, 0, 0);
        }
        //从上面弹出
        if (pwposition == 1) {
            int offsetX = Math.abs(mActionTypePW.getContentView().getMeasuredWidth() - mLtPullViewLly.getWidth()) / 2;
            int offsetY = -(mActionTypePW.getContentView().getMeasuredHeight() + mLtPullViewLly.getHeight());
            PopupWindowCompat.showAsDropDown(mActionTypePW, mLtPullViewLly, offsetX, offsetY, Gravity.START);
        }
        //左
        if (pwposition == 2) {
        }
        //右
        if (pwposition == 3) {
        }
        //中
        if (pwposition == 4) {
        }

        mActionTypePW.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPullImage.setRotation(180);

            }
        });
    }


    //定义一个对外提供的接口
    public interface OnAdapterPositionClickListener {
        void onPositionClick(int id, int position);
    }

    //定义一个对外提供调用接口的方法（有参构造）
    public void setOnPositionClickListener(int id, OnAdapterPositionClickListener listener) {
        if (listener != null) {
            this.id = id;
            this.listener = listener;
        }
    }


    @SuppressWarnings("ResourceType")
    private int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = MeasureSpec.UNSPECIFIED;
        } else {
            mode = MeasureSpec.EXACTLY;
        }
        return MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(measureSpec), mode);
    }


    class GeneralAdapter<T> extends RecyclerView.Adapter<GeneralAdapter.ItemViewHolder> {

        private T[] mStrings;

        private Context mContext;
        private LayoutInflater mInflater;
        private ItemClickListener mItemClickListener;
        private ItemOnLongClickListener mItemOnLongClickListener;

        public void setOnItemClickListener(ItemClickListener listener) {
            this.mItemClickListener = listener;
        }

        public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
            mItemOnLongClickListener = itemOnLongClickListener;
        }


        public GeneralAdapter(Context context, T[] strings) {
            this.mStrings = strings;
            this.mContext = context;
            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public GeneralAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_data_item, parent, false);
            GeneralAdapter.ItemViewHolder holder = new GeneralAdapter.ItemViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(GeneralAdapter.ItemViewHolder holder, final int position) {
            holder.tvName.setText(mStrings[position] + "");
            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mItemClickListener.onItemClick(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //mItemOnLongClickListener.onItemOnLongClick(position);
                    return true;  //为true消耗事件，false不消耗事件 继续传递
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mStrings == null ? 0 : mStrings.length;

        }


        protected class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            View itemView;

            public ItemViewHolder(View view) {
                super(view);
                itemView = view.findViewById(R.id.text_item);
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public interface ItemOnLongClickListener {
        void onItemOnLongClick(int position);
    }

}
