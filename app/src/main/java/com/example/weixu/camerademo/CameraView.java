package com.example.weixu.camerademo;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by weixu on 2017/5/17.
 */

public class CameraView implements Camera.PreviewCallback {

    private static final String TAG = "CameraView";
    private WeakReference<SurfaceHolder> mHolder;
    private WeakReference<Activity> mActivity;



    private Camera mCamera;
    int mCurrentCameraType;
    private CameraInterface mCameraInterface;

    //单例用来实现单例的实现
    static CameraView instance = null;
    public static CameraView getInstance() {
        if (instance == null) {
            synchronized (CameraUtils.class) {
                if (instance == null) {
                    instance = new CameraView();
                }
            }
        }
        return instance;
    }

    public void setCameraInterface(CameraInterface cameraInterface) {
        cameraInterface = cameraInterface;
    }

    /**
     * 获取Camera实例
     * @return
     */
    public void startCamera(int id){
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = 0;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == id) {
                cameraId = i;
                mCamera = Camera.open(i);
                mCurrentCameraType = id;
                break;
            }
        }
        if (mCamera == null) {
            throw new RuntimeException("unable to open camera");
        }
    }

    public void changeCamera() {
        stopCamera();
        if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
        }else {
            startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }

        startPreview(mHolder.get(),mActivity.get());
    }

    /**
     * 预览相机
     */
    public void startPreview( SurfaceHolder holder,Activity activity){
        try {
            mHolder=new WeakReference<SurfaceHolder>(holder);
            mActivity= new WeakReference<Activity>(activity);

            //这里要设置相机的一些参数，下面会详细说下
            setupCamera(mCamera);

            //用来设置数据的回调
            mCamera.setPreviewCallback(this);
            mCamera.setPreviewDisplay(holder);

            //亲测的一个方法 基本覆盖所有手机 将预览矫正
            CameraUtils.getInstance().setCameraDisplayOrientation(activity,mCurrentCameraType,mCamera);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        List< String > focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // Autofocus mode is supported 自动对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

//       // 这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
//        Camera.Size previewSize = getPropPreviewSize(parameters.getSupportedPreviewSizes(), 1000);
//        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setPreviewSize(1280, 720);

        camera.setParameters(parameters);
    }

    /**
     * 释放相机资源
     */

    public void stopPreview(){
        if (mCamera !=null) {
            mCamera.stopPreview();
        }
    }

    public void stopCamera(){
        if(mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mCameraInterface !=null) {
            mCameraInterface.onCameraPreviewFrame(data,camera);
        }
    }
}
