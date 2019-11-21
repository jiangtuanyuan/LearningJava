package cn.ccsu.learning.ui.main.fragment.mine.logs;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.db.ShowLogs;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;

public class LogListAdapter extends BaseQuickAdapter<ShowLogs, BaseViewHolder> {
    public LogListAdapter(List<ShowLogs> list) {
        super(R.layout.java_basis_details_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShowLogs item) {
        helper.addOnClickListener(R.id.ll_layout);
        helper.addOnClickListener(R.id.iv_delete);

        //标题
        helper.setText(R.id.tv_resTitle, item.getResTitle());
        //上传者
        helper.setText(R.id.tv_userName, "上传者：" + item.getUserName());
        //上传时间
        helper.setText(R.id.tv_createTime, "上传时间：" + item.getCreateTime());
        if (User.getInstance().getRid().equals("1")) {
            helper.setVisible(R.id.iv_delete, false);
        }
    }
}
