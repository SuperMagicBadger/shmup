package com.cowthegreat.shmup;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.cowthegreat.shmup.controllers.EnemyController;
import com.cowthegreat.shmup.controllers.PlayerController;
import com.cowthegreat.shmup.controllers.UnitController;
import com.cowthegreat.shmup.graphics.GameSprite;

public interface GameMap {
	public void draw(SpriteBatch batch);
	public void draw(ShapeRenderer renderer);
	public void draw(ImmediateModeRenderer renderer, Matrix4 transform);
	public void update(float delta);
	public void spawnNextLevel();
	public void reset(GameSprite playerSprite);
	public void recordScore(Scoreboard board);
	public float getLevelSpeed();
	public boolean testCollisions(PlayerController playerContrller);
	public Rectangle getBounds();
	public ArrayList<EnemyController> getActiveUnits();
	public void leash(UnitController uc);
	public int getLevel(); 
}
