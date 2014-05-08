package com.cowthegreat.shmup.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.PolyTools;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;

public class DashBroController extends EnemyController {
	public enum State {
		READY, TRACK, DASH, CHARGE, RECOVER, WAIT
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

	public static final float TRACK_SPEED = 125;
	public static final float DASH_CHARGE_TIMER = 0.55f;
	public static final float DASH_DISTANCE = 300;
	public static final float DASH_SPEED = 825;
	public static final float DASH_DURATION = DASH_DISTANCE / DASH_SPEED;
	public static final float RECOVER_SPEED = 50;
	public static final float RECOVER_DISTANCE = 100;
	public static final float RECOVER_DURATION = RECOVER_DISTANCE
			/ RECOVER_SPEED;
	public static final float DASH_THRESHOLD = DASH_DISTANCE * 0.65f;

	private GameSprite unit;
	private GameSprite center;
	private GameSprite shield;
	public Polygon hitbox;
	public State currentState;
	float dashTimer;
	boolean dead;

	float alpha;

	Vector2 position;
	Vector2 destination;

	Vector2 velocity;
	Vector2 targetVelocity;

	TextureRegion marker, dashMarker;

	public DashBroController() {
		currentState = State.READY;

		dead = false;

		position = new Vector2();
		destination = new Vector2();

		velocity = new Vector2();
		targetVelocity = new Vector2();

		float[] points = new float[8];
		points[6] = -15;
		points[7] = -15;
		points[4] = 15;
		points[5] = -15;
		points[2] = 15;
		points[3] = 15;
		points[0] = -15;
		points[1] = 15;

		hitbox = new Polygon(points);
	}

	@Override
	public void initialize(Skin s) {
		unit = new GameSprite(s.getRegion("dash_bro"));
		center = new GameSprite(new Animation(0.1f, s.getAtlas().findRegions(
				"dash_bro_center"), Animation.LOOP_PINGPONG));
		shield = new GameSprite(s.getRegion("dash_bro_shield"));

		unit.addChild(center);
		unit.addChild(shield);

		hitbox.setPosition(unit.getX() + unit.getOriginX(),
				unit.getY() + unit.getOriginY());

		marker = s.getRegion("dash_bro_marker");
		dashMarker = s.getRegion("marker");
	}

	public void setControlled(GameSprite object) {
		unit = object;
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
		case READY:
			position.set(0, 0);
			destination.set(0, 0);
			velocity.set(0, 0);
			targetVelocity.set(0, 0);
			unit.velocity.set(Vector2.Zero);
			break;
		case CHARGE:
			dashTimer = 0;
			unit.velocity.set(Vector2.Zero);
			break;
		case DASH:
			// find direction to target
			dashTimer = 0;
			velocity.set(tracked.getX(), tracked.getY());
			velocity.sub(unit.getX(), unit.getY());
			velocity.nor().scl(DASH_SPEED);
			targetVelocity.set(velocity);
			setInvulnerable(true);
			break;
		case RECOVER:
			dashTimer = 0;
			setInvulnerable(false);
			destination.set(SHMUP.rng.nextFloat(), SHMUP.rng.nextFloat());
			destination.nor().scl(RECOVER_DISTANCE)
					.add(unit.getX(), unit.getY());
			break;
		case TRACK:
			unit.velocity.set(Vector2.Zero);
			break;
		case WAIT:
			unit.velocity.set(Vector2.Zero);
			break;
		default:
			break;
		}
		currentState = state;
	}

	public void update(float delta) {
		if (alpha < 1) {
			alpha += delta;
			if (alpha > 1) {
				alpha = 1;
			}
		} else {
			setInteractable(true);
			switch (currentState) {
			case RECOVER:
				updateRecover(delta);
				break;
			case CHARGE:
				dashTimer += delta;
				if (dashTimer >= DASH_CHARGE_TIMER) {
					setState(State.DASH);
				}
				break;
			case DASH:
				updateDash(delta);
				break;
			case TRACK:
				updateTrack(delta);
				break;
			case READY:
				if (tracked != null) {
					setState(State.TRACK);
				}
				break;
			case WAIT:
				break;
			default:
				break;
			}
			unit.update(delta);
			shield.setVisible(isInvulnerable());
			hitbox.setPosition(unit.getX() + unit.getOriginX(), unit.getY()
					+ unit.getOriginY());
		}
	}

	private void updateRecover(float delta) {
		velocity.set(destination.x - unit.getX(), destination.y - unit.getY());
		velocity.nor().scl(RECOVER_SPEED);
		// update position
		unit.velocity.set(velocity);
		dashTimer += delta;

		if (destination.dst(unit.getX(), unit.getY()) < 5
				|| dashTimer > RECOVER_DURATION) {
			velocity.set(0, 0);
			setState(State.READY);
		}
	}

	private void updateTrack(float delta) {
		// find destination point
		destination.set(tracked.getX(), tracked.getY());

		// find tracking velocity
		velocity.set(destination.x - unit.getOriginPosX(),
				destination.y - unit.getOriginPosY());
		velocity.nor().scl(TRACK_SPEED);

		// update position
		unit.velocity.set(velocity);

		// test state swap
		if (destination.dst(unit.getX(), unit.getY()) < DASH_THRESHOLD) {
			PooledEffect dashGlow = SHMUP.dash_particles.obtain();
			unit.addParticles(dashGlow, new DashEnder(dashGlow));
			setState(State.CHARGE);
			return;
		}

	}

	private void updateDash(float delta) {
		setInvulnerable(true);
		dashTimer += delta;
		if (dashTimer < DASH_DURATION) {
			unit.velocity.set(velocity);
		} else {
			setState(State.RECOVER);
		}

	}

	@Override
	public void onDeath() {
		setState(State.WAIT);
		unit.clearParticles();
		SHMUP.explosion.play();
		PooledEffect explode = SHMUP.explosion_particles.obtain();
		unit.addParticles(explode, new ExplosionEnder(explode));
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
		unit.draw(batch, alpha);
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
		case WAIT:
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
