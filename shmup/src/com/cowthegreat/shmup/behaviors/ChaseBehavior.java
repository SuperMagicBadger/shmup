package com.cowthegreat.shmup.behaviors;

import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.controllers.EnemyController;
import com.cowthegreat.shmup.graphics.GameSprite;

public class ChaseBehavior implements Behavior {

	private EnemyController cont;
	private GameSprite source, target;

	private float speed = 100;
	private float minDistance = 0;
	private float maxDistance = 0;

	private float currentDist2 = 0;

	public void setTarget(GameSprite gs) {
		target = gs;
	}

	@Override
	public void setController(EnemyController ec) {
		cont = ec;
	}

	@Override
	public void updtae(float delta) {
		source = cont.getControlled();
		if (source != null && target != null) {
			Vector2 direction = SHMUP.vector_pool.obtain();
			direction.set(target.getOriginPosX(), target.getOriginPosY());
			direction.sub(source.getOriginPosX(), source.getOriginPosY());
			
			currentDist2 = direction.len2();
			if (maxDistance != 0 || minDistance != 0) {
				if (currentDist2 < (maxDistance * maxDistance)) {
					if (currentDist2 > minDistance * minDistance) {
						direction.scl(0);
					} else {
						direction.scl(-1);
					}
				}
			}
			
			direction.nor();
			direction.scl(speed * delta);
			source.move(direction);
		}
	}

	public void setSpeed(float s) {
		speed = s;
	}

	public void setDistance(float min, float max) {
		minDistance = min;
		maxDistance = max;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean complete() {
		return currentDist2 <= (maxDistance * maxDistance)
				&& currentDist2 >= (minDistance * minDistance);
	}

}
