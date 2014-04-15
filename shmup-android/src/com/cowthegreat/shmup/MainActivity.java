package com.cowthegreat.shmup;

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        
        initialize(new SHMUP(new AndroidPlayerControler()), cfg);
    }
    
//    @Override
//    protected void onStart() {
//    	super.onStart();
//    	EasyTracker.getInstance(this).activityStart(this);
//    }
//    
//    @Override
//    protected void onStop() {
//    	super.onStop();
//    	EasyTracker.getInstance(this).activityStart(this);
//    }
}