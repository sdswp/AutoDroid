package com.gk.touchstone.taskview;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

import com.gk.touchstone.R;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Result;
import com.gk.touchstone.utils.DateTimes;
import com.gk.touchstone.utils.Utils;

public class CameraVideoPreviewGet extends Activity{
	/** 图片媒体类型 **/  
    public static final int MEDIA_TYPE_IMAGE = 1;  
    /** 摄像头类的对象 **/  
    private Camera mCamera;
    /** SurfaceView对象 **/  
    //private CameraView mPreview;   
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private boolean mIsRecording = false;
	private MediaRecorder mediaRecorder;
	private Utils us;

    private Handler testHandler;
    private int internalTime;
    private Intent intent;
    protected List<Result> ResultList;
    private TaskManager tm;
	private String className="";
	private int newTimeCount=0;
	// 存储任务计数
		/**
		 * 每次任务结束时发广播更新
		 */
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        ResultList = new ArrayList<Result>();
        us = new Utils(this);
        tm=new TaskManager(this);
        testHandler = new myHandler();
        
        Window window = getWindow();// 得到窗口  
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 没有标题  
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏  
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕亮  
  
        setContentView(R.layout.getbackcameravideopreview);  
        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceview);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				releaseCamera();
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				initpreview();
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				
			}
		});
       
        intent = this.getIntent();
        className=intent.getStringExtra("className");
        internalTime = intent.getIntExtra("internalTime", 1);
        
        Thread testThread = new testThread();
        testThread.start();
    }  
    
    protected void releaseCamera() {
		if(mCamera!=null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
    
    protected void initpreview() {
		mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tm.getCameraTask(className).writeResult(tm.getCameraTask(className).timeCount, 
					Constants.FAIL, e.getMessage());
			e.printStackTrace();
		}
		setCameraDisplayOrientation(this,CameraInfo.CAMERA_FACING_BACK,mCamera);
		mCamera.startPreview();
	}
    
    public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
    
    private void stopmediaRecorder() {
		if(mediaRecorder!=null){
			if(mIsRecording){
				mediaRecorder.stop();
				//mCamera.lock();
				mediaRecorder.reset();
				mediaRecorder.release();
				mediaRecorder=null;
				mIsRecording = false;
				try {
					mCamera.reconnect();
				} catch (IOException e) {
					//Toast.makeText(this, "reconect fail", 0).show();
					tm.getCameraTask(className).writeResult(tm.getCameraTask(className).timeCount, 
							Constants.FAIL, e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private void startmediaRecorder() {
		//mCamera.stopPreview();
		mCamera.unlock();
		mIsRecording = true;
		mediaRecorder = new MediaRecorder();
		mediaRecorder.reset();
		mediaRecorder.setCamera(mCamera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//		mediaRecorder.setOutputFile(getName());
//		mediaRecorder.setVideoFrameRate(5);
//		mediaRecorder.setVideoSize(640, 480);
		CamcorderProfile mCamcorderProfile = CamcorderProfile.get(CameraInfo.CAMERA_FACING_BACK, 
				CamcorderProfile.QUALITY_HIGH);
		mediaRecorder.setProfile(mCamcorderProfile);
		mediaRecorder.setOutputFile(getName());
		mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		
		try {
			mediaRecorder.prepare();
			
//			mediaRecorder.set
		} catch (Exception e) {
			
			mIsRecording = false;
			tm.getCameraTask(className).writeResult(tm.getCameraTask(className).timeCount, 
					Constants.FAIL, e.getMessage());
			//Toast.makeText(this, "fail", 0).show();
			e.printStackTrace();
			mCamera.lock();
		}
		mediaRecorder.start();
	
	}
	
	private String getName() {
		//String fileName = Environment.getExternalStorageDirectory()+"camertest"+System.currentTimeMillis()+".3gp";
		//dir = Environment.getExternalStorageDirectory();
        File mediaStorageDir = new File(us.getAppSDCardPath() + File.separator
				+ Constants.FOLDER_CAMERA);
        // 文件夹不存在，则创建该文件夹  
        if (!mediaStorageDir.exists()) {  
        	mediaStorageDir.mkdirs();
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraVideoUse", "failed to create directory");  
                return null;  
            }  
        }  
        
        /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")  
                .format(new Date());  
        // 创建照片文件
        
		String fileName = mediaStorageDir.getPath()+File.separator+timeStamp+".3gp";*/
        String gp3 = DateTimes.getCurrentTime(Constants.DATE_FILE_YMDHMS)
				+ ".3gp";

		String fileName = mediaStorageDir.getPath() + File.separator + gp3;
		Log.e("David","fileName"+fileName);
		return fileName;
	}
 
    class testThread extends Thread {
    	@Override
    	public void run() {
    		Looper.prepare();
    		super.run();
    		try {
    			//相机启动时间
				sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				tm.getCameraTask(className).writeResult(tm.getCameraTask(className).timeCount, 
						Constants.FAIL, e1.getMessage());
				e1.printStackTrace();
			}
    		newTimeCount=tm.getCameraTask(className).timeCount;
    		while (newTimeCount > 0){
    			try {
    				sleep(2000);
					startmediaRecorder();
					sleep(1000*internalTime);
					stopmediaRecorder();
					newTimeCount--;
					tm.getCameraTask(className).timeCount=newTimeCount;
					testHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					tm.getCameraTask(className).writeResult(newTimeCount, Constants.FAIL, e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		testHandler.sendEmptyMessage(1);
    	}
    }
    
    private class myHandler extends Handler{
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case 0:
    			tm.getCameraTask(className).writeResult(newTimeCount,Constants.PASS,"");
    			break;
    		case 1:
    			tm.getCameraTask(className).Finish();
    			finish();
    			//simulateKey(KeyEvent.KEYCODE_BACK);
    			break;
    		default:
    			break;
    		}
    	}
    }
    
	/*private TaskBase getCameraTask() {
		// int taskcount=0;
		TaskBase taskbase = null;
		List<TaskBase> taskbases = app.getTaskBases();
		if (taskbases != null && taskbases.size() > 0) {
			for (TaskBase t : taskbases) {
				if (t.taskName.equals(className)) {
					taskbase = t;
					break;
				}
				
			}
		}
		
		return taskbase;
	}*/
    
    public static void simulateKey(final int KeyCode) {
    	   new Thread() {
    	         public void run() {
    	             try {
    	                  Instrumentation inst = new Instrumentation(); 
    	                  inst.sendKeyDownUpSync(KeyCode);    
    	             } catch (Exception e) {
    	                 Log.e("Exception when sendKeyDownUpSync", e.toString());    
    	            }   
    	        }  
    	  }.start(); 
    }
    
    /*@SuppressLint("SimpleDateFormat")
	private String currentTime() {
		String startdate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		startdate = formatter.format(curDate);
		return startdate;
	}
    
    public void logd(String tips) {
		Log.d(this.getClass().getSimpleName(), tips);
	}
    
    private void writeResult(int timeCount, String result, String reason) {
		TestResult tr = new TestResult();
		tr.setResultData(timeCount, result, reason, currentTime());
		ResultList.add(tr);
		
		//String prefsName = task.getTaskName();
		//int taskcountPrefs = tm.getTaskSharePrefs(className);
		
		logd("开始记录" + className + ":[" + getCameraTask().taskCount + "-"
				+ timeCount + "]" + result + reason);

		if (app.getTaskBases().size() == 1 && getCameraTask().taskCount == 0 && timeCount == 0) {
			tm.sendTaskBroadcast(0);
		} else {
			tm.sendTaskBroadcast(1);
		}
	}*/
}


