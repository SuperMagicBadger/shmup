package com.cowthegreat.shmup.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ParallaxCamera extends OrthographicCamera{
	public ParallaxBackground bg;
	
	public ParallaxCamera(float w, float h){
		super(w, h);
	}
	
	public void setPosition(float x, float y){
		float deltaX = x - position.x;
		float deltaY = y - position.y;
		bg.slide(-deltaX, -deltaY);
		position.set(x, y, 0);
	}
	
	public void setPosition(Vector2 pos){
		setPosition(pos.x, pos.y);
	}
	
	public void slide(float deltaX, float deltaY, Rectangle bounds){
		float newX = position.x + deltaX;
		float newY = position.y + deltaY;
		
		float cLeft = newX - viewportWidth / 2;
		float cRight = newX + viewportWidth / 2;
		float cTop = newY +  viewportHeight / 2;
		float cBottom = newY - viewportHeight / 2;
		
		if(cLeft < bounds.x){
			newX += bounds.x - cLeft;
		} else if (cRight > bounds.x + bounds.width){
			newX += bounds.x + bounds.width - cRight;
		}
		
		if(cTop > bounds.y + bounds.height){
			newY += bounds.y + bounds.height - cTop;
		} else if (cBottom < bounds.y){
			newY += bounds.y - cBottom;
		}
		setPosition(newX, newY);
	}
	
	public void slide(float deltaX, float deltaY){
		float newX = position.x + deltaX;
		float newY = position.y + deltaY;
		setPosition(newX, newY);
	}
	
	public void slide(Vector2 delta){
		slide(delta.x, delta.y);
	}
}
