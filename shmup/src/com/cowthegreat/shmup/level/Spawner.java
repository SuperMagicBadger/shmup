package com.cowthegreat.shmup.level;

import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;

public abstract class Spawner {
	public Vector2 center = SHMUP.vector_pool.obtain();
	
	public float spawnRadius;
	
	public float spawnTimer = 0f;
	
	public float spawnIndex = 0f;
	public float spawnIncrement = 0.15f;
	
	public void update(float delta){
		spawnTimer -= delta;
		if(spawnTimer <= 0){
			System.out.println("spawning time");
			spawnTimer = spawnRate();
			int c = count();
			for(int i = 0; i < c; i++){
				spawn(spawnX(spawnIndex), spawnY(spawnIndex));
				spawnIndex += spawnIncrement;
			}
		}
	}
	
	public float spawnX(float i) {
		return (float) Math.cos(2 * Math.PI * i) * spawnRadius + center.x;
	}

	public float spawnY(float i) {
		return (float) Math.sin(2 * Math.PI * i) * spawnRadius + center.y;
	}
	
	public abstract int count();
	public abstract void spawn(float x, float y);
	public abstract float spawnRate();
}
