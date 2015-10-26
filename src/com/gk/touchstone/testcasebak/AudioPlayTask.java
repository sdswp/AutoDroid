package com.gk.touchstone.testcasebak;

import com.gk.touchstone.R;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class AudioPlayTask extends TestCase{
	private Handler testHandler;
	private AudioManager audioMg;
	
	private MediaPlayer mediaPlayer;
	
	private int internalTime;
	private int audioplayTime;
	
	private final static int MSG_CHANGE_AUDIO_STATE = 30;
	private final static int MSG_LISTEN_AUDIO_STATE = 31;
	private final static int MSG_AUDIO_TEST_END = 32;
	private int testCount = 0;
	
	public AudioPlayTask(Context context, Task task) {
		super(context, task);
		// TODO Auto-generated constructor stub
		testHandler = new myHandler();
		
		initData();
	}
	
	private void initData(){
		internalTime = getIntValue("audioPlanInterval");
		audioplayTime = getIntValue("audioPlanTimes");
		
		timeCount = (audioplayTime / internalTime)/2;
		mediaPlayer = MediaPlayer.create(context, R.raw.soundtest);
		audioMg = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		testState();
	}
	
	@Override
	public void Start() {
		// TODO Auto-generated method stub
		initData();
		isRunning = true;
		if (mThread == null){
			mThread = new AudioThread();
			mThread.start();
		}
		startTest();
	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		audioMg.setMode(AudioManager.MODE_NORMAL);
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		audioMg.setMode(AudioManager.MODE_NORMAL);
		// TODO Auto-generated method stub
		stopTask();
	}
	
	class AudioThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				while (true) {
					if (isRunning) {
						if (testCount < audioplayTime) {
							testHandler.sendEmptyMessage(MSG_LISTEN_AUDIO_STATE);
							if (testCount % internalTime == 0) {
								testHandler.sendEmptyMessage(MSG_CHANGE_AUDIO_STATE);
							}
						} else {
							testHandler.sendEmptyMessage(MSG_AUDIO_TEST_END);
						}
						sleep(1000);
						testCount++;
						System.out.println(String.valueOf(testCount));
					} else {
						sleep(1000);
					}
				}
			} catch (Exception e) {
				writeResult(timeCount, Constants.FAIL, "Play Sound Thread Error: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LISTEN_AUDIO_STATE: {
				testState();
				break;
			}
			case MSG_CHANGE_AUDIO_STATE: {
				//changeState();
				if (!testState()){
					try{
						changeState();
						writeResult(timeCount, Constants.PASS, "");
					}catch(Exception e){
						writeResult(timeCount, Constants.FAIL, e.getMessage());
					}
				}else{
					timeCount--;
					try{
						changeState();
						writeResult(timeCount, Constants.PASS, "");
					}catch(Exception e){
						writeResult(timeCount, Constants.FAIL, e.getMessage());
					}
				}
				break;
			}
			case MSG_AUDIO_TEST_END: {
				if (mediaPlayer != null && mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				audioMg.setMode(AudioManager.MODE_NORMAL);
				Finish();
				break;
			}

			default:
				break;
			}
		}
	}
	
	private boolean testState() {
		//String forReturn = "";
		//String showStateInTextView = "";
		// check the state of speaker
		if (audioMg.isSpeakerphoneOn()) {
			//forReturn = "Speaker ON   ";
			return true;
			//showStateInTextView = getString(R.string.text_speaker) + " ON      ";
		} else{
			return false;
		}
		// check the state of earpiece
		/*if (audioMg.getMode() == AudioManager.MODE_IN_CALL) {
			forReturn = forReturn + "earpiece  ON  ";
			return false;
		}*/
	}
	
	private void changeState() {

		if (audioMg.isSpeakerphoneOn()) {
			audioMg.setSpeakerphoneOn(false);
			audioMg.setMode(AudioManager.MODE_IN_CALL);
		} else {
			audioMg.setSpeakerphoneOn(true);
			audioMg.setMode(AudioManager.MODE_NORMAL);
		}
	}
	
	private void startTest() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		playSound(R.raw.soundtest);
	}
	
	private void playSound(int raw) {
		try {
			mediaPlayer = MediaPlayer.create(context, raw);
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.reset();
			}
			mediaPlayer.start();
		} catch (Exception e) {
			writeResult(timeCount, Constants.FAIL, "Play Sound Error: "+e.getMessage());
			e.printStackTrace();
		}
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				if (mediaPlayer != null) {
					mediaPlayer.start();
				}
			}
		});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer player, int arg1, int arg2) {
				mediaPlayer.release();
				mediaPlayer = null;
				return false;
			}
		});
	}
}
