package com.cowthegreat.shmup.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.behaviors.AlphaBehavior;
import com.cowthegreat.shmup.behaviors.Behavior;
import com.cowthegreat.shmup.behaviors.ChaseBehavior;
import com.cowthegreat.shmup.behaviors.ExplodeBehavior;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;
import com.cowthegreat.shmup.graphics.PolyTools;

public class IdiotBroController extends EnemyController {

	private class ExplodeEnder implements ParticleEffectListener {
		@Override
		public void effectFinished() {
			setDispose(true);
		}
	}

	public static final float IDIOT_BRO_SPEED = 75;
	public static final float ROTATION_RATE = 360;
	private static TextureRegion marker;

	AlphaBehavior ab;
	ChaseBehavior cb;
	ExplodeBehavior eb;
	Behavior activeBehavior;

	Polygon hitbox;
	GameSprite body;
	GameSprite shield;
	boolean dead;

	public IdiotBroController(Skin s) {
		body = new GameSprite(s.getRegion("bro"));
		body.setRotation(SHMUP.rng.nextFloat() * 360);
		body.setScale(1.5f);
		body.rotationalVelocity = ROTATION_RATE;

		shield = new GameSprite(s.getRegion("bro_shield"));
		body.addChild(shield);

		float[] points = new float[] { 5, body.getHeight() - 5, body.getWidth() - 5,
				body.getHeight() - 5, body.getWidth() - 5, 5, 5, 5 };

		hitbox = new Polygon(points);
		hitbox.setOrigin(body.getHeight() / 2, body.getWidth() / 2);
		hitbox.setScale(1.5f, 1.5f);

		marker = s.getRegion("bro_marker");
		
		ab = new AlphaBehavior();
		ab.setDuration(1);
		ab.setController(this);
		
		cb = new ChaseBehavior();
		cb.setSpeed(IDIOT_BRO_SPEED);
		cb.setDistance(0, 0);
		cb.setController(this);
		
		eb = new ExplodeBehavior();
		eb.setSpeed(IDIOT_BRO_SPEED * 2);
		eb.setController(this);
	}

	@Override
	public void initialize(Skin s) {
		dead = false;
		setAlpha(0);
		setInteractable(false);
		setDispose(false);
		
		body.clearParticles();
		
		activeBehavior = ab;
	}

	@Override
	public GameSprite getControlled() {
		return body;
	}

	@Override
	public void setTracked(GameSprite gs) {
		cb.setTarget(gs);
		super.setTracked(gs);
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
		if (!isInvulnerable() && !isDead()) {
			dead = true;
			if (isDead()) {
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
		body.addParticles(SHMUP.explosion_particles.obtain(),
				new ExplodeEnder());
		eb.reset();
		eb.setDirection(getTracked());
		eb.setSpeed(IDIOT_BRO_SPEED * 2);
		activeBehavior = eb;
		SHMUP.explosion.play();
	}

	@Override
	public void update(float delta) {
		activeBehavior.updtae(delta);
		body.update(delta);
		hitbox.setPosition(body.getX(), body.getY());
		
		if(activeBehavior.complete()){
			if(activeBehavior == ab){
				setInteractable(true);
				activeBehavior = cb;
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch) {
		hitbox.setPosition(body.getX(), body.getY());
		shield.setVisible(isInvulnerable());
		body.draw(batch, getAlpha());
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
