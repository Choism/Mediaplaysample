package com.example.tacademy.mediaplaysample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = (VideoView)findViewById(R.id.video_view);
        MediaController controller = new MediaController(this);
        videoView.setMediaController(controller);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_video_list) {
            startActivityForResult(new Intent(this, VideoListActivity.class), 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                videoView.setVideoURI(uri);
                videoView.start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
