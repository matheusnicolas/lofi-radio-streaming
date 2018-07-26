package com.streaming.lofi.lo_firadio;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dyanamitechetan.vusikview.VusikView;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener{

    private ImageButton btn_play_pause;
    private SeekBar seekBar;
    private TextView textView;

    private VusikView musicView;

    private MediaPlayer mediaPlayer;
    private int mediaFileLenght;
    private int realtimeLenght;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicView = (VusikView) findViewById(R.id.musicView);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setMax(99);

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mediaPlayer.isPlaying()){
                    SeekBar seekBar = (SeekBar)view;
                    int playPosition = (mediaFileLenght/100)*seekBar.getProgress();
                    mediaPlayer.seekTo(playPosition);
                }
                return false;
            }
        });

        textView = (TextView)findViewById(R.id.timer);


        btn_play_pause = (ImageButton) findViewById(R.id.btn_play_pause);
        btn_play_pause.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);

                AsyncTask<String, String, String> mp3Play = new AsyncTask<String, String, String>() {

                    @Override
                    protected void onPreExecute() {
                        mDialog.setMessage("Aguarde por favor!");
                        mDialog.show();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        try {
                            mediaPlayer.setDataSource(strings[0]);
                            mediaPlayer.prepare();
                        } catch (Exception e) {

                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mediaFileLenght = mediaPlayer.getDuration();
                        realtimeLenght = mediaFileLenght;
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            btn_play_pause.setImageResource(R.drawable.ic_pause);
                        } else {
                            mediaPlayer.pause();
                            btn_play_pause.setImageResource(R.drawable.ic_play);
                        }

                        updateSeekBar();
                        mDialog.dismiss();
                    }
                };

                mp3Play.execute("http://picosong.com/cdn/3dc10d400fa83bd531b3c177fa505452.mp3");
                musicView.start();
                //"http://mic.duytan.edu.vn:86/ncs.mp3"
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

    }
        private void updateSeekBar(){
            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / mediaFileLenght) * 100));
            if(mediaPlayer.isPlaying()){
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        updateSeekBar();
                        realtimeLenght -= 1000;
                        textView.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(realtimeLenght),
                                TimeUnit.MILLISECONDS.toSeconds(realtimeLenght) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(realtimeLenght))));

                    }
                };
                handler.postDelayed(updater, 1000);
            }

        }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        seekBar.setSecondaryProgress(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
            btn_play_pause.setImageResource(R.drawable.ic_play);
            musicView.stopNotesFall();

    }
}