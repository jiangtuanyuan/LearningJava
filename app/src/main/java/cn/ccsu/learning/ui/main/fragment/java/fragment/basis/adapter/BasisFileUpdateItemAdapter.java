package cn.ccsu.learning.ui.main.fragment.java.fragment.basis.adapter;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.ui.main.fragment.java.bean.FilesDetailsBean;

/**
 * 附件修改适配器
 */
public class BasisFileUpdateItemAdapter extends BaseQuickAdapter<FilesDetailsBean, BaseViewHolder> {
    public BasisFileUpdateItemAdapter(List<FilesDetailsBean> list) {
        super(R.layout.java_download_delete_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, FilesDetailsBean item) {
        helper.addOnClickListener(R.id.tv_delete);
        //标题
        helper.setText(R.id.tv_name, item.getFileAlias());
    }
}
