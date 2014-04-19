package com.cowthegreat.shmup.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;
import com.cowthegreat.shmup.graphics.PolyTools;

public class IdiotBroController extends EnemyController {

	private class ExplodeEnder implements ParticleEffectListener{
		@Override
		public void effectFinished() {
			setDispose(true);
		}
	}
	
	public static final float IDIOT_BRO_SPEED = 75;
	public static final float ROTATION_RATE = 360;
	private static TextureRegion marker;
	
	Polygon hitbox;
	GameSprite body;
	boolean dead;
	
	@Override
	public void initialize(Skin s) {
		body = new GameSprite(s.getRegion("bro"));
		body.setRotation(SHMUP.rng.nextFloat() * 360);
		body.setScale(1.5f);
		
		float[] points = new float[]{
				0, body.getHeight(),
				body.getWidth(), body.getHeight(),
				body.getWidth(), 0,
				0, 0
		};
		
		hitbox = new Polygon(points);
		hitbox.setOrigin(body.getHeight() / 2, body.getWidth() / 2);
		hitbox.setScale(1.5f, 1.5f);
		dead = false;
		
		marker = s.getRegion("bro_marker");
	}
	
	@Override
	public GameSprite getControlled() {
		return body;
	}

	@Override
	public void drawHitbox(ShapeRenderer shapes) {
		PolyTools.drawPolygon(shapes, hitbox);
	}

	@Override
	public Polygon getHitBox() {
		return hitbox;
	}

	@Override
	public void damage(int dam) {
		if(!isInvulnerable() && !isDead()){
			dead = true;
			if(isDead()){
				onDeath();
			}
		}
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	@Override
	public void onDeath() {
		body.addParticles(SHMUP.explosion_particles.obtain(), new ExplodeEnder());
		SHMUP.explosion.play();
	}

	@Override
	public void update(float delta) {
		Vector2 vel = SHMUP.vector_pool.obtain();
		vel.set(getTracked().getOriginPosX(), 
				getTracked().getOriginPosY());
		vel.sub(body.getOriginPosX(), 
				body.getOriginPosY());
		vel.nor();

		if(!isDead()){
			vel.scl(IDIOT_BRO_SPEED * delta);
		} else {
			vel.scl(IDIOT_BRO_SPEED * -2 *  delta);
		}
		body.move(vel);
		body.rotate(ROTATION_RATE * delta);
		SHMUP.vector_pool.free(vel);
		
		body.update(delta);
		hitbox.setPosition(body.getX(), body.getY());
	}

	@Override
	public void draw(SpriteBatch batch) {
		hitbox.setPosition(body.getX(), body.getY());
		body.draw(batch);
	}
	
	@Override
	public void applyShield() {
		setInvulnerable(true);
	}

	@Override
	public int pointValue() {
		return 1;
	}

	@Override
	public TextureRegion radarMarker() {
		return marker;
	}
	
	@Override
	public boolean isSeperable() {
		return false;
	}
}
