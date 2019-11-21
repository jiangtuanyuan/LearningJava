package cn.ccsu.learning.ui.main.fragment.mine.download;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;

public class MyDownloadListAdapter extends BaseQuickAdapter<MyDownLoadBean, BaseViewHolder> {
    public MyDownloadListAdapter(List<MyDownLoadBean> list) {
        super(R.layout.my_download_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyDownLoadBean item) {
        helper.addOnClickListener(R.id.iv_delete);
        helper.addOnClickListener(R.id.tv_file_name);
        //标题
        helper.setText(R.id.tv_file_name, item.getFileAlias());
    }
}
