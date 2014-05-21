package com.cowthegreat.shmup.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.behaviors.AlphaBehavior;
import com.cowthegreat.shmup.behaviors.Behavior;
import com.cowthegreat.shmup.behaviors.ChaseBehavior;
import com.cowthegreat.shmup.behaviors.ExplodeBehavior;
import com.cowthegreat.shmup.behaviors.MoveBehavior;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;
import com.cowthegreat.shmup.graphics.PolyTools;

public class DashBroController extends EnemyController {
	public enum State {
		TRACK, DASH, CHARGE, RECOVER, WAIT
	}

	private class DashEnder implements ParticleEffectListener {
		PooledEffect efct;

		DashEnder(PooledEffect efct) {
			this.efct = efct;
		}

		@Override
		public void effectFinished() {
			efct.free();
		}
	}

	private class ExplosionEnder implements ParticleEffectListener {
		PooledEffect efct;

		public ExplosionEnder(PooledEffect efct) {
			this.efct = efct;
		}

		@Override
		public void effectFinished() {
			setDispose(true);
			efct.free();
		}
	}

	public static final float TRACK_SPEED = 120;
	public static final float DASH_CHARGE_TIMER = 0.25f;
	public static final float DASH_DISTANCE = 150;
	public static final float DASH_SPEED = 800;
	public static final float DASH_DURATION = DASH_DISTANCE / DASH_SPEED;
	public static final float RECOVER_SPEED = 75;
	public static final float RECOVER_DISTANCE = 75;
	public static final float RECOVER_DURATION = RECOVER_DISTANCE
			/ RECOVER_SPEED;
	public static final float DASH_THRESHOLD = DASH_DISTANCE * 0.65f;

	private GameSprite unit;
	private GameSprite center;
	private GameSprite shield;
	private Polygon hitbox;
	private State currentState;
	boolean dead;

	TextureRegion marker, dashMarker;

	AlphaBehavior ab;
	ChaseBehavior cb;
	MoveBehavior mb;
	ExplodeBehavior eb;
	Behavior activeBehavior;

	public DashBroController(Skin s) {
		currentState = State.WAIT;

		dead = false;

		unit = new GameSprite(s.getRegion("dash_bro"));
		float[] points = new float[] { 0, unit.getHeight(), unit.getWidth(),
				unit.getHeight(), unit.getWidth(), 0, 0, 0 };
		hitbox = new Polygon(points);

		center = new GameSprite(new Animation(0.1f, s.getAtlas().findRegions(
				"dash_bro_center"), Animation.LOOP_PINGPONG));
		shield = new GameSprite(s.getRegion("dash_bro_shield"));

		unit.addChild(center);
		unit.addChild(shield);

		hitbox.setPosition(unit.getX() + unit.getOriginX(),
				unit.getY() + unit.getOriginY());

		marker = s.getRegion("dash_bro_marker");
		dashMarker = s.getRegion("marker");

		ab = new AlphaBehavior();
		ab.setController(this);
		ab.setDuration(1);

		cb = new ChaseBehavior();
		cb.setController(this);
		cb.setSpeed(TRACK_SPEED);

		mb = new MoveBehavior();
		mb.setController(this);
		mb.setDuration(DASH_DURATION);
		mb.setSpeed(DASH_SPEED);

		eb = new ExplodeBehavior();
		eb.setController(this);
		eb.setSpeed(TRACK_SPEED * 2);
	}

	@Override
	public void initialize(Skin s) {
		setInteractable(false);
		setDispose(false);
		setAlpha(0);

		setState(State.WAIT);
		dead = false;

		ab.reset();
		ab.setDuration(1);
		activeBehavior = ab;

		unit.clearParticles();
	}

	@Override
	public GameSprite getControlled() {
		return unit;
	}

	@Override
	public void damage(int dam) {
		if (!isInvulnerable()) {
			dead = true;
			onDeath();
		}
	}

	public void setState(State state) {
		switch (state) {
		case CHARGE:
			PooledEffect dash = SHMUP.dash_particles.obtain();
			unit.addParticles(dash, new DashEnder(dash));
			mb.reset();
			mb.setDirection(0.5f, 0.5f);
			mb.setSpeed(0);
			mb.setDuration(DASH_CHARGE_TIMER);
			activeBehavior = mb;
			break;
		case DASH:
			mb.reset();
			mb.setDirection(getTracked());
			mb.setSpeed(DASH_SPEED);
			mb.setDuration(DASH_DURATION);
			activeBehavior = mb;
			break;
		case RECOVER:
			mb.reset();
			mb.setDirection(SHMUP.rng.nextFloat(), SHMUP.rng.nextFloat());
			mb.setSpeed(RECOVER_SPEED);
			mb.setDuration(RECOVER_DURATION);
			activeBehavior = mb;
			break;
		case TRACK:
			cb.reset();
			cb.setTarget(getTracked());
			cb.setSpeed(TRACK_SPEED);
			cb.setDistance(0, DASH_THRESHOLD);
			activeBehavior = cb;
			break;
		case WAIT:
			break;
		default:
			break;

		}
		currentState = state;
	}

	public void update(float delta) {
		activeBehavior.updtae(delta);
		unit.update(delta);
		hitbox.setPosition(unit.getX(), unit.getY());

		if (activeBehavior.complete()) {
			switch (currentState) {
			case CHARGE:
				setState(State.DASH);
				break;
			case DASH:
				setState(State.RECOVER);
				break;
			case RECOVER:
				setState(State.TRACK);
				break;
			case TRACK:
				setState(State.CHARGE);
				break;
			case WAIT:
				setInteractable(true);
				if (getTracked() != null) {
					setState(State.TRACK);
				}
				break;
			default:
				setState(State.WAIT);
				break;
			}
		}
	}

	@Override
	public void onDeath() {
		setState(State.WAIT);
		unit.clearParticles();
		SHMUP.explosion.play();
		PooledEffect explode = SHMUP.explosion_particles.obtain();
		unit.addParticles(explode, new ExplosionEnder(explode));

		if (currentState != State.DASH) {
			eb.setDirection(getTracked());
			eb.setSpeed(TRACK_SPEED * 2);
			activeBehavior = eb;
		}
		setState(State.WAIT);
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	@Override
	public void setInvulnerable(boolean set) {
		if (currentState != State.DASH) {
			super.setInvulnerable(set);
		}
		if (isInvulnerable()) {
			hbCol = Color.BLUE;
		} else {
			hbCol = Color.GREEN;
		}
	}

	Color hbCol = Color.GREEN;

	@Override
	public void drawHitbox(ShapeRenderer shapes) {
		Color c = shapes.getColor();
		shapes.setColor(Color.RED);
		Rectangle r = hitbox.getBoundingRectangle();
		shapes.rect(r.x, r.y, r.width, r.height);
		shapes.setColor(hbCol);
		PolyTools.drawPolygon(shapes, hitbox);
		shapes.setColor(Color.RED);
		shapes.circle(unit.getOriginPosX(), unit.getOriginPosY(),
				DASH_THRESHOLD);
		shapes.setColor(c);
	}

	@Override
	public Polygon getHitBox() {
		return hitbox;
	}

	@Override
	public void draw(SpriteBatch batch) {
		shield.setVisible(isInvulnerable());
		unit.draw(batch, getAlpha());
	}

	public final int pointValue() {
		return 1;
	}

	@Override
	public void applyShield() {
		setInvulnerable(true);
	}

	@Override
	public TextureRegion radarMarker() {
		switch (currentState) {
		case DASH:
		case CHARGE:
			return dashMarker;
		default:
			return marker;
		}
	}

	@Override
	public boolean isSeperable() {
		return true;
	}
}
