package com.example.weixu.camerademo;

import android.hardware.Camera;

/**
 * Created by weixu on 2017/5/17.
 */

public interface CameraInterface {
    public void onCameraPreviewFrame(byte[] data, Camera camera);
}
