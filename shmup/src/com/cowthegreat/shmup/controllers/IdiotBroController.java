package com.cowthegreat.shmup.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.graphics.GameSprite;

public class IdiotBroController extends EnemyController {

	@Override
	public GameSprite getControlled() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawHitbox(ShapeRenderer shapes) {
		// TODO Auto-generated method stub

	}

	@Override
	public Polygon getHitBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void damage(int dam) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDeath() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void applyShield() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int pointValue() {
		return 1;
	}

	@Override
	public void initialize(Skin s) {
		// TODO Auto-generated method stub

	}

	@Override
	public TextureRegion radarMarker() {
		// TODO Auto-generated method stub
		return null;
	}

}
