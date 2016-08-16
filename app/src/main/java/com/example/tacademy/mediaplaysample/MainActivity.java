package com.example.tacademy.mediaplaysample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mPlayer;

    enum PlayState {
        IDLE,
        INITIALIED,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED,
        ERROR,
        RELEASED
    }

    PlayState mState;
    SeekBar progressView, volumeView;
    AudioManager mAM;
    CheckBox muteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView = (SeekBar) findViewById(R.id.seek_progress);
        volumeView = (SeekBar)findViewById(R.id.seek_volume);
        //볼륨 셋팅
        mAM = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int max = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeView.setMax(max);
        //볼륨 Progress 와 연결 짖는 장소
        int current = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeView.setProgress(current);
        volumeView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean froUser) {
                if(froUser){
                    mAM.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        muteView = (CheckBox)findViewById(R.id.check_mute);
        muteView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheacked) {
                if(isCheacked){
                    mHandler.removeCallbacks(volumeUp);
                    mHandler.post(volumeDown);
                }else{
                    mHandler.removeCallbacks(volumeDown);
                    mHandler.post(volumeUp);
                    }
            }
        });




        mPlayer = MediaPlayer.create(this, R.raw.winter_blues);
        mState = PlayState.PREPARED;

        progressView.setMax(mPlayer.getDuration()); //듀레이션은 프리페어드 상태에서만 얻을 수 있다.

        Button btn = (Button) findViewById(R.id.btn_play);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });
        btn = (Button) findViewById(R.id.btn_pause);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause();
            }
        });




        btn = (Button) findViewById(R.id.btn_stop);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });

        progressView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = -1; //프로그레스의 상태

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.progress = progress; //사용자의 의해서 바끼는 경우
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progress = -1;
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { //손을 놨을때
               if(progress != -1){
                if (mState == PlayState.STARTED) {
                    mPlayer.seekTo(progress);
                }
            }

            isSeeking=false;
        }

        });
        btn = (Button)findViewById(R.id.btn_get);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, MusicListActivity.class), RC_MUSIC_LIST);
            }
        });
    }

    private static final int RC_MUSIC_LIST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_MUSIC_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                mPlayer.reset();
                mState = PlayState.IDLE;
                Uri uri = data.getData();
                try {
                    mPlayer.setDataSource(this, uri);
                    mState = PlayState.INITIALIED;
                    mPlayer.prepare();
                    mState = PlayState.PREPARED;
                    progressView.setMax(mPlayer.getDuration());
                    progressView.setProgress(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    boolean isSeeking = false; //시크바를 위치한곳에서 부터 실행하기위해 셋팅
    Handler mHandler = new Handler(Looper.getMainLooper());
//상태를 읽어서 알려주는 곳
    float currentVolume = 1.0f;

    Runnable volumeUp = new Runnable() {
        @Override
        public void run() {
            if (currentVolume < 1.0f) {
                mPlayer.setVolume(currentVolume, currentVolume);
                currentVolume += 0.1f;
                mHandler.postDelayed(this, 100);
            } else {
                currentVolume = 1.0f;
                mPlayer.setVolume(currentVolume, currentVolume);
            }
        }
    };

    Runnable volumeDown = new Runnable() {
        @Override
        public void run() {
            if (currentVolume > 0) {
                mPlayer.setVolume(currentVolume, currentVolume);
                currentVolume -= 0.1f;
                mHandler.postDelayed(this, 100);
            } else {
                currentVolume = 0;
                mPlayer.setVolume(currentVolume, currentVolume);
            }
        }
    };



    //상태를 읽어서 알려주는 곳
Runnable progressRunnable = new Runnable(){
    @Override
    public void run() {
    if (mState == PlayState.STARTED) {
        if (!isSeeking) {
            int position = mPlayer.getCurrentPosition();
            progressView.setProgress(position);
        }
        mHandler.postDelayed(this, 100);
    }
}
};


    private void play() {
        if (mState == PlayState.INITIALIED || mState == PlayState.STOPPED) {
            try {
                mPlayer.prepare();
                mState = PlayState.PREPARED;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mState == PlayState.PREPARED || mState == PlayState.PAUSED) {
            mPlayer.seekTo(progressView.getProgress());
            mPlayer.start();
            mState = PlayState.STARTED;
            mHandler.post(progressRunnable);
        }
    }

    private void pause() {
        if (mState == PlayState.STARTED) {
            mPlayer.pause();
            mState = PlayState.PAUSED;
        }
    }

    private void stop() {
        if (mState == PlayState.STARTED || mState == PlayState.PAUSED) {
            mPlayer.stop();
            mState = PlayState.STOPPED;
            progressView.setProgress(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
        mState = PlayState.RELEASED;
        mPlayer = null;
    }
}