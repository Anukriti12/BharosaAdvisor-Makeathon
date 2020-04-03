package com.ap.bharosaadvisor;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jaedongchicken.ytplayer.JLog;
import com.jaedongchicken.ytplayer.YoutubePlayerView;
import com.jaedongchicken.ytplayer.model.PlaybackQuality;
import com.jaedongchicken.ytplayer.model.YTParams;


public class VideoPlayerActivity extends AppCompatActivity
{
    private YoutubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        youtubePlayerView =  findViewById(R.id.fullscreen_video);
        //youtubePlayerView.setAutoPlayerHeight(this);

        YTParams params = new YTParams();
        params.setVolume(100);
        params.setPlaybackQuality(PlaybackQuality.small);

        if (!isConnected(VideoPlayerActivity.this)) {
            buildDialog(VideoPlayerActivity.this).show();
            youtubePlayerView.setVisibility(View.GONE);
        }
        youtubePlayerView.initializeWithCustomURL(getIntent().getStringExtra("url"), params, new YoutubePlayerView.YouTubeListener(){



            @Override
            public void onReady() {
                // when player is ready.
                JLog.i("onReady()");
            }

            @Override
            public void onStateChange(YoutubePlayerView.STATE state) {
                /**
                 * YoutubePlayerView.STATE
                 *
                 * UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, CUED, NONE
                 *
                 */

                JLog.i("onStateChange(" + state + ")");
            }

            @Override
            public void onPlaybackQualityChange(String arg) {


            }

            @Override
            public void onPlaybackRateChange(String arg) {

            }

            @Override
            public void onError(String arg) {

                    Toast.makeText(VideoPlayerActivity.this, R.string.error_document, Toast.LENGTH_SHORT).show();
                    finish();

            }

            @Override
            public void onApiChange(String arg) {

            }

            @Override
            public void onCurrentSecond(double second) {

            }

            @Override
            public void onDuration(double duration) {
                String message = "onDuration(" + duration + ")";
                JLog.i(message);
            }

            @Override
            public void logs(String log) {
                // javascript debug log. you don't need to use it.
                JLog.d(log);
            }



        });
         youtubePlayerView.play();
         youtubePlayerView.onLoadVideo(getIntent().getStringExtra("url"),0);
         youtubePlayerView.seekToMillis(0);


    }


    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

             //   finish();
                //moveTaskToBack(true);
                VideoPlayerActivity.super.onBackPressed();
            }
        });
       builder.setCancelable(false);
        return builder;
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause video when on the background mode.
        youtubePlayerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // this is optional but you need.
        youtubePlayerView.destroy();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
