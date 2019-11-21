package cn.ccsu.learning.ui.main.fragment.test.adapter;

import android.view.View;
import android.widget.RadioGroup;

import com.hd.commonmodule.chad.library.adapter.base.BaseQuickAdapter;
import com.hd.commonmodule.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ccsu.learning.R;
import cn.ccsu.learning.ui.main.fragment.test.bean.TestDetailsBean;

/**
 * 在线测试
 */
public class TestDetailsAdapter extends BaseQuickAdapter<TestDetailsBean, BaseViewHolder> {
    public TestDetailsAdapter(List<TestDetailsBean> list) {
        super(R.layout.test_details_list_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestDetailsBean item) {
        helper.setText(R.id.tv_name, "(" + (helper.getAdapterPosition() + 1) + ") " + item.getExCont());
        if (item.isCommit()) {
            helper.setVisible(R.id.tv_answer, true);
            helper.setEnabled(R.id.rb_a, false);
            helper.setEnabled(R.id.rb_b, false);
            helper.setEnabled(R.id.rb_c, false);
            helper.setEnabled(R.id.rb_d, false);
            helper.setEnabled(R.id.rb_true, false);
            helper.setEnabled(R.id.rb_false, false);
        } else {
            helper.setVisible(R.id.tv_answer, false);
        }

        RadioGroup RgABCD = helper.getView(R.id.rg_abcd);
        RadioGroup RgTF = helper.getView(R.id.rg_true_false);
        RgABCD.setOnCheckedChangeListener(null);
        RgTF.setOnCheckedChangeListener(null);
        if (item.getExKind().equals("选择")) {
            helper.setText(R.id.tv_answer, "正确答案:" + item.getExAnswer());
            //隐藏判断
            RgABCD.setVisibility(View.VISIBLE);
            RgTF.setVisibility(View.GONE);
            //设置题目
            helper.setText(R.id.rb_a, "A: " + item.getChoiceA());
            helper.setText(R.id.rb_b, "B: " + item.getChoiceB());
            helper.setText(R.id.rb_c, "C: " + item.getChoiceC());
            helper.setText(R.id.rb_d, "D: " + item.getChoiceD());

            //设置选中
            switch (item.getCheckString()) {
                case "A":
                    helper.setChecked(R.id.rb_a, true);
                    break;
                case "B":
                    helper.setChecked(R.id.rb_b, true);
                    break;
                case "C":
                    helper.setChecked(R.id.rb_c, true);
                    break;
                case "D":
                    helper.setChecked(R.id.rb_d, true);
                    break;
                default:
                    break;
            }

        } else {
            RgABCD.setVisibility(View.GONE);
            RgTF.setVisibility(View.VISIBLE);
            if (item.getExAnswer().equals("False")) {
                helper.setText(R.id.tv_answer, "正确答案:错");
            } else {
                helper.setText(R.id.tv_answer, "正确答案:对");
            }

            helper.setText(R.id.rb_true, "对");
            helper.setText(R.id.rb_false, "错");

            switch (item.getCheckString()) {
                case "true":
                    helper.setChecked(R.id.rb_true, true);
                    break;
                case "false":
                    helper.setChecked(R.id.rb_false, true);
                    break;
                default:
                    break;
            }
        }

        RgABCD.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //选中的ID
                switch (checkedId) {
                    case R.id.rb_a:
                        item.setCheckString("A");
                        break;
                    case R.id.rb_b:
                        item.setCheckString("B");
                        break;
                    case R.id.rb_c:
                        item.setCheckString("C");
                        break;
                    case R.id.rb_d:
                        item.setCheckString("D");
                        break;
                    default:
                        break;
                }
            }
        });
        RgTF.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //选中的ID
                switch (checkedId) {
                    case R.id.rb_true:
                        item.setCheckString("true");
                        break;
                    case R.id.rb_false:
                        item.setCheckString("false");
                        break;
                    default:
                        break;
                }
            }
        });


    }
}
