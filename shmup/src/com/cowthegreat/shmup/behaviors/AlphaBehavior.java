package com.cowthegreat.shmup.behaviors;

import com.cowthegreat.shmup.controllers.EnemyController;

public class AlphaBehavior implements Behavior {
	
	EnemyController ec;
	
	float timeElapsed;
	float duration;
	
	public AlphaBehavior(){
		timeElapsed = 0;
		duration = 1;}
	
	public float currentAlpha(){
		return Math.min(timeElapsed / duration, 1);
	}
	
	public void setDuration(float set){
		duration = set;
	}
	
	@Override
	public boolean complete() {
		return timeElapsed >= duration;
	}

	@Override
	public void reset() {
		timeElapsed = 0;
		ec.setAlpha(0);
	}

	@Override
	public void setController(EnemyController ec) {
		this.ec = ec;
	}
	
	@Override
	public void updtae(float delta) {
		timeElapsed += delta;
		ec.setAlpha(currentAlpha());
	}
}
