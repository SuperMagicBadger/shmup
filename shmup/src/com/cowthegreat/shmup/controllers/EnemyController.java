package com.cowthegreat.shmup.controllers;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.level.RoundMap;

public abstract class EnemyController implements UnitController{
	
	protected RoundMap spawner;
	protected GameSprite tracked;
	
	private boolean invulnerable = false;
	private boolean dispose = false;
	
	public void onSpawn(RoundMap parent){
		spawner = parent;
	}
	
	public void onDeSpawn(){
		spawner = null;
	}
	
	@Override
	public void setInvulnerable(boolean set){
		invulnerable = set;
	}
	
	@Override
	public boolean isInvulnerable(){
		return invulnerable;
	}
	
	public void setTracked(GameSprite gs){
		tracked = gs;
	}
	
	public GameSprite getTracked(){
		return tracked;
	}
	
	public void setDispose(boolean set){
		dispose=set;
	}
	
	public boolean isDispose(){
		return dispose;
	}

	public abstract int pointValue();
	public abstract void applyShield();
	public abstract void initialize(Skin s);
	public abstract TextureRegion radarMarker();
}
