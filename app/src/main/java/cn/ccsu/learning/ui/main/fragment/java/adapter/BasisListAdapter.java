package cn.ccsu.learning.ui.main.fragment.java.adapter;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;

/**
 * 基础知识 适配器
 * jiang
 * 2019-10-16
 */
public class BasisListAdapter extends BaseQuickAdapter<BasisBean.ListsBean, BaseViewHolder> {
    public BasisListAdapter(List<BasisBean.ListsBean> list) {
        super(R.layout.java_basis_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, BasisBean.ListsBean item) {
        helper.addOnClickListener(R.id.ll_layout);
        helper.addOnClickListener(R.id.iv_delete);
        //标题
        helper.setText(R.id.tv_resTitle, item.getResTitle());
        //内容 简介
        helper.setText(R.id.tv_resContent, item.getResContent());
        //上传者
        helper.setText(R.id.tv_userName, "作者：" + item.getUserName());
        //上传时间
        helper.setText(R.id.tv_createTime, "上传时间：" + item.getCreateTime());
        if (User.getInstance().getRid().equals("1")) {
            helper.setVisible(R.id.iv_delete, false);
        } else if (User.getInstance().getRid().equals("2")) {
            //老师  自己的可以删除
            if (item.getUserName().equals(User.getInstance().getUserName())) {
                helper.setVisible(R.id.iv_delete, true);
            } else {
                helper.setVisible(R.id.iv_delete, false);
            }
        } else {
            //管理员 显示全部删
            helper.setVisible(R.id.iv_delete, true);
        }

    }
}
