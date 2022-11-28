package com.lyb.image_lib;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageUpload extends UploadHelper {

    private Handler mHandler;
    private String imgPath;
    private int imgIndex;
    private UploadImgBean uploadImgBean;
    private long attPicValueId;

    public ImageUpload(Handler handler) {
        mHandler = handler;

    }

    public ImageUpload(Handler handler, String path, int idx) {
        mHandler = handler;
        imgPath = path;
        imgIndex = idx;
        uploadImgBean = new UploadImgBean(path, imgIndex, 0);
    }

    public ImageUpload(Handler handler, String path, int idx, long attPicValueId) {
        mHandler = handler;
        imgPath = path;
        imgIndex = idx;
        this.attPicValueId = attPicValueId;
        uploadImgBean = new UploadImgBean(path, imgIndex, 0);
    }


    @Override
    public void uploadSuccess(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("id");
            String msg = jsonObject.getString("msg");
            Message message = new Message();
            message.obj = msg;
            if (code != 1) {
                message.what = MsgConstants.MSG_FILE_UPLOAD_FAIL;
            }else {
                message.what = MsgConstants.MSG_FILE_UPLOAD_SUCCESS;
            }
            uploadImgBean.imgUrl = msg;
            uploadImgBean.isUploadSuccess = true;
            message.obj = uploadImgBean;
            Bundle bundle = new Bundle();
            bundle.putLong("attPicValueId", attPicValueId);
            message.setData(bundle);
            mHandler.sendMessage(message);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            Message msg = new Message();
            msg.what = MsgConstants.MSG_FILE_UPLOAD_FAIL;
            uploadImgBean.isUploadFailed = true;
            msg.obj = uploadImgBean;
            Bundle bundle = new Bundle();
            bundle.putLong("attPicValueId", attPicValueId);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void uploadFail() {

        Message msg = new Message();
        msg.what = MsgConstants.MSG_FILE_UPLOAD_FAIL;
        uploadImgBean.isUploadFailed = true;
        msg.obj = uploadImgBean;
        mHandler.sendMessage(msg);

    }

    @Override
    public void uploadImgProgress(long progress) {
        if(progress > 100){
            return;
        }
        Message msg = new Message();
        msg.what = MsgConstants.MSG_FILE_UPLOAD_PROGRESS;
        uploadImgBean.imgProgress = progress;
        msg.obj = uploadImgBean;
        mHandler.sendMessage(msg);

    }

}
