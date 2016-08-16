package com.example.tacademy.mediaplaysample;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;

import java.io.File;
import java.io.IOException;

public class RecorderActivity extends AppCompatActivity implements
        SurfaceHolder.Callback {

    SurfaceView displayView;
    Gallery gallery;
    GalleryAdapter mAdapter;
    SurfaceHolder mDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        displayView = (SurfaceView)findViewById(R.id.surface_display);
        gallery = (Gallery)findViewById(R.id.gallery);
        mAdapter = new GalleryAdapter(this);
        gallery.setAdapter(mAdapter);
        displayView.getHolder().addCallback(this);
        Button btn = (Button)findViewById(R.id.btn_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

        btn = (Button)findViewById(R.id.btn_stop);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
    }

    MediaRecorder mRecorder;
    boolean isUseProfile = false;
    private void startRecording() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            if (isUseProfile) {
                mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            } else {
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

                mRecorder.setVideoSize(320, 240);
            }

            File file = getSavedFile();
            mRecorder.setOutputFile(file.getAbsolutePath());
            mRecorder.setMaxDuration(60 * 1000);
            mRecorder.setMaxFileSize(100 * 1024 * 1024);

            if (mDisplay != null) {
                mRecorder.setPreviewDisplay(mDisplay.getSurface());
            }

            try {
                mRecorder.prepare();
                mRecorder.start();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mRecorder != null) {
                mSavedFile = null;
                mRecorder.release();
                mRecorder = null;
            }
        }
    }

    File mSavedFile;

    public File getSavedFile() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "my_video");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mSavedFile = new File(dir, "S_" + System.currentTimeMillis() + ".mpeg");
        return mSavedFile;
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(mSavedFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            mAdapter.add(thumbnail);

            addToMediaStore(mSavedFile);
        }
    }

    private void addToMediaStore(File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, "my video");
        values.put(MediaStore.Video.Media.DESCRIPTION, "my video description");
        values.put(MediaStore.Video.Media.TITLE, "video title");
        values.put(MediaStore.Video.Media.DATA, mSavedFile.getAbsolutePath());
        values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mpeg");
        Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mDisplay = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mDisplay = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mDisplay = null;
        stopRecording();
    }
}
