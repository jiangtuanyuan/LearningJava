package cn.ccsu.learning.ui.main.fragment.java.fragment.basis.adapter;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.ui.main.fragment.java.bean.FilesDetailsBean;

/**
 * 基础知识 详情的 适配器
 * jiang
 * 2019-10-16
 */
public class BasisDownLoadItemAdapter extends BaseQuickAdapter<FilesDetailsBean, BaseViewHolder> {
    public BasisDownLoadItemAdapter(List<FilesDetailsBean> list) {
        super(R.layout.java_download_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, FilesDetailsBean item) {
        helper.addOnClickListener(R.id.tv_download);
        //标题
        helper.setText(R.id.tv_name, item.getFileAlias());

    }
}
