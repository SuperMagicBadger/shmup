package com.cowthegreat.shmup.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class GSImage extends Actor {
	GameSprite gs;
	
	public GSImage(GameSprite gs){
		this.gs = gs;
		setSize(gs.getWidth(), gs.getHeight());
	}
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		gs.setPosition(getX(), getY());
		gs.setScale(getScaleX(), getScaleY());
		gs.draw(batch, parentAlpha);
	}
}
