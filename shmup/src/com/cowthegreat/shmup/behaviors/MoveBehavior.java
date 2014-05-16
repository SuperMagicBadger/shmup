package com.cowthegreat.shmup.behaviors;

import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.controllers.EnemyController;
import com.cowthegreat.shmup.graphics.GameSprite;

public class MoveBehavior implements Behavior{
	
	EnemyController ec;
	Vector2 direction;
	float speed;
	
	float duration;	
	float timeElapsed;
	
	@Override
	public void setController(EnemyController ec) {
		this.ec = ec;
	}
	
	@Override
	public void updtae(float delta) {
		if(!complete()){
			Vector2 d = SHMUP.vector_pool.obtain();
			d.set(direction).scl(delta * speed);
			timeElapsed += delta;
			SHMUP.vector_pool.free(d);
		}
	}
	
	public void setDuration(float time){
		duration = time;
	}
	
	public void setDirection(float x, float y){
		direction.set(x, y);
	}
	
	public void setDirection(GameSprite gs) {
		
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	@Override
	public void reset() {	
	}
	
	@Override
	public boolean complete() {
		return timeElapsed >= duration;
	}
	
}
