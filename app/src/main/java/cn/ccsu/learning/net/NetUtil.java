package cn.ccsu.learning.net;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.ccsu.learning.utils.LogUtil;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;


/**
 * Created by LiuTao.
 * functiona:网络数据请求
 * Date: 2019/8/28 0028
 * Time: 上午 11:04
 */
public class NetUtil {


    public interface DataListener {
        void showDialogLoading();

        void onSubScribe(Disposable d);

        void onError(Throwable e);

        void onFaild();

        void dissDialogmissLoad();

        void onSuccess(String info, String data);
    }



    /**
     * 网络数据
     *
     * @param dataListener
     */
    public void getNetData(String url, HttpParams httpParams, final DataListener dataListener) {
        OkGo.<String>get(url)
                .params(httpParams)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (dataListener != null) {
                            dataListener.showDialogLoading();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        if (dataListener != null) {
                            dataListener.onSubScribe(d);
                        }
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        try {
                            if (stringResponse.isSuccessful()) {
                                String s = stringResponse.body();
                                LogUtil.e("请求信息", s);
                                if (s != null) {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int code = jsonObject.optInt("code");
                                    String info = jsonObject.optString("info");
                                    String data = jsonObject.optString("data");
                                    if (code == NetResultCode.CODE10000.getCode()) {
                                        if (dataListener != null) {
                                            dataListener.onSuccess(info, data);
                                        }
                                    } else {
                                        ToastUtil.showToast(NetResultCode.getCode(code).getDesc());
                                        if (dataListener != null) {
                                            dataListener.onFaild();
                                        }
                                        if (code == NetResultCode.CODE1015.getCode() || code == NetResultCode.CODE1011.getCode()) {
                                            //   BaseApp.getInstance().toLogin();
                                        }
                                    }
                                } else {
                                    if (dataListener != null) {
                                        dataListener.onFaild();
                                    }
                                    ToastUtil.showToast("请求服务器数据失败");
                                }
                            } else {
                                if (dataListener != null) {
                                    dataListener.onFaild();
                                }
                                ToastUtil.showToast("请求服务器失败,请稍后再试");
                            }
                        } catch (Exception e) {
                            if (dataListener != null) {
                                dataListener.onFaild();
                            }
                            e.printStackTrace();
                            ToastUtil.showToast("解析服务器数据错误");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e("请求信息", "---------------onError-----------" + e.getMessage());
                        try {
                            ToastUtil.showToast(e.getMessage() + "");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (dataListener != null) {
                            dataListener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (dataListener != null) {
                            dataListener.dissDialogmissLoad();
                        }
                    }
                });
    }


    /**
     * 网络数据json
     *
     * @param dataListener
     */
    public void postJsonNetData(String url, RequestBody requestBody, final DataListener dataListener) {
        OkGo.<String>post(url)
                .upRequestBody(requestBody)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (dataListener != null) {
                            dataListener.showDialogLoading();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        if (dataListener != null) {
                            dataListener.onSubScribe(d);
                        }
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        try {
                            if (stringResponse.isSuccessful()) {
                                String s = stringResponse.body();
                                LogUtil.e("请求信息", s);
                                if (s != null) {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int code = jsonObject.optInt("code");
                                    String info = jsonObject.optString("info");
                                    String data = jsonObject.optString("data");
                                    if (code == NetResultCode.CODE200.getCode()) {
                                        if (dataListener != null) {
                                            dataListener.onSuccess(info, data);
                                        }
                                    } else {
                                        //ToastUtil.showToast(NetResultCode.getCode(code).getDesc());
                                        ToastUtil.showToast(jsonObject.optString("info"));
                                        if (dataListener != null) {
                                            dataListener.onFaild();
                                        }
                                        if (code == NetResultCode.CODE1015.getCode() || code == NetResultCode.CODE1011.getCode()) {
                                            //BaseApp.getInstance().toLogin();
                                        }
                                    }
                                } else {
                                    if (dataListener != null) {
                                        dataListener.onFaild();
                                    }
                                    ToastUtil.showToast("请求服务器数据失败");
                                }
                            } else {
                                if (dataListener != null) {
                                    dataListener.onFaild();
                                }
                                ToastUtil.showToast("请求服务器失败,请稍后再试");
                            }
                        } catch (Exception e) {
                            if (dataListener != null) {
                                dataListener.onFaild();
                            }
                            e.printStackTrace();
                            ToastUtil.showToast("解析服务器数据错误");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e("请求信息", "---------------onError-----------" + e.getMessage());
                        try {
                            ToastUtil.showToast(e.getMessage() + "");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (dataListener != null) {
                            dataListener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (dataListener != null) {
                            dataListener.dissDialogmissLoad();
                        }
                    }
                });
    }

    /**
     * 网络数据
     * post
     *
     * @param dataListener
     */
    public void postNetData(String url, HttpParams httpParams, final DataListener dataListener) {
        OkGo.<String>post(url)
                .params(httpParams)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (dataListener != null) {
                            dataListener.showDialogLoading();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        if (dataListener != null) {
                            dataListener.onSubScribe(d);
                        }
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        try {
                            if (stringResponse.isSuccessful()) {
                                String s = stringResponse.body();
                                LogUtil.e("请求信息", s);
                                Logger.json(s);
                                if (s != null) {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int code = jsonObject.optInt("code");
                                    String info = jsonObject.optString("info");
                                    String data = jsonObject.optString("data");
                                    if (code == NetResultCode.CODE200.getCode()) {
                                        if (dataListener != null) {
                                            dataListener.onSuccess(info, data);
                                        }
                                    } else {
                                        ToastUtil.showToast(jsonObject.optString("info"));
                                        if (dataListener != null) {
                                            dataListener.onFaild();
                                        }

                                    }
                                } else {
                                    if (dataListener != null) {
                                        dataListener.onFaild();
                                    }
                                }
                            } else {
                                if (dataListener != null) {
                                    dataListener.onFaild();
                                }
                            }
                        } catch (Exception e) {
                            if (dataListener != null) {
                                dataListener.onFaild();
                            }
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e("请求信息", "---------------onError-----------" + e.getMessage());
                        ToastUtil.showToast("连接服务器失败，请稍候再试。");
                        if (dataListener != null) {
                            dataListener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (dataListener != null) {
                            dataListener.dissDialogmissLoad();
                        }
                    }
                });
    }
}
