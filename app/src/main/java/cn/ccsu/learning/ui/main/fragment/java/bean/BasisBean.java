package cn.ccsu.learning.ui.main.fragment.java.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 基础知识 bean
 */
public class BasisBean {
    /**
     * pageNum : 1
     * pageSize : 5
     * lists : [{"resId":"2","userName":"teacher","userRealname":"蒋团媛","resTitle":"JAVA语言基础","resContent":"","isLeaf":"N","isDel":"N","createTime":"2019-11-14 23:29:37.0","updateTime":"2019-11-14 23:29:37.0","id":"2","pid":"","title":"JAVA语言基础","mid":"1"},{"resId":"25","userName":"teacher","userRealname":"蒋团媛","resTitle":"面向对象技术的基本概念","resContent":"面向对象技术强调在软件开发过程中面向客观世界或问题域中的事物，采用人类在认识客观世界的过程中普遍运用的思维方法，直观、自然地描述客观世界中的有关事物。面向对象技术的基本特征主要有抽象性、封装性、继承性和多态性。","isLeaf":"Y","isDel":"N","createTime":"2019-11-14 23:42:03.0","updateTime":"2019-11-14 23:42:03.0","id":"25","pid":"1","title":"面向对象技术的基本概念","mid":"1"},{"resId":"26","userName":"teacher","userRealname":"蒋团媛","resTitle":"java数据类型","resContent":"Java基本类型共有八种，基本类型可以分为三类，字符类型char，布尔类型boolean以及数值类型byte、short、int、long、float、double。数值类型又可以分为整数类型byte、short、int、long和浮点数类型float、double。JAVA中的数值类型不存在无符号的，它们的取值范围是固定的，不会随着机器硬件环境或者操作系统的改变而改变。实际上，JAVA中还存在另外一种基本类型void，它也有对应的包装类 java.lang.Void，不过我们无法直接对它们进行操作。","isLeaf":"Y","isDel":"N","createTime":"2019-11-14 23:44:24.0","updateTime":"2019-11-14 23:44:24.0","id":"26","pid":"2","title":"java数据类型","mid":"1"}]
     */

    private int pageNum;
    private int pageSize;
    private List<ListsBean> lists;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<ListsBean> getLists() {
        return lists;
    }

    public void setLists(List<ListsBean> lists) {
        this.lists = lists;
    }

    public static class ListsBean implements Parcelable {
        /**
         * resId : 2
         * userName : teacher
         * userRealname : 蒋团媛
         * resTitle : JAVA语言基础
         * resContent :
         * isLeaf : N
         * isDel : N
         * createTime : 2019-11-14 23:29:37.0
         * updateTime : 2019-11-14 23:29:37.0
         * id : 2
         * pid :
         * title : JAVA语言基础
         * mid : 1
         */

        private String resId;
        private String userName;
        private String userRealname;
        private String resTitle;
        private String resContent;
        private String isLeaf;
        private String isDel;
        private String createTime;
        private String updateTime;
        private String id;
        private String pid;
        private String title;
        private String mid;

        public ListsBean() {
        }

        protected ListsBean(Parcel in) {
            resId = in.readString();
            userName = in.readString();
            userRealname = in.readString();
            resTitle = in.readString();
            resContent = in.readString();
            isLeaf = in.readString();
            isDel = in.readString();
            createTime = in.readString();
            updateTime = in.readString();
            id = in.readString();
            pid = in.readString();
            title = in.readString();
            mid = in.readString();
        }

        public static final Creator<ListsBean> CREATOR = new Creator<ListsBean>() {
            @Override
            public ListsBean createFromParcel(Parcel in) {
                return new ListsBean(in);
            }

            @Override
            public ListsBean[] newArray(int size) {
                return new ListsBean[size];
            }
        };

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(resId);
            dest.writeString(userName);
            dest.writeString(userRealname);
            dest.writeString(resTitle);
            dest.writeString(resContent);
            dest.writeString(isLeaf);
            dest.writeString(isDel);
            dest.writeString(createTime);
            dest.writeString(updateTime);
            dest.writeString(id);
            dest.writeString(pid);
            dest.writeString(title);
            dest.writeString(mid);
        }
    }
}
