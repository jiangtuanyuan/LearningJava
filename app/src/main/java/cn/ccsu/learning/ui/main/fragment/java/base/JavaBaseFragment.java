package cn.ccsu.learning.ui.main.fragment.java.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;

/**
 * 任务的基类fragment
 * jiang
 */
public abstract class JavaBaseFragment extends Fragment {
    private LoadingPager mLoadingPager;
    protected Context mContext = null;
    //自己修改
    public int pageNum = 1;//分页要查询的数据当前页码，默认为1
    public int pageSize = 10;//分页每页查询的数据量，默认10
    public String pageNumStr = "pageNum";
    public String pageSizeStr = "pageSize";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            mContext = getContext();
            mLoadingPager = new LoadingPager(mContext) {
                @Override
                public LoadedResult initData() {
                    getData(true);
                    return JavaBaseFragment.this.initData();
                }

                @Override
                public View initSuccessView() {
                    return JavaBaseFragment.this.initSuccessView();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLoadingPager.triggerLoadData();
        return mLoadingPager;
    }

    public abstract LoadingPager.LoadedResult initData();

    public abstract View initSuccessView();

    public abstract void getData(boolean refresh);

    /**
     * @des 校验请求回来的数据
     */
    public LoadingPager.LoadedResult checkResultData(Object resObj) {
        if (resObj == null) {
            return LoadingPager.LoadedResult.EMPTY;
        }
        //resObj -->List
        if (resObj instanceof List) {
            if (((List) resObj).size() == 0) {
                return LoadingPager.LoadedResult.EMPTY;
            }
        }
        //resObj -->Map
        if (resObj instanceof Map) {
            if (((Map) resObj).size() == 0) {
                return LoadingPager.LoadedResult.EMPTY;
            }
        }
        return LoadingPager.LoadedResult.SUCCESS;
    }

}

