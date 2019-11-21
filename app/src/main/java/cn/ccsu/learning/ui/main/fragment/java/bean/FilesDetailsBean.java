package cn.ccsu.learning.ui.main.fragment.java.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FilesDetailsBean implements Parcelable {
    private String fileId;
    private String resId;
    private String fileName;
    private String fileAlias;
    private String filePath;
    private String createUser;
    private String createTime;
    private Object updateTime;
    private String isDel;

    public FilesDetailsBean() {
    }

    protected FilesDetailsBean(Parcel in) {
        fileId = in.readString();
        resId = in.readString();
        fileName = in.readString();
        fileAlias = in.readString();
        filePath = in.readString();
        createUser = in.readString();
        createTime = in.readString();
        isDel = in.readString();
    }

    public static final Creator<FilesDetailsBean> CREATOR = new Creator<FilesDetailsBean>() {
        @Override
        public FilesDetailsBean createFromParcel(Parcel in) {
            return new FilesDetailsBean(in);
        }

        @Override
        public FilesDetailsBean[] newArray(int size) {
            return new FilesDetailsBean[size];
        }
    };

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileAlias() {
        return fileAlias;
    }

    public void setFileAlias(String fileAlias) {
        this.fileAlias = fileAlias;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Object getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Object updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    @Override
    public String toString() {
        return "FilesDetailsBean{" +
                "fileId='" + fileId + '\'' +
                ", resId='" + resId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileAlias='" + fileAlias + '\'' +
                ", filePath='" + filePath + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime=" + updateTime +
                ", isDel='" + isDel + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileId);
        dest.writeString(resId);
        dest.writeString(fileName);
        dest.writeString(fileAlias);
        dest.writeString(filePath);
        dest.writeString(createUser);
        dest.writeString(createTime);
        dest.writeString(isDel);
    }
}
