package com.example.tacademy.mediaplaysample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.io.File;

public class CaptureImageActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        imageView = (ImageView) findViewById(R.id.image_image);
        Button btn = (Button) findViewById(R.id.btn_capture_image);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = getSaveFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, RC_CAMERA);
            }
        });

        String path = null;
        if (savedInstanceState != null) {
            path = savedInstanceState.getString("savedfile");
            if (!TextUtils.isEmpty(path))
                mSavedFile = new File(path);
        }

        if (!TextUtils.isEmpty(path)) {
            mContentFile = new File(path);
            Glide.with(this)
                    .load(mContentFile)
                    .into(imageView);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSavedFile != null) {
            outState.putString("savedfile", mSavedFile.getAbsolutePath());
        }
        if (mContentFile != null) {
            outState.putString("contentfile", mContentFile.getAbsolutePath());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                mContentFile = mSavedFile;
                Glide.with(this)
                        .load(mContentFile)
                        .into(imageView);
            }
        }
    }

    private static final int RC_CAMERA = 1;

    File mSavedFile, mContentFile;

    private Uri getSaveFile() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        ), "my_image");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mSavedFile = new File(dir, "my_picture_" + System.currentTimeMillis() + ".jpg");
        return Uri.fromFile(mSavedFile);
    }
}
