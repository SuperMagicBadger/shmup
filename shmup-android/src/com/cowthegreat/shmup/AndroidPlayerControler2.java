package com.cowthegreat.shmup;

import java.text.Format;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.cowthegreat.shmup.controllers.PlayerController;

public class AndroidPlayerControler2 extends PlayerController {

	Vector3 zeroTilt = null;
	public float deadZone = 0.15f;
	private float runningX, runningY;
	Vector2 v = new Vector2();

	@Override
	public void update(float delta) {
		if (zeroTilt == null) {
			reset();
		}

		float ax = -getSmoothedY();
		float ay = getSmoothedX();
		float az = Gdx.input.getAccelerometerZ() - zeroTilt.z + 10;
		float angleH = (float) Math.toDegrees(Math.asin(ay
				/ (Math.sqrt(ay * ay + az * az))));
		float angleV = -1
				* (float) Math.toDegrees(Math.asin(ax
						/ (Math.sqrt(ax * ax + az * az))));

		if (!isDead()) {

			if (Gdx.input.justTouched()) {
				setDash(Gdx.input.getX(), Gdx.input.getY());
			}
		}
		v.set(angleH / 90f * game.settings.sensitivityX,
				angleV / 90f * game.settings.sensitivityY).limit(1);

		if (zeroTilt.z < 0) {
			v.scl(-1);
		}

		if (v.len() < deadZone) {
			v.set(0, 0);
		}
		updateMovement(delta, v.x, v.y);
	}

	@Override
	public void reset() {
		zeroTilt = new Vector3(Gdx.input.getAccelerometerY(),
				Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerZ());
	}

	private float getSmoothedX() {
		if (zeroTilt == null)
			return 0;
		float raw = Gdx.input.getAccelerometerY() - zeroTilt.x;
		runningX = runningX * 0.75f + (1 - 0.75f) * raw;
		return runningX;
	}

	private float getSmoothedY() {
		if (zeroTilt == null)
			return 0;
		float raw = -(Gdx.input.getAccelerometerX() - zeroTilt.y);
		runningY = runningY * 0.75f + (1 - 0.75f) * raw;
		if (runningY == 0) {
		}
		return runningY;
	}

	@Override
	public void setMesage(Label l, Format f) {
		float ax = Gdx.input.getAccelerometerX() - zeroTilt.y;
		float ay = Gdx.input.getAccelerometerY() - zeroTilt.x;
		float az = Gdx.input.getAccelerometerZ() - zeroTilt.z + 10;
		float angleH = Math.signum(zeroTilt.z)
				* (float) Math.toDegrees(Math.asin(ay
						/ (Math.sqrt(ay * ay + az * az))));
		float angleV = -1
				* Math.signum(zeroTilt.z)
				* (float) Math.toDegrees(Math.asin(ax
						/ (Math.sqrt(ax * ax + az * az))));

		l.setText("x: " + f.format(ax) + " y: " + f.format(ay) + " z: "
				+ f.format(az) + " yz: " + f.format(angleH) + " xz: "
				+ f.format(angleV));

	}
}
