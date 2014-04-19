package com.cowthegreat.shmup.controllers;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.cowthegreat.shmup.graphics.GameSprite;

public interface UnitController {
	GameSprite getControlled();

	public boolean isInvulnerable();

	public void setInvulnerable(boolean set);

	public void drawHitbox(ShapeRenderer shapes);

	public Polygon getHitBox();

	public void damage(int dam);

	public boolean isDead();

	public void onDeath();

	public void update(float delta);

	public void draw(SpriteBatch batch);
	
	public boolean isSeperable();
}
