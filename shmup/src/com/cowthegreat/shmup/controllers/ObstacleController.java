package com.cowthegreat.shmup.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.PolyTools;

public class ObstacleController implements UnitController{
	
	GameSprite controlled;

	MinimumTranslationVector v = new MinimumTranslationVector();
	Polygon hitbox;
	
	
	public ObstacleController(GameSprite tr){
		controlled = tr;
		
		float x = 0;
		float y = 0;
		
		float[] points = new float[4*2];
		points[6] = x;
		points[7] = y;
		points[4] = x + tr.getRegionWidth();
		points[5] = y;
		points[2] = x + tr.getRegionWidth();
		points[3] = y + tr.getRegionHeight();
		points[0] = x;
		points[1] = y + tr.getRegionHeight();
		
		hitbox = new Polygon(points);
		hitbox.setPosition(tr.getX(), tr.getY());
		hitbox.setOrigin(tr.getWidth() / 2, tr.getHeight() / 2);
		hitbox.setRotation(tr.getRotation());
	}

	public void collide(UnitController uc){
		Polygon ucHB = uc.getHitBox();
		
		Vector2 v = SHMUP.vector_pool.obtain();
		if (PolyTools.intersect(hitbox, ucHB, v)){
			System.out.println("butts");
			System.out.println(v);
			uc.getControlled().move(v.x, v.y);
		}
		SHMUP.vector_pool.free(v);
	}
	
	@Override
	public GameSprite getControlled() {
		return controlled;
	}

	@Override
	public boolean isInvulnerable() {
		return true;
	}

	@Override
	public void setInvulnerable(boolean set) {}

	@Override
	public void drawHitbox(ShapeRenderer shapes) {
		Color c = shapes.getColor();
		shapes.setColor(Color.RED);
		shapes.rect(
				hitbox.getBoundingRectangle().x, hitbox.getBoundingRectangle().getY(),
				hitbox.getBoundingRectangle().width, hitbox.getBoundingRectangle().height);
		shapes.polygon(hitbox.getTransformedVertices());
		shapes.setColor(Color.BLUE);
		
		float[] p = hitbox.getTransformedVertices();
		
		for(int i = 0; i < p.length; i+=2){
			float x1 = p[i];
			float y1 = p[i + 1];
			float x2 = p[(i + 2) % p.length];
			float y2 = p[(i + 3) % p.length];
			
			Vector2 nor = SHMUP.vector_pool.obtain();
			nor.set(y1 - y2, -(x1 - x2)).nor().scl(10);
			
			Vector2 midpt = SHMUP.vector_pool.obtain();
			midpt.set((x1 + x2) / 2, (y1 + y2) / 2);
			nor.add(midpt);
			
			shapes.line(midpt, nor);
			
			SHMUP.vector_pool.free(nor);
			SHMUP.vector_pool.free(midpt);
		}
		
		shapes.setColor(c);
	}

	@Override
	public Polygon getHitBox() {
		return hitbox;
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
		controlled.update(delta);
//		controlled.rotate(100 * delta);
		hitbox.setPosition(controlled.getX(), controlled.getY());
		hitbox.setRotation(controlled.getRotation());
	}

	@Override
	public void draw(SpriteBatch batch) {
		controlled.draw(batch);
	}

}
