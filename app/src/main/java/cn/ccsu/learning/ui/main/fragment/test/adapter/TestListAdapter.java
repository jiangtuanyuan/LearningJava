package cn.ccsu.learning.ui.main.fragment.test.adapter;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.ui.main.fragment.test.bean.TestBean;

/**
 * 在线测试
 */
public class TestListAdapter extends BaseQuickAdapter<TestBean, BaseViewHolder> {
    public TestListAdapter(List<TestBean> list) {
        super(R.layout.test_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestBean item) {
        helper.addOnClickListener(R.id.tv_start);

        //标题
        helper.setText(R.id.tv_name, item.getTypeName());

       /* if (User.getInstance().getRid().equals("1")) {
            helper.setVisible(R.id.iv_delete, false);
        }*/
    }
}
