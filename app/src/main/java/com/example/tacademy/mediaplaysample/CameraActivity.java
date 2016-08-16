package com.example.tacademy.mediaplaysample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {


    Camera mCamera;
    SurfaceHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        SurfaceView displayView = (SurfaceView)findViewById(R.id.surface_preview);
        displayView.getHolder().addCallback(this);

        Button btn = (Button)findViewById(R.id.btn_change);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCamera();
            }
        });

        btn = (Button)findViewById(R.id.btn_effect);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyColorEffect();
            }
        });

        btn = (Button)findViewById(R.id.btn_picture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private void takePicture() {
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

            }
        });
    }

    private void applyColorEffect() {
        Camera.Parameters parameters = mCamera.getParameters();
        final List<String> effects = parameters.getSupportedColorEffects();
        if (effects.size() == 0) {
            Toast.makeText(this, "no color effects", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Color Effect");
        builder.setItems(effects.toArray(new String[effects.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Camera.Parameters param = mCamera.getParameters();
                param.setColorEffect(effects.get(i));
                mCamera.setParameters(param);
            }
        });
        builder.create().show();
    }

    private void changeCamera() {
        if (Camera.getNumberOfCameras() > 1) {
            cameraId = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                    Camera.CameraInfo.CAMERA_FACING_FRONT :
                    Camera.CameraInfo.CAMERA_FACING_BACK;
            stopPreview();
            openCamera();
            startPreview();
        }
    }
    private static final int[] ORIENTATION = { 90, 0, 270 , 180};
    int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private void openCamera() {
        releaseCamera();
        mCamera = Camera.open(cameraId);
        Display display = getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int orientation = ORIENTATION[rotation];
        mCamera.setDisplayOrientation(orientation);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void startPreview() {
        if (mHolder != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopPreview() {
        try {
            mCamera.stopPreview();
        } catch(Exception e) {

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        openCamera();
        mHolder = surfaceHolder;
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        stopPreview();
        mHolder = surfaceHolder;
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopPreview();
        mHolder = null;
        releaseCamera();
    }
}
