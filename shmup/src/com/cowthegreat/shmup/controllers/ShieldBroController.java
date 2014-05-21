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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.behaviors.AlphaBehavior;
import com.cowthegreat.shmup.behaviors.Behavior;
import com.cowthegreat.shmup.behaviors.ChaseBehavior;
import com.cowthegreat.shmup.behaviors.ExplodeBehavior;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;
import com.cowthegreat.shmup.graphics.PolyTools;
import com.cowthegreat.shmup.graphics.TexturedCircle;

public class ShieldBroController extends EnemyController {

	private class ExplosionEnder implements ParticleEffectListener {
		private PooledEffect efct;

		ExplosionEnder(PooledEffect efct) {
			this.efct = efct;
		}

		@Override
		public void effectFinished() {
			setDispose(true);
			efct.free();
		}
	}

	public GameSprite sprite;
	public GameSprite shieldAnimation;
	TextureRegion marker;

	Polygon hitbox;
	public Circle shieldAura;
	private boolean dead;

	public static final float SHIELD_RADIUS = 300;
	public static final float MOVE_SPEED = 150;
	public static final float DODGE_SPEED = MOVE_SPEED * 4.5f;
	public static final float STANDOFF_DISTANCE = 150;
	public static final float STANDOFF_TOLERANCE = 50;

	public TexturedCircle tcircle = new TexturedCircle();

	AlphaBehavior ab;
	ChaseBehavior cb;
	ExplodeBehavior eb;
	Behavior activeBehavior;
	
	public ShieldBroController(Skin s) {
		dead = false;

		float[] points = new float[8];
		points[6] = -22;
		points[7] = -22;
		points[4] = 22;
		points[5] = -22;
		points[2] = 22;
		points[3] = 22;
		points[0] = -22;
		points[1] = 22;

		hitbox = new Polygon(points);

		shieldAura = new Circle();
		shieldAura.radius = SHIELD_RADIUS;

		sprite = new GameSprite(s.getRegion("shield_bro"));
		shieldAnimation = new GameSprite(
				new Animation(0.2f, s.getAtlas().findRegions(
						"shield_bro_shield_anim"), Animation.LOOP_PINGPONG));
		sprite.addChild(shieldAnimation);
		
		marker = s.getRegion("shield_bro_marker");

		tcircle.circle = shieldAura;
		tcircle.count = 30;
		tcircle.girth = shieldAura.radius;
		tcircle.texRegion = s.getRegion("shield_bro_shield_radius");
		tcircle.alhpa = 0.10f;
		
		ab = new AlphaBehavior();
		ab.setController(this);
		ab.setDuration(1);
		
		cb = new ChaseBehavior();
		cb.setController(this);
		cb.setDistance(STANDOFF_DISTANCE - STANDOFF_TOLERANCE, STANDOFF_DISTANCE);
		cb.setSpeed(MOVE_SPEED);
		
		eb = new ExplodeBehavior();
		eb.setController(this);
		eb.setSpeed(MOVE_SPEED * 2);
		
		activeBehavior = ab;
	}

	public void initialize(Skin s) {
		setAlpha(0);
		setInteractable(false);
		setDispose(false);
		
		dead = false;
		
		ab.reset();
		activeBehavior = ab;
	}

	@Override
	public GameSprite getControlled() {
		return sprite;
	}

	@Override
	public void update(float delta) {
		activeBehavior.updtae(delta);
		
		if(activeBehavior.complete()){
			setInteractable(true);
			cb.setTarget(getTracked());
			activeBehavior = cb;
		}
		sprite.update(delta);
		hitbox.setPosition(sprite.getX() + sprite.getOriginX(), sprite.getY()
				+ sprite.getOriginY());
		shieldAura.setPosition(sprite.getX() + sprite.getOriginX(),
				sprite.getY() + sprite.getOriginY());
	}

	@Override
	public void damage(int dam) {
		if (!isInvulnerable()) {
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
		SHMUP.explosion.play();
		sprite.clearParticles();
		PooledEffect explode = SHMUP.explosion_particles.obtain();
		sprite.addParticles(explode, new ExplosionEnder(explode));
	}

	@Override
	public void drawHitbox(ShapeRenderer shapes) {
		Color c = shapes.getColor();
		shapes.setColor(Color.RED);
		Rectangle r = hitbox.getBoundingRectangle();
		shapes.rect(r.x, r.y, r.width, r.height);
		shapes.setColor(Color.GREEN);
		PolyTools.drawPolygon(shapes, hitbox);
		shapes.setColor(Color.BLUE);
		shapes.circle(shieldAura.x, shieldAura.y, shieldAura.radius);
		shapes.setColor(c);
	}

	@Override
	public Polygon getHitBox() {
		return hitbox;
	}

	public final int pointValue() {
		return 2;
	}

	public void applyShield(UnitController ect) {
		if (isDead())
			return;
		if (!ect.isInvulnerable() && !ect.isDead()) {
			if (Intersector.overlaps(shieldAura, ect.getHitBox()
					.getBoundingRectangle())) {
				((EnemyController) ect).applyShield();
			}
		}
	}

	@Override
	public void applyShield() {
	}

	@Override
	public void draw(SpriteBatch batch) {
		sprite.draw(batch, getAlpha());
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
