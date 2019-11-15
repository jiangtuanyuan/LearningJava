package cn.ccsu.learning.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
}
