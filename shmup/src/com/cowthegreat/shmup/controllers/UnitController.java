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

	void damage(int dam);

	boolean isDead();

	void onDeath();

	void update(float delta);

	void draw(SpriteBatch batch);
}
