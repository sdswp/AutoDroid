package com.gk.touchstone.taskview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.gk.touchstone.R;
import com.gk.touchstone.core.BaseActivity;
import com.gk.touchstone.utils.Utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class VideoPlay extends BaseActivity {
	private AudioManager mAudioManager = null;
	// private TextView playtime = null;
	// private TextView durationTime = null;
	private SurfaceView surfaceView;
	private File videofile;
	private MediaPlayer mediaPlayer;
	private Handler handler = null;
	private int position;
	private int currentPosition;
	private Uri uri;
	private Utils us;
	private String fileName = "movie.mp4";
	private boolean pause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setBaseContentView();//(R.layout.video_play);

		us = new Utils(this);

		mediaPlayer = new MediaPlayer();

		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
		
		surfaceView.getHolder().setFixedSize(320, 300);
		

		Button playbutton = (Button) this.findViewById(R.id.playBtn);
		// playbutton.setAlpha(00000000);
		Button pausebutton = (Button) this.findViewById(R.id.pauseBtn);
		Button resetbutton = (Button) this.findViewById(R.id.resetBtn);
		Button stopbutton = (Button) this.findViewById(R.id.stopBtn);
		playbutton.setOnClickListener(new playVideo());
		pausebutton.setOnClickListener(new pauseVideo());
		resetbutton.setOnClickListener(new resetVideo());
		stopbutton.setOnClickListener(new stopVideo());

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		position = savedInstanceState.getInt("position");
		String path = savedInstanceState.getString("path");
		if (path != null && !"".equals(path)) {
			videofile = new File(path);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("position", position);
		if (videofile != null)
			outState.putString("path", videofile.getAbsolutePath());

		super.onSaveInstanceState(outState);
	}

	private class playVideo implements OnClickListener {
		@Override
		public void onClick(View v) {
			videofile = new File(us.getAppSDCardPath() + fileName);
			if (!videofile.exists()) {
				try {
					us.saveRawToSDCard(fileName, R.raw.movie);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			play();
		}
	};

	private class stopVideo implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mediaPlayer.isPlaying()) {

				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	};

	private class pauseVideo implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				pause = true;
			} else {
				if (pause) {
					mediaPlayer.start();
					pause = false;
				}
			}
		}
	};

	private class resetVideo implements OnClickListener {
		@Override
		public void onClick(View v){
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.seekTo(0);

			} else {
				play();

			}
		}
	};

	private void play() {
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDisplay(surfaceView.getHolder());

			FileInputStream fis = new FileInputStream(new File(
					us.getAppSDCardPath() + fileName));
			mediaPlayer.setDataSource(fis.getFD());
			// mediaPlayer.setDataSource(us.getSDCardFolderPath() + fileName);
			setup();
			mediaPlayer.start();
		} catch (Exception e) {
			System.out.println("play is wrong");
		}
	}

	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;

		int second = time % 60;
		return String.format("%02d:%02d", minute, second);
	}

	public String toFotmat(int num) {
		return String.format("%02d", num);
	}

	private void setup() {
		init();
		try {
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(final MediaPlayer mp) {
					// seekbar.setMax(mp.getDuration());
					handler.sendEmptyMessage(1);
					// playtime.setText(toTime(mp.getCurrentPosition()));
					// durationTime.setText(toTime(mp.getDuration()));
					mp.seekTo(currentPosition);

					// handler.sendEmptyMessage(2);
					// sound.setText(toFotmat(CurrentSound) + "/"
					// + toFotmat(MaxSound));

				}
			});
		} catch (Exception e) {
			System.out.println("wrong");
		}
	}

	private void init() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if (mediaPlayer != null)
						currentPosition = mediaPlayer.getCurrentPosition();
					// seekbar.setProgress(currentPosition);
					// playtime.setText(toTime(currentPosition));
					handler.sendEmptyMessage(1);
					break;
				default:
					break;
				}

			}
		};
	}
}
