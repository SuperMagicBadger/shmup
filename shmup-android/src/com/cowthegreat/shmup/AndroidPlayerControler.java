package com.cowthegreat.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.controllers.PlayerController;

public class AndroidPlayerControler extends PlayerController {

	public Vector2 zeroTilt = null;
	public float deadZone = 0.15f;

	private float runningX, runningY;
	
	Vector2 v = new Vector2();
	
	@Override
	public void update(float delta) {
		if (!isDead()) {
			if (zeroTilt == null) {
				reset();
			}

			if (Gdx.input.justTouched()) {
				setDash(Gdx.input.getX(), Gdx.input.getY());
			}
		}
		v.set(getSmoothedX(), getSmoothedY()).limit(game.settings.sensitivityX).scl( 1f / game.settings.sensitivityX);
		if(v.len() < deadZone){
			v.set(0,0);
		}
		updateMovement(delta, v.x, v.y);
	}

	@Override
	public void drawHitbox(ShapeRenderer shapes) {
		Color c = shapes.getColor();
		super.drawHitbox(shapes);
		shapes.setColor(Color.PINK);
		shapes.circle(zeroTilt.x + getControlled().getOriginPosX(), zeroTilt.y + getControlled().getOriginPosY(), game.settings.sensitivityX);
		shapes.setColor(c);
	}
	
	private float getSmoothedX() {
		if (zeroTilt == null)
			return 0;
		float raw = Gdx.input.getAccelerometerY() - zeroTilt.x;
		runningX = runningX * game.settings.smoothing + (1 - game.settings.smoothing) * raw;
		return runningX;
	}

	private float getSmoothedY() {
		if (zeroTilt == null)
			return 0;
		float raw = -(Gdx.input.getAccelerometerX() - zeroTilt.y);
		runningY = runningY * game.settings.smoothing + (1 - game.settings.smoothing) * raw;
		if(runningY == 0){
		}
		return runningY;
	}
	
	@Override
	public void reset(){
		zeroTilt = null;
		zeroTilt = new Vector2(Gdx.input.getAccelerometerY(),
				Gdx.input.getAccelerometerX());
		zeroTilt.x = Math.max(Math.min(10 - game.settings.sensitivityX, zeroTilt.x), -(10 - game.settings.sensitivityX));
		zeroTilt.y = Math.max(Math.min(10 - game.settings.sensitivityX, zeroTilt.y), -(10 - game.settings.sensitivityX));
	}
}
