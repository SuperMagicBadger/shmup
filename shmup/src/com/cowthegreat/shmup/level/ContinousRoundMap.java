package com.cowthegreat.shmup.level;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.Scoreboard;
import com.cowthegreat.shmup.controllers.DashBroController;
import com.cowthegreat.shmup.controllers.EnemyController;
import com.cowthegreat.shmup.controllers.EnemyControllerFactory;
import com.cowthegreat.shmup.controllers.IdiotBroController;
import com.cowthegreat.shmup.controllers.PlayerController;
import com.cowthegreat.shmup.controllers.ShieldBroController;
import com.cowthegreat.shmup.controllers.SplodeBroController;
import com.cowthegreat.shmup.controllers.UnitController;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.TexturedCircle;

public class ContinousRoundMap implements GameMap {

	public float WIDTH;
	public float HEIGHT;
	public float SPAWN_RADIUS = 500;
	public int INITIAL_ENEMIES = 10;
	public int MAX_ENEMIES = 30;
	public int MAXED_LEVEL = 5;

	public float DASH_BRO_LEVEL = 1;
	public float SHIELD_BRO_LEVEL = 2;
	public float SPLODE_BRO_LEVEL = 3;

	private QuadTree qt;
	private ArrayList<EnemyController> activeUnits;

	private SHMUP game;
	private GameSprite player;
	TexturedCircle texCircle;

	private int level;
	private int nextLevelCounter;
	private float levelTimer;

	private Spawner idiotSpawner;
	private Spawner specialSpawner;

	Rectangle bounds;

	// ====================================================
	// CONSTRUCTOR ----------------------------------------
	// ====================================================
	public ContinousRoundMap(SHMUP shmup) {
		this.game = shmup;

		qt = new QuadTree(SPAWN_RADIUS * 2, SPAWN_RADIUS * 2);
		activeUnits = new ArrayList<EnemyController>();

		level = 0;
		levelTimer = 0;

		texCircle = new TexturedCircle();
		texCircle.circle = new Circle(0, 0, SPAWN_RADIUS + 40);
		texCircle.girth = 40;
		texCircle.count = 100;
		texCircle.texRegion = game.skn.getRegion("bg_ring");
		texCircle.generate();

		bounds = new Rectangle(-SPAWN_RADIUS - 50, -SPAWN_RADIUS - 50,
				SPAWN_RADIUS * 2 + 100, SPAWN_RADIUS * 2 + 100);

		idiotSpawner = new Spawner() {
			@Override
			public void spawn(float x, float y) {
				if (activeUnits.size() < getMaxEnemies()) {
					spawnIdiotBro(x, y);
				}
			}

			@Override
			public int count() {
				return normalCount();
			}

			@Override
			public float spawnRate() {
				return normalSpawnRate();
			}
		};
		idiotSpawner.spawnRadius = SPAWN_RADIUS;

		specialSpawner = new Spawner() {
			int choice;
			@Override
			public void spawn(float x, float y) {
				if (choice < 25 && level >= SPLODE_BRO_LEVEL) {
					spawnSplodeBro(x, y);
				} else if (choice < 50 && level >= SHIELD_BRO_LEVEL) {
					spawnShieldBro(x, y);
				} else {
					spawnDashBro(x, y);
				}
			}

			@Override
			public int count() {
				if (getEnemyCount() >= getMaxEnemies()) {
					return 0;
				}
				
				choice = SHMUP.rng.nextInt(100 - getEnemyCount() * 2);
				if (choice < 15 && level >= SPLODE_BRO_LEVEL) {
					return 1;
				}
				if (choice < 50 && level >= SHIELD_BRO_LEVEL) {
					return 1;
				}
				return SHMUP.rng.nextInt(2) + 2;
			}

			@Override
			public float spawnRate() {
				return specialSpawnRate();
			}
		};
		specialSpawner.spawnRadius = SPAWN_RADIUS;
		specialSpawner.spawnTimer = 10;
	}

	// ====================================================
	// ENEMY SPAWNING -------------------------------------
	// ====================================================

	private void spawnDashBro(float x, float y) {
		DashBroController ctrl = EnemyControllerFactory.acquireDash();
		ctrl.initialize(game.skn);
		ctrl.setTracked(player);
		ctrl.getControlled().setPosition(x, y);

		activeUnits.add(ctrl);
	}

	private void spawnShieldBro(float x, float y) {
		ShieldBroController ctrl = EnemyControllerFactory.acquireShield();
		ctrl.setTracked(player);
		ctrl.getControlled().setPosition(x, y);

		activeUnits.add(ctrl);
	}

	private void spawnSplodeBro(float x, float y) {
		SplodeBroController ctrl = EnemyControllerFactory.acquireSplode();
		ctrl.initialize(game.skn);
		ctrl.setTracked(player);
		ctrl.getControlled().setPosition(x, y);

		activeUnits.add(ctrl);
	}

	private void spawnIdiotBro(float x, float y) {
		IdiotBroController ctrl = EnemyControllerFactory.acquireIdiot();
		ctrl.setTracked(player);
		ctrl.getControlled().setPosition(x, y);
		activeUnits.add(ctrl);
	}

	private void despawn(int index) {
		EnemyController ec = activeUnits.remove(index);
		EnemyControllerFactory.free(ec);
	}

	// ====================================================
	// UPDATE LOOPS ---------------------------------------
	// ====================================================

	// TODO OPTIMIZE
	@Override
	public void update(float delta) {
		qt.clear();

		// udate level
		if (nextLevelCounter <= 0) {
			level++;
			nextLevelCounter = nextLevelScore();
		}

		// update timers
		levelTimer += delta;

		// update spawners
		idiotSpawner.update(delta);
		specialSpawner.update(delta);

		// update enemies and build quad-tree
		for (int i = activeUnits.size() - 1; i >= 0; i--) {
			EnemyController ec = activeUnits.get(i);
			if (ec.isDispose()) {
				despawn(i);
			} else {
				qt.insert(ec);
				ec.setInvulnerable(false);
				ec.update(delta * getLevelSpeed());

			}
		}

		// collisions, auras, and level bounds
		Circle c = new Circle();
		for (EnemyController ec : activeUnits) {

			c.x = ec.getControlled().getX();
			c.y = ec.getControlled().getY();
			c.radius = 75;

			if (ec.isSeperable()) {
				Vector2 push = SHMUP.vector_pool.obtain();
				for (UnitController uc : qt.controllersInRange(c)) {
					if (uc == ec || !uc.isSeperable()) {
						continue;
					}
					// push apart
					Vector2 relation = SHMUP.vector_pool.obtain();
					relation.set(ec.getControlled().getOriginPosX(), ec
							.getControlled().getOriginPosY());
					relation.sub(uc.getControlled().getOriginPosX(), uc
							.getControlled().getOriginPosY());
					relation.nor().scl(100);
					push.add(relation);
					SHMUP.vector_pool.free(relation);
				}
				ec.getControlled().move(push.scl(delta));
				SHMUP.vector_pool.free(push);
			}

			if (ec.isInteractable()) {
				if (ec instanceof ShieldBroController) {
					c.setRadius(ShieldBroController.SHIELD_RADIUS);
					for (UnitController uc : qt.controllersInRange(c)) {
						((ShieldBroController) ec).applyShield(uc);
					}
				} else if (ec instanceof SplodeBroController) {
					if (((SplodeBroController) ec).isExploding()) {
						c.setRadius(SplodeBroController.EXPLODE_RADIUS);
						for (UnitController uc : qt.controllersInRange(c)) {
							((SplodeBroController) ec).applyExplosion(uc,
									game.score);
						}
					}
				}
			}
			leash(ec);
		}

	}

	@Override
	public boolean testCollisions(PlayerController player) {
		for (int i = 0; i < activeUnits.size(); i++) {
			if (!activeUnits.get(i).isDead()
					&& activeUnits.get(i).isInteractable()) {
				if (player.collidesWith(activeUnits.get(i))) {
					if (!player.isDashing) {
						player.damage(1);
						if (player.isDead()) {
							return true;
						}
					} else {
						activeUnits.get(i).damage(1);
						if (activeUnits.get(i).isDead()) {
							game.score.currentKills++;
							nextLevelCounter--;
						}
					}
				}
				else if (activeUnits.get(i) instanceof SplodeBroController) {
					System.out.println("applying splode bro to player " + ((SplodeBroController) activeUnits.get(i)).currentState);
					((SplodeBroController) activeUnits.get(i)).applyExplosion(
							player, game.score);
				}
			}
		}

		return player.isDead();
	}

	@Override
	public void leash(UnitController uc) {
		Vector2 leashLine = SHMUP.vector_pool.obtain();
		GameSprite ps = uc.getControlled();
		leashLine.set(ps.getOriginPosX(), ps.getOriginPosY());
		if (leashLine.len2() > SPAWN_RADIUS * SPAWN_RADIUS) {
			leashLine.limit(SPAWN_RADIUS);
			ps.setPosition(leashLine.x - ps.getOriginX(),
					leashLine.y - ps.getOriginY());
		}
		SHMUP.vector_pool.free(leashLine);
	}

	// ====================================================
	// LEVEL INFORMATION ----------------------------------
	// ====================================================

	@Override
	public int getLevel() {
		return level;
	}

	public int nextLevelScore() {
		if (level < 5) {
			return 10;
		}
		return 10 + level;
	}

	@Override
	public float getLevelSpeed() {
		return 1;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}
	
	/* =========================
	 * NORMALS------------------
	 * =========================
	 */
	
	public float normalSpawnRate() {
		return (float) Math.max(4f - (level / 2), 1);
	}

	public int normalCount() {
//		return Math.min(3, getMaxEnemies() - activeUnits.size());
		return 3;
	}

	/*
	 * =========================
	 * SPECIALS-----------------
	 * =========================
	 */
	public float specialSpawnRate() {
		return 10;
	}
	
	/*
	 * =========================
	 * COUNTS-------------------
	 * =========================
	 */

	@Override
	public ArrayList<EnemyController> getActiveUnits() {
		return activeUnits;
	}
	
	public int getMaxEnemies() {
		return Math.min(MAX_ENEMIES, (level - 1)
				* (MAX_ENEMIES - INITIAL_ENEMIES) / (MAXED_LEVEL - 1)
				+ INITIAL_ENEMIES);
	}

	private int getEnemyCount() {
		return activeUnits.size();
	}

	
	// ====================================================
	// CONTROLS -------------------------------------------
	// ====================================================
	
	@Override
	public void reset(GameSprite playerSprite) {
		player = playerSprite;
		for (int i = activeUnits.size() - 1; i >= 0; i--) {
			despawn(i);
		}
		activeUnits.clear();

		level = 0;
		nextLevelCounter = 0;
		idiotSpawner.reset();
		specialSpawner.reset();
		specialSpawner.spawnTimer = 5; // advance the timer a 'lil to get
										// specials out early
	}

	@Override
	public void recordScore(Scoreboard board) {
		board.currentWave = level;
		board.currentTime = levelTimer;
	}

	// =====================================================
	// RENDERING ------------------------------------------
	// ====================================================
	@Override
	public void draw(ShapeRenderer renderer) {
		for (EnemyController ec : activeUnits) {
			ec.drawHitbox(renderer);
		}
		renderer.circle(0, 0, SPAWN_RADIUS);
	}

	@Override
	public void draw(SpriteBatch batch) {
		for (EnemyController ec : activeUnits) {
			ec.draw(batch);
		}
	}

	@Override
	public void draw(ImmediateModeRenderer renderer, Matrix4 transform) {
		renderer.begin(transform, GL20.GL_TRIANGLE_STRIP);
		texCircle.draw(renderer);
		renderer.end();
		for (EnemyController uc : activeUnits) {
			if (uc.isInteractable()) {
				if (uc instanceof ShieldBroController) {
					renderer.begin(transform, GL20.GL_TRIANGLE_STRIP);
					((ShieldBroController) uc).tcircle.generate();
					((ShieldBroController) uc).tcircle.draw(renderer);
					renderer.end();
				} else if (uc instanceof SplodeBroController) {
					if (((SplodeBroController) uc).isCharging()) {
						renderer.begin(transform, GL20.GL_TRIANGLE_STRIP);
						((SplodeBroController) uc).circle.generate();
						((SplodeBroController) uc).circle.draw(renderer);
						renderer.end();
					}
				}
			}
		}
	}

}
