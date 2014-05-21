package com.cowthegreat.shmup.controllers;

import java.text.Format;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.GameSprite.ParticleEffectListener;


public abstract class PlayerController implements UnitController {
	public interface DashEnder {
		void onDashEnd(float x, float y);
	}

	// constants
	public static final int STARTING_PLAYER_HEALTH = 1;

	public static final float playerSpeed = 300;
	public static final float rotationalSpeed = 512;

	public static final float dashSpeed = 2000;
	public static final float dashDistance = 200;
	public static final float dashDuration = dashDistance / dashSpeed;
	public static final float dashCooldown = 0.25f;

	public static final float HITBOX_REST_SCALE_X = 0.5f;
	public static final float HITBOX_REST_SCALE_Y = 0.5f;
	public static final float HITBOX_ACTIVE_SCALE_X = 4;
	public static final float HITBOX_ACTIVE_SCALE_Y = 2.75f;

	// vars
	protected Camera camera;
	protected GameSprite player;
	protected GameSprite playerShield;
	protected GameSprite playerCharged;
	protected Polygon hitbox;

	protected SHMUP game;

	private boolean invulnerable = false;
	public int health;

	private float targetAngle;

	public boolean isDashing;
	protected float dashTimer;
	protected float dashCooldownTimer;
	private Vector3 dashCursor = new Vector3();
	protected Vector2 dashDirection = new Vector2();

	private Vector3 rotationCursor = new Vector3();
	private Vector2 direction = new Vector2();

	public DashEnder dashEnder;

	// INITIALIZATION
	public void setGame(SHMUP game) {
		this.game = game;
	}

	public void setCamera(Camera cam) {
		camera = cam;
	}

	public void initialize(Skin s) {
		player = new GameSprite(s.getRegion("player"));
		playerCharged = new GameSprite(s.getRegion("player_charged"));
		playerShield = new GameSprite(s.getRegion("player_shield"));

		playerCharged.setVisible(true);
		playerShield.setVisible(false);
		
		float[] newPoints = new float[4 * 2];
		
		newPoints[6] = 10;
		newPoints[7] = player.getRegionHeight() - 5;
		newPoints[4] = 10;
		newPoints[5] = 5;
		newPoints[2] = player.getRegionWidth() - 10;
		newPoints[3] = 8;
		newPoints[0] = player.getRegionWidth() - 10;
		newPoints[1] = player.getRegionHeight() - 8;		
		
		hitbox = new Polygon(newPoints);
		hitbox.setOrigin(player.getRegionWidth() / 2, player.getRegionHeight() / 2);
		hitbox.setScale(HITBOX_REST_SCALE_X, HITBOX_REST_SCALE_Y);
	}

	@Override
	public GameSprite getControlled() {
		return player;
	}

	// DAMAGE CONTROL

	@Override
	public void damage(int dam) {
		if (!isInvulnerable() && !isDead()) {
			health -= dam;
			if (isDead())
				onDeath();
		}
	}

	@Override
	public boolean isDead() {
		return health <= 0;
	}

	@Override
	public final void setInvulnerable(boolean set) {
		invulnerable = set;
		playerShield.setVisible(set);
		if (isInvulnerable()) {
			hitbox.setScale(HITBOX_ACTIVE_SCALE_X, HITBOX_ACTIVE_SCALE_Y);
		} else {
			hitbox.setScale(HITBOX_REST_SCALE_X, HITBOX_REST_SCALE_Y);
		}
	}

	@Override
	public final boolean isInvulnerable() {
		return invulnerable;
	}

	@Override
	public void onDeath() {
		playerCharged.setVisible(false);
		playerShield.setVisible(false);

		player.velocity.set(0, 0);
		player.rotationalVelocity = 0;

		SHMUP.explosion.play();

		player.clearParticles();
		final PooledEffect explode = SHMUP.explosion_particles.obtain();
		player.addParticles(explode, new ParticleEffectListener() {
			@Override
			public void effectFinished() {
				player.setVisible(false);
				explode.free();
			}
		});
	}

	// DASH CONTROL
	protected void setDash(float x, float y) {
		if (!isDashing && dashCooldownTimer >= dashCooldown) {
			dashCursor.set(x - player.getOriginX(), y - player.getOriginY(), 0);
			camera.unproject(dashCursor);
			dashDirection.set(dashCursor.x, dashCursor.y);
			dashDirection.sub(player.getX(), player.getY());
			dashDirection.nor();

			isDashing = true;
			setInvulnerable(true);
			dashTimer = 0;
			dashCooldownTimer = 0;

			playerCharged.setVisible(false);

			setFacing(dashCursor.x, dashCursor.y);
		}
	}

	protected void updateDash(float delta) {
		if (isDashing) {
			dashTimer += delta;
			isDashing = dashTimer < dashDuration;
			if (!isDashing) {
				setInvulnerable(false);
			}
		} else if (dashCooldownTimer < dashCooldown) {
			dashCooldownTimer += delta;
		} else if (dashCooldownTimer < Float.MAX_VALUE) {
			SHMUP.charged.play(0.50f);
			playerCharged.setVisible(true);
			dashCooldownTimer = Float.MAX_VALUE;
		}
	}

	// UPDATERS

	@Override
	public void draw(SpriteBatch batch) {
		playerShield.setPosition(player.getX(), player.getY());
		playerShield.setRotation(player.getRotation());
		playerCharged.setPosition(player.getX(), player.getY());
		playerCharged.setRotation(player.getRotation());
		player.draw(batch);
		playerCharged.draw(batch);
		playerShield.draw(batch);
	}

	protected void updateRotation(float delta) {
		player.setRotation(targetAngle);
		player.rotationalVelocity = 0;
	}

	public void updateMovement(float delta, float x_percent, float y_percent) {
		if (!isDead()) {
			if (isDashing) {
				player.velocity.set(dashDirection);
			} else {
				player.velocity.set(x_percent , y_percent);
			}
			player.velocity.scl(getSpeed()).limit(getSpeed());
			// prevent rotating back to 0 degrees when not moving
			if (player.velocity.len2() > 0) {
				targetAngle = (player.velocity.angle());
			}
			updateDash(delta);
			updateRotation(delta);
		}
		player.update(delta);
		playerShield.setPosition(player.getX(), player.getY());
		playerShield.setRotation(player.getRotation());
		playerCharged.setPosition(player.getX(), player.getY());
		playerCharged.setRotation(player.getRotation());
		if (hitbox != null) {
			hitbox.setPosition(player.getX(), player.getY());
			hitbox.setRotation(player.getRotation());
		}
	}

	// MOVEMENT CONTROL

	public float getSpeed() {
		if (!isDashing) {
			return playerSpeed;
		} else {
			return dashSpeed;
		}
	}

	public void setFacing(float x, float y) {
		rotationCursor.set(x, y, 0);
		direction.set(player.getOriginPosX() + (player.getWidth() / 2),
				player.getOriginPosY() + (player.getHeight() / 2));
		direction.set(rotationCursor.x - direction.x, rotationCursor.y
				- direction.y);
		player.setRotation(direction.angle());
	}

	public void rotateTo(float angle) {
		targetAngle = angle;
	}

	public boolean collidesWith(UnitController c) {
		Polygon p = c.getHitBox();

		if (p != null) {
			if (p.getBoundingRectangle()
					.overlaps(hitbox.getBoundingRectangle())) {
				return Intersector.overlapConvexPolygons(hitbox, p);
			}
		}

		return false;
	}

	public void drawHitbox(ShapeRenderer shapes) {
		if(isDead()){
			return;
		}
		Color c = shapes.getColor();
		
		// draw hitbox
		if (hitbox == null) {
			Rectangle r = player.getBoundingRectangle();
			shapes.setColor(Color.RED);
			shapes.rect(r.x, r.y, r.width, r.height);
		} else {
			Rectangle r = hitbox.getBoundingRectangle();
			shapes.setColor(Color.GREEN);
			shapes.polygon(hitbox.getTransformedVertices());
			shapes.setColor(Color.RED);
			shapes.rect(r.x, r.y, r.width, r.height);
		}
		
		// draw center of player
		shapes.setColor(Color.ORANGE);
		shapes.circle(player.getOriginPosX(), player.getOriginPosY(), 5);
		
		//draw player velocity
		shapes.setColor(Color.WHITE);
		shapes.line(player.getOriginPosX(), player.getOriginPosY(),
				player.velocity.x / 3 + player.getOriginPosX(),
				player.velocity.y / 3 + player.getOriginPosY());
		
		// draw the hitbox normals
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
		
		//draw facing line
		shapes.setColor(Color.BLUE);
		Vector2 facing = SHMUP.vector_pool.obtain();
		facing.set(50, 0);
		facing.rotate(player.getRotation());
		facing.add(player.getOriginPosX(), player.getOriginPosY());
		shapes.line(player.getOriginPosX(), player.getOriginPosY(), facing.x, facing.y);
		SHMUP.vector_pool.free(facing);
		
		shapes.setColor(c);
	}

	public Polygon getHitBox() {
		return hitbox;
	}
	
	@Override
	public boolean isSeperable() {
		return false;
	}
	
	// RESET TILT CONTROL

	public abstract void reset();
	public abstract void setAngle(float x, float y, float z);
	public abstract void setMesage(Label l, Format f);

}
