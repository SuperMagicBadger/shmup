package com.cowthegreat.shmup.controllers;

import com.cowthegreat.shmup.controllers.EnemyController.EnemyBehavior;

public class ChaseBehavior implements EnemyBehavior {

	EnemyController ec;
	
	public float speed;
	
	@Override
	public void setEnemy(EnemyController ec) {
		this.ec = ec;
	}

	@Override
	public void update(float delta) {
		
	}

}
