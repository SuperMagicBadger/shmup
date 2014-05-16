package com.cowthegreat.shmup.behaviors;

import com.cowthegreat.shmup.controllers.EnemyController;

public interface Behavior {	
	public void setController(EnemyController ec);
	public void updtae(float delta);	
	public void reset();
	public boolean complete();
}
