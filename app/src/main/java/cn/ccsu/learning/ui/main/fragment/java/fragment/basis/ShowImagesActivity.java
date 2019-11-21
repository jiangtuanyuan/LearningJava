package cn.ccsu.learning.ui.main.fragment.java.fragment.basis;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bm.library.PhotoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.custom.PreImageViewPager;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.ui.main.fragment.java.bean.FilesDetailsBean;
import cn.ccsu.learning.utils.GlideUtils;
import cn.ccsu.learning.utils.ToastUtil;

public class ShowImagesActivity extends BaseActivity {
    @BindView(R.id.view_pager)
    PreImageViewPager mViewPager;
    private ArrayList<FilesDetailsBean> mMImages;
    private int mPosition = 0;
    private PreviewPagerAdapter mPreviewPagerAdapter;

    public static void actionStart(Context context, ArrayList<FilesDetailsBean> imageList, int position) {
        Intent intent = new Intent(context, ShowImagesActivity.class);
        intent.putParcelableArrayListExtra("imageList", imageList);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_show_images;
    }

    @Override
    protected void initVariables() {
        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();
            mMImages = getIntent().getParcelableArrayListExtra("imageList");
            mPosition = intent.getIntExtra("position", 0);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (mMImages.size() == 0) {
            ToastUtil.showToast("图片数量为0!");
            finish();
            return;
        }
        initToolbarBlackNav();

        setToolbarTitle((mPosition + 1) + "/" + mMImages.size());
        mPreviewPagerAdapter = new PreviewPagerAdapter(this, mMImages);
        mViewPager.setAdapter(mPreviewPagerAdapter);
        mViewPager.setCurrentItem(mPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                mPosition = pos;
                setToolbarTitle((mPosition + 1) + "/" + mMImages.size());
            }
        });
    }

    public class PreviewPagerAdapter extends PagerAdapter {
        private List<FilesDetailsBean> mImages;
        private Context mContenxt;
        private int mChildCount = 0;

        public PreviewPagerAdapter(Context context, List<FilesDetailsBean> images) {
            this.mContenxt = context;
            this.mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            try {
                //放大缩小旋转的
                PhotoView photoView = new PhotoView(mContenxt);
                photoView.enable();//启动图片缩放功能
                photoView.setMaxScale(8);
                photoView.setScaleType(ImageView.ScaleType.FIT_XY);

                String path = NetApi.HIP + mImages.get(position).getFilePath()
                        .replace("D:", "")
                        .replace("\\", "/");


                GlideUtils.intoImage(path, photoView);
                container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return photoView;
            } catch (Exception e) {
                e.printStackTrace();
                //普通的ImageView
                ImageView imageView = new ImageView(mContenxt);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                String path = NetApi.HIP + mImages.get(position).getFilePath()
                        .replace("D:", "")
                        .replace("\\", "/");

                GlideUtils.intoImage(path, imageView);
                container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return imageView;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }
    }
}
