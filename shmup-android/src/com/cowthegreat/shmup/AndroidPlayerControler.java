package com.cowthegreat.shmup;

import java.text.Format;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.cowthegreat.shmup.controllers.PlayerController;

public class AndroidPlayerControler extends PlayerController {

	Vector3 zeroTilt = null;
	public float deadZone = 0.15f;
	private float runningX, runningY, runningZ;
	Vector3 v3 = new Vector3();
	Vector3 vax = new Vector3();
	Vector3 vaz = new Vector3();
	Vector2 v = new Vector2();

	@Override
	public void update(float delta) {
		if (zeroTilt == null) {
			reset();
		}

		if (!isDead()) {
			if (Gdx.input.justTouched()) {
				setDash(Gdx.input.getX(), Gdx.input.getY());
			} else {
				v3.set(getSmoothedY(), getSmoothedX(), getSmoothedZ());		
				v.x = v3.dot(vaz);
				v.y = v3.dot(vax);
				if(zeroTilt.z > 0){
					v.y *= -1;
				}

				if (v.len() < deadZone) {
					v.set(0, 0);
				}
				
				v.x *= game.settings.sensitivityX;
				v.y *= game.settings.sensitivityY;
				v.limit(1);
			}
		}
		
		updateMovement(delta, v.x, v.y);
	}

	@Override
	public void reset() {
		if(zeroTilt == null){
			zeroTilt = new Vector3();
		}
		zeroTilt.set(Gdx.input.getAccelerometerX(),
				Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());
		zeroTilt.nor();
		
		vax = new Vector3(1, 0, 0).nor();
		vaz = new Vector3(zeroTilt);
		
		vaz.crs(vax).nor();
		vax.set(vaz).crs(zeroTilt).nor();
	}
	
	@Override
	public void setAngle(float x, float y, float z) {
		if(zeroTilt == null){
			zeroTilt = new Vector3();
		}
		zeroTilt.set(x, y, z);
	};

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
		float raw = (Gdx.input.getAccelerometerX() - zeroTilt.y);
		runningY = runningY * 0.75f + (1 - 0.75f) * raw;
		return runningY;
	}

	private float getSmoothedZ() {
		if (zeroTilt == null)
			return 0;
		float raw = (Gdx.input.getAccelerometerZ() - zeroTilt.z);
		runningZ = runningZ * 0.75f + (1 - 0.75f) * raw;
		return runningZ;

	}

	@Override
	public void setMesage(Label l, Format f) {
		
		Vector2 vel = new Vector2();
		
		float ax = Gdx.input.getAccelerometerX();
		float ay = Gdx.input.getAccelerometerY();
		float az = Gdx.input.getAccelerometerZ();
		Vector3 v3 = new Vector3(ax, ay, az).nor();
		
		vel.x = v3.dot(vax);
		vel.y = v3.dot(vaz);
		
		float lengthH = (float) (Math.sqrt(ay * ay + az * az));
		float angleH = (float) Math.toDegrees(Math.acos(ay / lengthH));
		float lengthV = (float) (Math.sqrt(ax * ax + az * az));
		float angleV = (float) Math.toDegrees(Math.acos(ax / lengthV));

		l.setText(
				"zero: " + zeroTilt +
				"\nvax: " + vax +
				"\nvaz: " + vaz +
				"\nvel: " + f.format(vel.x) + ":" + f.format(vel.y) +
				"\nx: " + f.format(ax) +
				"\ny: " + f.format(ay) +
				"\nz: " + f.format(az) + 
				"\nlyz: " + f.format(lengthH) + 
				"\nlxz: " + f.format(lengthV) + 
				"\nyz: " + f.format(angleH) + 
				"\nxz: " + f.format(angleV));

	}
}
