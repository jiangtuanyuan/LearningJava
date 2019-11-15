package cn.ccsu.learning.ui.main.fragment.mine;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.base.BaseFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineFragment extends BaseFragment {
    private static final String ARG_MSG = "mine";
    @BindView(R.id.civ_head_image)
    CircleImageView civHeadImage;
    @BindView(R.id.tv_user_type_name)
    TextView tvUserTypeName;
    @BindView(R.id.rl_head_image_layout)
    RelativeLayout rlHeadImageLayout;
    @BindView(R.id.mine_person_data_layout)
    LinearLayout minePersonDataLayout;
    @BindView(R.id.ll_bottom_layou)
    LinearLayout llBottomLayou;
    @BindView(R.id.rl_record_layout)
    RelativeLayout rlRecordLayout;
    @BindView(R.id.rl_upload_layout)
    RelativeLayout rlUploadLayout;
    @BindView(R.id.rl_download_layout)
    RelativeLayout rlDownloadLayout;
    @BindView(R.id.rl_user_layout)
    RelativeLayout rlUserLayout;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    public static MineFragment newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void setFragmentData() {
        /*switch (User.getInstance().getmUserType()) {
            case ADMIN://管理员
                rlRecordLayout.setVisibility(View.GONE);
                rlDownloadLayout.setVisibility(View.GONE);

                rlUploadLayout.setVisibility(View.VISIBLE);
                rlUserLayout.setVisibility(View.VISIBLE);
                tvUserTypeName.setText("管理员");
                break;
            case TEACHER://老师
                rlUserLayout.setVisibility(View.GONE);
                rlRecordLayout.setVisibility(View.GONE);

                rlDownloadLayout.setVisibility(View.VISIBLE);
                rlUploadLayout.setVisibility(View.VISIBLE);
                tvUserTypeName.setText("老师");
                break;
            case STUDENTS://学生
                rlUserLayout.setVisibility(View.GONE);
                rlUploadLayout.setVisibility(View.GONE);

                rlDownloadLayout.setVisibility(View.VISIBLE);
                rlRecordLayout.setVisibility(View.VISIBLE);
                tvUserTypeName.setText("学生");
                break;
            default:
                break;
        }*/

    }

    @OnClick({R.id.mine_person_data_layout, R.id.rl_record_layout, R.id.rl_upload_layout, R.id.rl_download_layout, R.id.rl_user_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mine_person_data_layout:
                MineDetilsActivity.actionStart(getContext());
                break;
            case R.id.rl_record_layout:
                break;
            case R.id.rl_upload_layout:
                break;
            case R.id.rl_download_layout:
                break;
            case R.id.rl_user_layout:
                break;
            default:
                break;
        }
    }
}
