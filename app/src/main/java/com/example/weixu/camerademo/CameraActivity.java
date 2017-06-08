package com.example.weixu.camerademo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener,CameraInterface{


    private SurfaceHolder mHolder;
    private SurfaceView mSurfaceView;

    //屏幕宽高
    private int screenWidth;
    private int screenHeight;

    private Context context;
    private Button mButton;
    private boolean isRecord= false;
    private Button mChangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        setUpView();
        setUpData();
    }

    private void setUpView() {
        mButton = (Button)findViewById(R.id.record_btn);
        mButton.setOnClickListener(this);

        mChangeButton = (Button)findViewById(R.id.record_btn_change);
        mChangeButton.setOnClickListener(this);

        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);

        context = this;
    }

    private void setUpData() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        Button button = (Button)v;
        if (button ==mButton) {
            if (isRecord){
                 mButton.setText("开始");
                 isRecord = false;
            }
            else {
                mButton.setText("结束");
                isRecord = true;
            }
        }
        else if(button == mChangeButton) {
            CameraView.getInstance().changeCamera();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //在surface创建的时候开启相机预览
        CameraView.getInstance().startCamera(1);
        CameraView.getInstance().startPreview(holder,this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //在相机改变的时候调用此方法， 此时应该先停止预览， 然后重新启动
        CameraView.getInstance().stopPreview();
        CameraView.getInstance().startPreview(holder,this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //在destroy的时候释放相机资源
        CameraView.getInstance().stopCamera();
    }

    @Override
    public void onCameraPreviewFrame(byte[] data, Camera camera) {

    }
}

