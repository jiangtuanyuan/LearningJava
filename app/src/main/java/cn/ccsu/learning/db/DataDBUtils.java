package cn.ccsu.learning.db;


import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.ccsu.learning.app.User;
import cn.ccsu.learning.ui.main.fragment.java.bean.BasisBean;

/**
 * LitePal 数据库操作类
 * jiang
 */
public class DataDBUtils {
    /**
     * 获取数据库里面的所有记录
     *
     * @return
     */
    public static List<ShowLogs> getShowLogsAll() {
        List<ShowLogs> list = new ArrayList<>();
        list.addAll(LitePal.findAll(ShowLogs.class));
        return list;
    }

    /**
     * 获取数据库里面指定的用户浏览记录
     *
     * @return
     */
    public static List<ShowLogs> getUserShowLogsAll(String userID) {
        List<ShowLogs> list = LitePal.where("userID = ?", userID).order("id desc").find(ShowLogs.class);
        return list;
    }

    /**
     * 新增一个条日志
     *
     * @param
     * @return 成功返回新的的ID 否则返回0
     */
    public static int addShowLog(ShowLogs showLogs) {
        if (showLogs.save()) {
            return showLogs.getId();
        } else {
            return 0;
        }
    }

    /**
     * 新增一条浏览记录
     *
     * @param bean
     * @param mIdStrID
     * @return
     */
    public static int addShowLog(BasisBean.ListsBean bean, String mIdStrID, String userID) {
        List<ShowLogs> list = LitePal.where("userID = ? and resId = ?", userID, bean.getResId()).find(ShowLogs.class);
        if (list != null && list.size() == 0) {
            //新增一条日志记录
            ShowLogs mShowLogs = new ShowLogs();
            //用户ID
            mShowLogs.setUserID(User.getInstance().getUserId());
            //用户权限ID
            mShowLogs.setRid(User.getInstance().getRid());
            mShowLogs.setmIdStrID(mIdStrID);//父级序号
            //实体类相关
            mShowLogs.setResId(bean.getResId());//资源ID
            mShowLogs.setUserName(bean.getUserName());
            mShowLogs.setUserRealname(bean.getUserRealname());
            mShowLogs.setResTitle(bean.getResTitle());
            mShowLogs.setResContent(bean.getResContent());
            mShowLogs.setIsLeaf(bean.getIsLeaf());
            mShowLogs.setIsDel(bean.getIsDel());
            mShowLogs.setCreateTime(bean.getCreateTime());
            mShowLogs.setUpdateTime(bean.getUpdateTime());
            mShowLogs.setPid(bean.getPid());
            mShowLogs.setTitle(bean.getTitle());
            mShowLogs.setMid(bean.getMid());
            if (mShowLogs.save()) {
                return mShowLogs.getId();
            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }
}
