package cn.ccsu.learning.ui.main.fragment.test.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TestBean implements Parcelable {

    /**
     * typeId : 1
     * typeName : Java基础测试
     * typeUrl:
     */

    private String typeId;
    private String typeName;
    private String typeUrl;

    public TestBean() {
    }

    protected TestBean(Parcel in) {
        typeId = in.readString();
        typeName = in.readString();
        typeUrl = in.readString();
    }

    public static final Creator<TestBean> CREATOR = new Creator<TestBean>() {
        @Override
        public TestBean createFromParcel(Parcel in) {
            return new TestBean(in);
        }

        @Override
        public TestBean[] newArray(int size) {
            return new TestBean[size];
        }
    };

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeUrl() {
        return typeUrl;
    }

    public void setTypeUrl(String typeUrl) {
        this.typeUrl = typeUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(typeId);
        dest.writeString(typeName);
        dest.writeString(typeUrl);
    }
}
