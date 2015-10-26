package com.gk.touchstone.utils;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView  extends SurfaceView implements SurfaceHolder.Callback {  
	
	private SurfaceHolder mHolder;  
    private Camera mCamera;  
  
    public CameraView(Context context, Camera camera) {  
        super(context);  
        mCamera = camera;  
        mHolder = getHolder();  
        mHolder.addCallback(this);  
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
    }  
    
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
        if (holder.getSurface() == null)  
            return;  
        try {  
            mCamera.setPreviewDisplay(holder);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        mCamera.startPreview(); 
    }  
  
    @Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {  
        if (mHolder.getSurface() == null)  
            return;  
        mCamera.stopPreview();  
        try {  
            mCamera.setPreviewDisplay(mHolder);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        mCamera.startPreview(); 
    }  
  
    @Override  
    public void surfaceDestroyed(SurfaceHolder holder) {  
    	
    }  
}  
