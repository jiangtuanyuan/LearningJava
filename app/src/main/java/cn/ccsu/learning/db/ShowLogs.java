package cn.ccsu.learning.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class ShowLogs extends LitePalSupport {
    @Column
    private int id; //不可构造set方法 自增ID


    private String userID;//用户ID
    private String rid;//角色ID
    private String mIdStrID;//上一层的父级序号
    //相关的Ben属性
    private String resId;//资源ID
    private String userName;
    private String userRealname;
    private String resTitle;
    private String resContent;
    private String isLeaf;
    private String isDel;
    private String createTime;
    private String updateTime;
    private String pid;
    private String title;
    private String mid;

    public String getmIdStrID() {
        return mIdStrID;
    }

    public void setmIdStrID(String mIdStrID) {
        this.mIdStrID = mIdStrID;
    }

    public int getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRealname() {
        return userRealname;
    }

    public void setUserRealname(String userRealname) {
        this.userRealname = userRealname;
    }

    public String getResTitle() {
        return resTitle;
    }

    public void setResTitle(String resTitle) {
        this.resTitle = resTitle;
    }

    public String getResContent() {
        return resContent;
    }

    public void setResContent(String resContent) {
        this.resContent = resContent;
    }

    public String getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
}
