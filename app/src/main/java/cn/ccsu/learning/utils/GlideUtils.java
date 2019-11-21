package cn.ccsu.learning.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import cn.ccsu.learning.R;
import cn.ccsu.learning.app.MyApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 封装一下Glide
 * 无论使用啥框架 都在做一层封装
 */
public class GlideUtils {
    public static void loadImage(Activity activity, String path, ImageView imageView) {
        Glide.with(activity)
                .load(path)
                .into(imageView);
    }

    public static void loadImage(Context mContext, String path, ImageView imageView) {
        Glide.with(mContext)
                .load(path)
                .into(imageView);
    }

    public static void intoImage(String path, ImageView imageView) {
        if (path == null) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .error(R.mipmap.logo)
                .placeholder(R.mipmap.logo)
                .centerCrop();
        Glide.with(MyApp.getContext())
                .load(path)
                .apply(requestOptions)
                .transition(withCrossFade())
                .into(imageView);
    }
}
