package cn.ccsu.learning.ui.main.fragment.java.fragment.basis.adapter;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;

/**
 * 基础知识 详情的 适配器
 * jiang
 * 2019-10-16
 */
public class BasisDetailsListAdapter extends BaseQuickAdapter<BasisBean, BaseViewHolder> {
    public BasisDetailsListAdapter(List<BasisBean> list) {
        super(R.layout.java_basis_details_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, BasisBean item) {
        helper.addOnClickListener(R.id.ll_layout);
        helper.addOnClickListener(R.id.iv_delete);
    }
}
