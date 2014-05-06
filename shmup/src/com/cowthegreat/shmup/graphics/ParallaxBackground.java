package com.cowthegreat.shmup.graphics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ParallaxBackground {
	
	Camera cam;
	ArrayList<Layer> layers;
	
	public ParallaxBackground(Camera camera){
		layers = new ArrayList<Layer>();
		cam = camera;
	}
	
	public void addLayer(TextureRegion tr, float speed){
		Layer l = new Layer(tr, speed);
		layers.add(l);
	}
	
	public void addLayer(TextureRegion tr, float speedx, float speedy){
		Layer l = new Layer(tr, speedx, speedy);
		layers.add(l);
	}
	
	public void draw(SpriteBatch batch){
		for(Layer l : layers){
			l.draw(batch);
		}
	}
	
	public void slide(float x, float y){
		for(Layer l : layers){
			l.slide(x, y);
		}
	}
	
	private class Layer {
		TextureRegion texture;
		float offsetX, offsetY, speedX, speedY;
		public Layer(TextureRegion tr, float s){
			texture = tr;
			offsetX = offsetY = 0;
			speedX = speedY = s;
		}
		
		public Layer(TextureRegion tr, float sx, float sy){
			texture = tr;
			offsetX = offsetY = 0;
			speedX = sx;
			speedY = sy;
		}
		
		public void draw(SpriteBatch batch){
			for(int i = -1; offsetX + x() + texture.getRegionWidth() * i <= right(); i++){
				for(int j = -1; offsetY + y() + texture.getRegionHeight() * j <= top(); j++){
					batch.draw(texture, offsetX + x() + texture.getRegionWidth() * i, offsetY + y() + texture.getRegionHeight() * j);
				}
			}
		}
		
		private float x(){
			return cam.position.x - (cam.viewportWidth / 2f);
		}
		
		private float y(){
			return cam.position.y - (cam.viewportHeight / 2f);
		}
		
		private float right(){
			return cam.position.x + (cam.viewportWidth / 2f);
		}
		
		private float top(){
			return cam.position.y + (cam.viewportHeight / 2f);
		}
		
		private void slide(float x, float y){
			offsetX += x * speedX;
			offsetY += y * speedY;
			
			if(offsetX > texture.getRegionWidth()) offsetX -= texture.getRegionWidth();
			if(offsetX < -texture.getRegionWidth()) offsetX += texture.getRegionWidth();

			if(offsetY > texture.getRegionHeight()) offsetY -= texture.getRegionHeight();
			if(offsetY < -texture.getRegionHeight()) offsetY += texture.getRegionHeight();
		}
	}
}
