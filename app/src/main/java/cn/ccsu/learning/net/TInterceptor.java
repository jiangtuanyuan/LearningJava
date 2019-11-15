package cn.ccsu.learning.net;

import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.utils.IOUtils;
import com.lzy.okgo.utils.OkLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import cn.ccsu.learning.utils.LogUtil;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;


/**
 * Created by ${LiuTao}.
 * User: Administrator
 * Name: RobRedPackage
 * functiona:
 * Date: 2019/5/30 0030
 * Time: 下午 15:25
 */
public class TInterceptor implements Interceptor {
    private String TAG = "网络拦截器";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private volatile HttpLoggingInterceptor.Level printLevel = HttpLoggingInterceptor.Level.BODY;

    public boolean netIsOk = false;

    public TInterceptor() {

    }

    private static volatile TInterceptor tInterceptor;

    public static TInterceptor getInstance() {
        if (tInterceptor == null) {
            synchronized (TInterceptor.class) {
                if (tInterceptor == null) {
                    tInterceptor = new TInterceptor();
                }
            }
        }
        return tInterceptor;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (printLevel == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request);
        }

        //请求日志拦截
        logForRequest(request, chain.connection());

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
            netIsOk = true;
        } catch (Exception e) {
            LogUtil.e(TAG, " HTTP FAILED: " + e);
            netIsOk = false;
//            ToastUtils.showToast(UIUtils.getResources().getString(R.string.net_error));
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        //响应日志拦截
        return logForResponse(response, tookMs);
    }

    private void logForRequest(Request request, Connection connection) throws IOException {
        boolean logBody = (printLevel == HttpLoggingInterceptor.Level.BODY);
        boolean logHeaders = (printLevel == HttpLoggingInterceptor.Level.BODY || printLevel == HttpLoggingInterceptor.Level.HEADERS);
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        try {
            String requestStartMessage = "" + request.method() + ' ' + request.url() + ' ' + protocol;
            LogUtil.e(TAG, requestStartMessage);

            if (logHeaders) {
                if (hasRequestBody) {
                    // Request body headers are only present when installed as base_spalsh network interceptor. Force
                    // them to be included (when available) so there values are known.
                    if (requestBody.contentType() != null) {
                        LogUtil.e(TAG, "\n|Content-Type: " + requestBody.contentType());
                    }
                    if (requestBody.contentLength() != -1) {
                        LogUtil.e(TAG, "\n|Content-Length: " + requestBody.contentLength());
                    }
                }
                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        LogUtil.e(TAG, "\n|" + name + ": " + headers.value(i));
                    }
                }

                LogUtil.e(TAG, " ");
                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody.contentType())) {
                        bodyToString(request);
                    } else {
                        LogUtil.e(TAG, "\n|body: maybe [binary body], omitted!");
                    }
                }
            }
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            LogUtil.e(TAG, "END " + request.method());
        }
    }

    private Response logForResponse(Response response, long tookMs) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        boolean logBody = (printLevel == HttpLoggingInterceptor.Level.BODY);
        boolean logHeaders = (printLevel == HttpLoggingInterceptor.Level.BODY || printLevel == HttpLoggingInterceptor.Level.HEADERS);

        try {
            LogUtil.e(TAG, "\n|" + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）");
            if (logHeaders) {
                Headers headers = clone.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {

                    LogUtil.e(TAG, "\n|" + headers.name(i) + ": " + headers.value(i));
                }
                LogUtil.e(TAG, " ");
                if (logBody && HttpHeaders.hasBody(clone)) {
                    if (responseBody == null) return response;

                    if (isPlaintext(responseBody.contentType())) {
                        byte[] bytes = IOUtils.toByteArray(responseBody.byteStream());
                        MediaType contentType = responseBody.contentType();
                        String body = new String(bytes, getCharset(contentType));
                        LogUtil.e(TAG, "\n|body:" + body);
                        if (body.startsWith("{") || body.startsWith("[")) {
                            checkToken(body);
                        }
                        responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                        return response.newBuilder().body(responseBody).build();
                    } else {
                        LogUtil.e(TAG, "\n|body: maybe [binary body], omitted!");
                    }
                }
            }
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            LogUtil.e(TAG, " END HTTP");
        }
        return response;
    }

    private void checkToken(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            int code = jsonObject.optInt("code");

            if (code == NetResultCode.CODE1015.getCode() || code == NetResultCode.CODE1011.getCode()) {
                // BaseApp.getInstance().toLogin();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void reLogin() {

    }


    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses base_spalsh small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private void bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            LogUtil.e(TAG, "\n|Request body:" + buffer.readString(charset));
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        }
    }

    public boolean isNetIsOk() {
        return netIsOk;
    }

    public void setNetIsOk(boolean netIsOk) {
        this.netIsOk = netIsOk;
    }
}
