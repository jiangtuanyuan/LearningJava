package cn.ccsu.learning.ui.main.fragment.java.adapter;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;

/**
 * 基础知识 适配器
 * jiang
 * 2019-10-16
 */
public class BasisListAdapter extends BaseQuickAdapter<BasisBean, BaseViewHolder> {
    public BasisListAdapter(List<BasisBean> list) {
        super(R.layout.java_basis_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, BasisBean item) {
        helper.addOnClickListener(R.id.ll_layout);
        helper.addOnClickListener(R.id.iv_delete);
    }
}
