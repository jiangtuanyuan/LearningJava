package cn.ccsu.learning.ui.main.fragment.mine.user;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;

/**
 * 用户列表适配器
 * jiang
 * 2019-10-16
 */
public class UserListAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
    public UserListAdapter(List<UserBean> list) {
        super(R.layout.user_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        helper.addOnClickListener(R.id.ll_layout);
        helper.addOnClickListener(R.id.iv_delete);
        //标题
        helper.setText(R.id.tv_name, item.getUserName());
        //内容 简介
        helper.setText(R.id.tv_tel, "电话:"+item.getUserTel());
        //上传者
        helper.setText(R.id.tv_sex, "性别:"+item.getUserSex());
        //上传时间
        helper.setText(R.id.tv_shcool, "学校:"+item.getUserSubordinate());
    }
}
