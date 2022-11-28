package com.lyb.image_lib.upload;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadImgBean implements Parcelable {
    public String imgPath;
    public int imgIndex;
    public long imgProgress;
    public String imgUrl;
    public boolean isUploadSuccess;
    public boolean isUploadFailed;

    public boolean isSpuDetailmgUrl;//区分是本地上传的文件还是商品详情接口返回的图片url


    public UploadImgBean(String path, int index, long progress){
        imgPath = path;
        imgIndex = index;
        imgProgress = progress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgPath);
        dest.writeInt(this.imgIndex);
        dest.writeLong(this.imgProgress);
    }

    public UploadImgBean() {
    }

    protected UploadImgBean(Parcel in) {
        this.imgPath = in.readString();
        this.imgIndex = in.readInt();
        this.imgProgress = in.readLong();
    }

    public static final Creator<UploadImgBean> CREATOR = new Creator<UploadImgBean>() {
        @Override
        public UploadImgBean createFromParcel(Parcel source) {
            return new UploadImgBean(source);
        }

        @Override
        public UploadImgBean[] newArray(int size) {
            return new UploadImgBean[size];
        }
    };
}
