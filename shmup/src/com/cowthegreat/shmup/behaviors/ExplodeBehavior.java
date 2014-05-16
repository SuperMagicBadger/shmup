package com.cowthegreat.shmup.behaviors;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.controllers.EnemyController;
import com.cowthegreat.shmup.graphics.GameSprite;

public class ExplodeBehavior implements Behavior {

	EnemyController ec;
	Vector2 direction;
	float speed;

	@Override
	public void setController(EnemyController ec) {
		this.ec = ec;
	}

	public void setSpeed(float s) {
		speed = s;
	}

	public void setDirection(GameSprite gs) {
		if (direction == null) {
			direction = SHMUP.vector_pool.obtain();
		}

		Vector2 gsFacing = SHMUP.vector_pool.obtain();

		// find the facing
		gsFacing.set(1, 0);
		gsFacing.rotate(gs.getRotation());
		gsFacing.add(gs.getOriginPosX(), gs.getOriginPosY());

		// find the side
		int side = Intersector.pointLineSide(gs.getOriginPosX(), gs
				.getOriginPosY(), gsFacing.x, gsFacing.y, ec.getControlled()
				.getOriginPosX(), ec.getControlled().getOriginPosY());

		// make it perpendicular
		if (side < 0) {
			direction.set(gsFacing.y, -gsFacing.x);
		} else {
			direction.set(-gsFacing.y, gsFacing.x);
		}
		direction.nor();

		SHMUP.vector_pool.free(gsFacing);
	}

	@Override
	public void updtae(float delta) {
		ec.getControlled().move(direction.x * delta * speed,
				direction.y * delta * speed);
	}

	@Override
	public void reset() {
		if (direction != null) {
			SHMUP.vector_pool.free(direction);
			direction = null;
		}
	}

	@Override
	public boolean complete() {
		return false;
	}

}
