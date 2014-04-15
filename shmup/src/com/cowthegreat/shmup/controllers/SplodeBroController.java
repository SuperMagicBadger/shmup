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
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;
import com.cowthegreat.shmup.graphics.TexturedCircle;

public class SplodeBroController extends EnemyController {

	public enum State {
		WAIT, TRACK, CHARGE, EXPLODE, DEAD
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
			setState(State.DEAD);
			setDispose(true);
			efct.free();
		}
	}

	public static final float MOVE_SPEED = 100;
	public static final float EXPLODE_RADIUS = 225;
	public static final float EXPLODE_TRIGER_RADIUS = 75;

	GameSprite unit;
	GameSprite center;
	TextureRegion marker, splodeMarker;
	Polygon hitbox;
	State state;
	boolean dead;
	
	Circle explodeCircle;
	Vector2 velocity;
	
	public TexturedCircle circle;

	public SplodeBroController() {
		velocity = new Vector2();
		hitbox = new Polygon(new float[] { -20, 20, -20, -20, 20, -20, 20, 20 });

		explodeCircle = new Circle(0, 0, EXPLODE_RADIUS);
		setState(State.WAIT);
	}

	@Override
	public void initialize(Skin s) {
		unit = new GameSprite(s.getRegion("sploode_bro"));
		center = new GameSprite(new Animation(0.1f, s.getAtlas().findRegions("splode_bro_center"), Animation.LOOP_PINGPONG));
		hitbox.setPosition(unit.getOriginPosX(), unit.getOriginPosY());
		explodeCircle.setPosition(unit.getX(), unit.getY());
		marker = s.getRegion("splode_bro_marker");
		splodeMarker = s.getRegion("marker");
		
		circle = new TexturedCircle();
		circle.texRegion = s.getRegion("splode_bro_splode_radius");
		circle.circle = explodeCircle;
		circle.girth = explodeCircle.radius;
		circle.count = 30;
		circle.alhpa = 0.10f;
	}

	@Override
	public GameSprite getControlled() {
		return unit;
	}

	@Override
	public void drawHitbox(ShapeRenderer shapes) {
		Color c = shapes.getColor();
		shapes.setColor(Color.RED);
		shapes.rect(hitbox.getBoundingRectangle().x,
				hitbox.getBoundingRectangle().y,
				hitbox.getBoundingRectangle().width,
				hitbox.getBoundingRectangle().height);
		shapes.setColor(Color.GREEN);
		shapes.polygon(hitbox.getTransformedVertices());
		shapes.setColor(Color.RED);
		shapes.circle(explodeCircle.x, explodeCircle.y, explodeCircle.radius);
		shapes.circle(unit.getOriginPosX(), unit.getOriginPosY(), EXPLODE_TRIGER_RADIUS);
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
	}

	public boolean isExploding() {
		return state == State.EXPLODE;
	}

	private void setState(State s) {
		switch (s) {
		case WAIT:
			break;
		case CHARGE:
			PooledEffect efct = SHMUP.gsplode_charge_particles.obtain();
			efct.start();
			unit.addParticles(efct, new ChargeEnder(efct));
			unit.velocity.set(Vector2.Zero);
			break;
		case EXPLODE:
			break;
		case TRACK:
			break;
		case DEAD:
			break;
		default:
			break;
		}
		state = s;
	}

	public void update(float delta) {
		switch (state) {
		case CHARGE:
			unit.velocity.set(Vector2.Zero);
			break;
		case EXPLODE:
			setState(State.WAIT);
			setInvulnerable(true);
			break;
		case TRACK:
			updateTrack(delta);
			break;
		case WAIT:
			if (getTracked() != null) {
				setState(State.TRACK);
			}
			break;
		case DEAD:
			break;
		default:
			break;
		}
		unit.update(delta);
		center.update(delta);
		center.setPosition(unit.getX(), unit.getY());
		hitbox.setPosition(unit.getOriginPosX(), unit.getOriginPosY());
		explodeCircle.setPosition(unit.getOriginPosX(), unit.getOriginPosY());
	}

	public void updateTrack(float delta) {
		if (!isDead()) {
			velocity.set(getTracked().getOriginPosX(), getTracked().getOriginPosY());
			velocity.sub(unit.getOriginPosX(), unit.getOriginPosY());

			if (velocity.len2() < EXPLODE_TRIGER_RADIUS * EXPLODE_TRIGER_RADIUS) {
				setState(State.CHARGE);
			}

			velocity.nor().scl(MOVE_SPEED);

			unit.velocity.set(velocity);
		}
	}

	@Override
	public void draw(SpriteBatch batch) {
		center.draw(batch);
		unit.draw(batch);
	}

	@Override
	public int pointValue() {
		return 1;
	}

	public boolean applyExplosion(UnitController unit, Scoreboard board) {
		if (isExploding()) {
			Polygon hb = unit.getHitBox();

			if (Intersector.overlaps(explodeCircle, hb.getBoundingRectangle())) {
				float[] xy = hb.getTransformedVertices();
				for (int i = 0; i < xy.length - 1; i += 2) {
					if (explodeCircle.contains(xy[i], xy[i + 1])) {
						unit.damage(10);
						if(unit.isDead() && !(unit instanceof PlayerController)){
							board.currentKills++;
						}
						return true;
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
		if(state == State.CHARGE || state == State.EXPLODE){
			return splodeMarker;
		}
		return marker;
	}

	public boolean isCharging() {
		return state == State.CHARGE;
	}

}
