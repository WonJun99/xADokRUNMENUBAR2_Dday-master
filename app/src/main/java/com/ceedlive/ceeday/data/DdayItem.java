package com.ceedlive.ceeday.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Java에서는 Value Object를 쉽게 직렬화 하기 위해 Serialiable이라는 Interface가 있다.
 * Marker Interface로서 단순히 implement하는 것만으로도 자바가상머신에게 직렬화가 가능하다는 것을 알려주기 때문에 사용하는게 매우 쉬운편이다.
 *
 * reference
 * https://lx5475.github.io/2016/06/27/android-parcelable/
 * https://medium.com/udacity-android-nanodegree/android-parcelable-vs-serializable-f2d7d2f4a2a
 * https://medium.com/@limgyumin/parcelable-vs-serializable-%EC%A0%95%EB%A7%90-serializable%EC%9D%80-%EB%8A%90%EB%A6%B4%EA%B9%8C-bc2b9a7ba810
 */
public class DdayItem implements Parcelable {
    private String uniqueKey;
    private int _id;
    private String title;
    private String description;
    private String date;
    private String diffDays;
    private int notification;

    private boolean isChecked;
    private boolean isVisibleDetail;

    // TODO 생성자 빌더 패턴 알아보기

    public static class Builder {
        private String uniqueKey;
        private int _id;

        private String title;
        private String description;
        private String date;

        private String diffDays;
        private int notification;

        private boolean isChecked;
        private boolean isVisibleDetail;

        public Builder(String date, String title, String description) {
            this.date = date;
            this.title = title;
            this.description = description;
        }

        public Builder uniqueKey(String uniqueKey) {
            this.uniqueKey = uniqueKey;
            return this;
        }
        public Builder rowId(int _id) {
            this._id = _id;
            return this;
        }
        public Builder diffDays(String diffDays) {
            this.diffDays = diffDays;
            return this;
        }
        public Builder notification(int notification) {
            this.notification = notification;
            return this;
        }
        public Builder isChecked(boolean isChecked) {
            this.isChecked = isChecked;
            return this;
        }
        public Builder isVisibleDetail(boolean isVisibleDetail) {
            this.isVisibleDetail = isVisibleDetail;
            return this;
        }

        public DdayItem build() {
            return new DdayItem(this);
        }
    }

    public DdayItem(Builder builder) {
        this.uniqueKey = builder.uniqueKey;
        this._id = builder._id;
        this.title = builder.title;
        this.description = builder.description;
        this.date = builder.date;
        this.diffDays = builder.diffDays;
        this.notification = builder.notification;
        this.isChecked = builder.isChecked;
        this.isVisibleDetail = builder.isVisibleDetail;
    }

    protected DdayItem(Parcel in) {
        uniqueKey = in.readString();
        _id = in.readInt();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        diffDays = in.readString();
        notification = in.readInt();
        isChecked = in.readByte() != 0;
        isVisibleDetail = in.readByte() != 0;
    }

    public static final Creator<DdayItem> CREATOR = new Creator<DdayItem>() {
        @Override
        public DdayItem createFromParcel(Parcel in) {
            return new DdayItem(in);
        }

        @Override
        public DdayItem[] newArray(int size) {
            return new DdayItem[size];
        }
    };

    public int get_id() { return _id; }

    public void set_id(int _id) { this._id = _id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getDiffDays() {
        return diffDays;
    }

    public void setDiffDays(String diffDays) {
        this.diffDays = diffDays;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean getIsVisibleDetail() {
        return isVisibleDetail;
    }

    public void setIsVisibleDetail(boolean isVisibleDetail) {
        this.isVisibleDetail = isVisibleDetail;
    }

    @Override
    public String toString() {
        return "DdayItem{" +
                "uniqueKey='" + uniqueKey + '\'' +
                ", _id=" + _id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", diffDays='" + diffDays + '\'' +
                ", notification=" + notification +
                ", isChecked=" + isChecked +
                ", isVisibleDetail=" + isVisibleDetail +
                '}';
    }

    // Parcelable의 사용법은 조금 어렵다. 필수적으로 정의 해야하는 메소드가 두가지가 있다.

    /**
     * Parcel의 내용을 기술한다.
     * FileDescriptor 같은 특별한 객체가 들어가면 이 부분을 통해서 알려줘야한다.
     * 보통은 0을 리턴해준다.
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcel안에 데이터를 넣는 작업을 한다.
     *
     * writeToParcel에서 Parcel 목적지(dest)에다가 데이터를 포맷에 따라 차곡차곡 넣어주고,
     * Parcel이 있는 생성자에서 순서대로 꺼내오면 된다. (두 개의 순서는 같아야 한다!)
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uniqueKey);
        parcel.writeInt(_id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(diffDays);
        parcel.writeInt(notification);
        parcel.writeByte((byte) (isChecked ? 1 : 0));
        parcel.writeByte((byte) (isVisibleDetail ? 1 : 0));
    }
}
