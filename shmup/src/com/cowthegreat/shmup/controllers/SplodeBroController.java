package com.cowthegreat.shmup.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.Scoreboard;
import com.cowthegreat.shmup.behaviors.AlphaBehavior;
import com.cowthegreat.shmup.behaviors.Behavior;
import com.cowthegreat.shmup.behaviors.ChaseBehavior;
import com.cowthegreat.shmup.behaviors.ExplodeBehavior;
import com.cowthegreat.shmup.behaviors.MoveBehavior;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;
import com.cowthegreat.shmup.graphics.PolyTools;
import com.cowthegreat.shmup.graphics.TexturedCircle;

public class SplodeBroController extends EnemyController {

	public enum State {
		WAIT, TRACK, CHARGE, EXPLODE
	}

	public class ChargeEnder implements ParticleEffectListener {
		PooledEffect efct;

		public ChargeEnder(PooledEffect efct) {
			this.efct = efct;
		}

		@Override
		public void effectFinished() {
			setState(State.EXPLODE);
			efct.free();

			PooledEffect effect = SHMUP.gsplode_particles.obtain();
			unit.addParticles(effect, new ExplodeEnder(effect));
		}
	}

	public class ExplodeEnder implements ParticleEffectListener {
		PooledEffect efct;

		public ExplodeEnder(PooledEffect efct) {
			this.efct = efct;
		}

		@Override
		public void effectFinished() {
			setDispose(true);
			setInvulnerable(false);
			efct.free();
		}
	}

	public static final float MOVE_SPEED = 100;
	public static final float EXPLODE_RADIUS = 225;
	public static final float EXPLODE_TRIGER_RADIUS = 75;

	public static final float EXPLODE_DURATION = 1;
	public static final float CHARGE_DURATION = 2;

	GameSprite unit;
	TextureRegion marker, splodeMarker;
	Polygon hitbox;
	public State currentState;

	boolean dead;

	AlphaBehavior ab;
	ChaseBehavior cb;
	MoveBehavior mb;
	ExplodeBehavior eb;
	Behavior activeBehavior;

	Circle explodeCircle;
	Vector2 velocity;

	public TexturedCircle circle;

	public SplodeBroController(Skin s) {
		velocity = new Vector2();
		hitbox = new Polygon(new float[] { -20, 20, -20, -20, 20, -20, 20, 20 });

		explodeCircle = new Circle(0, 0, EXPLODE_RADIUS);
		setState(State.WAIT);
		unit = new GameSprite(s.getRegion("sploode_bro"));
		GameSprite center = new GameSprite(new Animation(0.1f, s.getAtlas()
				.findRegions("splode_bro_center"), Animation.LOOP_PINGPONG));
		unit.addChild(center);

		marker = s.getRegion("splode_bro_marker");
		splodeMarker = s.getRegion("marker");

		circle = new TexturedCircle();
		circle.texRegion = s.getRegion("splode_bro_splode_radius");
		circle.circle = explodeCircle;
		circle.girth = explodeCircle.radius;
		circle.count = 30;
		circle.alhpa = 0.10f;

		ab = new AlphaBehavior();
		ab.setDuration(2);
		ab.setController(this);

		cb = new ChaseBehavior();
		cb.setSpeed(MOVE_SPEED);
		cb.setDistance(0, EXPLODE_TRIGER_RADIUS);
		cb.setController(this);

		mb = new MoveBehavior();
		mb.setController(this);
		mb.setDirection(1, 0);
		mb.setSpeed(0);

		eb = new ExplodeBehavior();
		eb.setController(this);
	}

	@Override
	public void initialize(Skin s) {
		setInteractable(false);
		setDispose(false);

		dead = false;
		setState(State.WAIT);

		unit.clearParticles();

		ab.reset();
		activeBehavior = ab;
	}

	@Override
	public GameSprite getControlled() {
		return unit;
	}

	@Override
	public void drawHitbox(ShapeRenderer shapes) {
		Color c = shapes.getColor();
		shapes.setColor(Color.RED);
		PolyTools.drawPolygon(shapes, hitbox);
		shapes.setColor(Color.RED);
		shapes.circle(explodeCircle.x, explodeCircle.y, explodeCircle.radius);
		shapes.circle(unit.getOriginPosX(), unit.getOriginPosY(),
				EXPLODE_TRIGER_RADIUS);
		shapes.setColor(c);
	}

	@Override
	public Polygon getHitBox() {
		return hitbox;
	}

	@Override
	public void damage(int dam) {
		if (!isInvulnerable() && !isDead()) {
			dead = true;
			onDeath();
		}
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	@Override
	public void onDeath() {
		PooledEffect efct = SHMUP.explosion_particles.obtain();
		SHMUP.explosion.play();
		unit.addParticles(efct, new ExplodeEnder(efct));

		eb.setDirection(getTracked());
		eb.setSpeed(MOVE_SPEED * 2);

		activeBehavior = eb;

		setState(State.WAIT);
	}

	public boolean isExploding() {
		return currentState == State.EXPLODE;
	}

	private void setState(State s) {
		switch (s) {
		case TRACK:
			cb.reset();
			cb.setTarget(getTracked());
			cb.setDistance(0, EXPLODE_TRIGER_RADIUS);
			cb.setSpeed(MOVE_SPEED);
			activeBehavior = cb;
			break;
		case EXPLODE:
			PooledEffect effect = SHMUP.gsplode_particles.obtain();
			unit.addParticles(effect, new ExplodeEnder(effect));
			setInvulnerable(true);
			mb.reset();
			mb.setSpeed(0);
			mb.setDuration(EXPLODE_DURATION);
			activeBehavior = mb;
			break;
		case CHARGE:
			PooledEffect charge = SHMUP.gsplode_charge_particles.obtain();
			unit.addParticles(charge, new ChargeEnder(charge));
			mb.reset();
			mb.setSpeed(0);
			mb.setDuration(CHARGE_DURATION);
			activeBehavior = mb;
			break;
		case WAIT:
			break;
		default:
			break;
		}
		currentState = s;
	}

	public void update(float delta) {
		activeBehavior.updtae(delta);

		if (activeBehavior.complete()) {
			switch (currentState) {
			case CHARGE:
				setState(State.EXPLODE);
				break;
			case EXPLODE:
				setState(State.WAIT);
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

		unit.update(delta);
		hitbox.setPosition(unit.getOriginPosX(), unit.getOriginPosY());
		explodeCircle.setPosition(unit.getOriginPosX(), unit.getOriginPosY());
	}

	@Override
	public void draw(SpriteBatch batch) {
		unit.draw(batch, getAlpha());
	}

	@Override
	public int pointValue() {
		return 1;
	}

	public boolean applyExplosion(UnitController unit, Scoreboard board) {
		if (isExploding()) {
			if (!unit.isDead()) {
				Polygon hb = unit.getHitBox();
				if (Intersector.overlaps(explodeCircle,
						hb.getBoundingRectangle())) {
					float[] xy = hb.getTransformedVertices();
					for (int i = 0; i < xy.length - 1; i += 2) {
						if (explodeCircle.contains(xy[i], xy[i + 1])) {
							unit.damage(10);
							if (unit.isDead()
									&& !(unit instanceof PlayerController)) {
								board.currentKills++;
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void applyShield() {
	}

	@Override
	public TextureRegion radarMarker() {
		if (currentState == State.CHARGE || currentState == State.EXPLODE) {
			return splodeMarker;
		}
		return marker;
	}

	public boolean isCharging() {
		return currentState == State.CHARGE;
	}

	@Override
	public boolean isSeperable() {
		return !isExploding();
	}
}
