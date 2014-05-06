package com.cowthegreat.shmup;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        
        RelativeLayout layout = new RelativeLayout(this);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;

        View libgdxView = initializeForView(new SHMUP(new AndroidPlayerControler()), cfg);
        
        layout.addView(libgdxView);
        
        setContentView(layout);
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