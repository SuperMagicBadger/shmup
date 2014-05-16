package com.cowthegreat.shmup.controllers;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.graphics.GameSprite;

public abstract class EnemyController implements UnitController{
	
	protected GameSprite tracked;
	
	private float alpha;
	
	private boolean interactable = false;
	private boolean invulnerable = false;
	private boolean dispose = false;

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
	
	public void setInteractable(boolean set){
		interactable = set;
	}
	
	public boolean isInteractable(){
		return interactable;
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
	
	public void setAlpha(float set){
		alpha = set;
	}
	
	public float getAlpha(){
		return alpha;
	}
	
	public abstract int pointValue();
	public abstract void applyShield();
	public abstract void initialize(Skin s);
	public abstract TextureRegion radarMarker();
}
