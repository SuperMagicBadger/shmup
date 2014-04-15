package com.cowthegreat.shmup.controllers;

import java.util.ArrayList;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.TexturedCircle;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;

public class ShieldBroController extends EnemyController {

	private class ExplosionEnder implements ParticleEffectListener {
		private PooledEffect efct;
		ExplosionEnder(PooledEffect efct){
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
	private Vector2 velocity;
	
	ArrayList<EnemyController> shieldList;

	public static final float SHIELD_RADIUS = 200;
	public static final float MOVE_SPEED = PlayerController.playerSpeed;
	public static final float DODGE_SPEED = MOVE_SPEED * 4.5f;
	public static final float STANDOFF_DISTANCE = 150;
	public static final float STANDOFF_TOLERANCE = 50;
	
	public TexturedCircle tcircle = new TexturedCircle();

	public ShieldBroController() {
		shieldList = new ArrayList<EnemyController>();
		dead = false;
		velocity = new Vector2();

		hitbox = new Polygon(new float[] { -22, -22, 22, -22, 22, 22, -22, 22 });

		shieldAura = new Circle();
		shieldAura.radius = SHIELD_RADIUS;
	}
	
	public void initialize(Skin s){
		sprite = new GameSprite(s.getRegion("shield_bro"));
		shieldAnimation = new GameSprite(new Animation(0.2f, s.getAtlas().findRegions("shield_bro_shield_anim"), Animation.LOOP_PINGPONG));
		hitbox.setPosition(sprite.getX() + sprite.getOriginX(), sprite.getY()
				+ sprite.getOriginY());
		shieldAura.setPosition(sprite.getX() + sprite.getOriginX(),
				sprite.getY() + sprite.getOriginY());
		marker = s.getRegion("shield_bro_marker");
		
		tcircle.circle = shieldAura;
		tcircle.count = 30;
		tcircle.girth = shieldAura.radius;
		tcircle.texRegion = s.getRegion("shield_bro_shield_radius");
		tcircle.alhpa = 0.10f;
	}

	@Override
	public GameSprite getControlled() {
		return sprite;
	}

	@Override
	public void update(float delta) {
		if (tracked != null) {
			// find destination, tempe'd in the velocity vector
			velocity.set(sprite.getX(), sprite.getY());
			velocity.sub(tracked.getX(), tracked.getY());
			velocity.nor().scl(STANDOFF_DISTANCE);
			velocity.add(tracked.getX(), tracked.getY());
			float dist = velocity.dst(sprite.getX(), sprite.getY());

			if (dist > STANDOFF_TOLERANCE) {
				// find velocity
				velocity.sub(sprite.getX(), sprite.getY());
				velocity.nor().scl(MOVE_SPEED);
				velocity.limit(MOVE_SPEED);
			} else {
				velocity.set(sprite.velocity);
				velocity.nor().scl(4 * MOVE_SPEED);
				velocity.set(Vector2.Zero);
			}
		} else {
			velocity.set(Vector2.Zero);
		}
		sprite.velocity.set(velocity);
		sprite.update(delta);
		shieldAnimation.update(delta);
		shieldAnimation.setPosition(sprite.getX(), sprite.getY());
		hitbox.setPosition(sprite.getOriginPosX(), sprite.getOriginPosY());
		shieldAura.setPosition(sprite.getOriginPosX(), sprite.getOriginPosY());
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
		for (EnemyController ec : shieldList) {
			ec.setInvulnerable(false);
		}
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
		shapes.polygon(hitbox.getTransformedVertices());
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
		if(isDead()) return;
		if (!ect.isInvulnerable() && !ect.isDead()) {
			if (Intersector.overlaps(shieldAura, ect.getHitBox()
					.getBoundingRectangle())) {
				((EnemyController)ect).applyShield();
			}
		}
	}
	
	@Override
	public void applyShield() {	
	}

	@Override
	public void draw(SpriteBatch batch){
		sprite.draw(batch);
		shieldAnimation.draw(batch);
	}
	
	@Override
	public TextureRegion radarMarker() {
		return marker;
	}
}
