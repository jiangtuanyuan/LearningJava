package cn.ccsu.learning.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.ccsu.learning.BuildConfig;
import cn.ccsu.learning.net.NetInterceptor;
import cn.ccsu.learning.utils.BuglyUtils;
import okhttp3.OkHttpClient;

public class MyApp extends Application {
    private static Context mContext;
    public static Handler mFpvHandler;

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        mFpvHandler = new Handler(getMainLooper());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initLogger();
        initBugly();
        initOkGo();
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 初始化Okgo
     */
    private void initOkGo() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("网络监听");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);

        NetInterceptor mNetInterceptor = new NetInterceptor(mContext);
        mNetInterceptor.setShowRequestLog(BuildConfig.DEBUG);
        mNetInterceptor.setShowResponseLog(BuildConfig.DEBUG);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(mNetInterceptor)
                //.addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cookieJar(new CookieJarImpl(new SPCookieStore(this)));

        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .addCommonHeaders(new HttpHeaders("token", "1123"))
                .setRetryCount(1);
    }

    /**
     * 初始化Logger
     */
    private void initLogger() {
        //Logger 初始化
        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(new AndroidLogAdapter());
        } else {
            //项目上线 执行该注释方法 取消打印日志 防止ADB抓取日志
            Logger.addLogAdapter(new AndroidLogAdapter() {
                @Override
                public boolean isLoggable(int priority, String tag) {
                    return false;
                }
            });
        }
    }


    /**
     * 初始化Bugly
     */
    private void initBugly() {
        /*全量更新配置*/
        BuglyUtils.BuglyAppConfig();
        /*热更新配置*/
        //BuglyUtils.BuglyHotConfig();
        Bugly.init(mContext, BuildConfig.BUGLY_APP_ID, false);
    }
}
