package com.cowthegreat.shmup;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.cowthegreat.shmup.controllers.DashBroController;
import com.cowthegreat.shmup.controllers.EnemyController;
import com.cowthegreat.shmup.controllers.ObstacleController;
import com.cowthegreat.shmup.controllers.PlayerController;
import com.cowthegreat.shmup.controllers.ShieldBroController;
import com.cowthegreat.shmup.controllers.SplodeBroController;
import com.cowthegreat.shmup.controllers.UnitController;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.TexturedCircle;

public class RoundMap implements GameMap {

	public float WIDTH;
	public float HEIGHT;
	public float SPAWN_RADIUS = 750;
	public int MAX_ENEMIES = 30;

	public int SPEED_UP_LEVEL = 6;
	public float SPEED_UP_RATE = 0.02f;
	public float SPEED_UP_MAX = 1.5f;

	public int SHIELD_BRO_LEVEL = 3;
	public int MAX_SHIELD_BROS = 4;
	public float SHIELD_BRO_SPAWN_RATE = 0.25f;

	public int SPLODE_BRO_LEVEL = 4;
	public int MAX_SPLODE_BROS = 2;

	private QuadTree qt;
	private ArrayList<EnemyController> activeUnits;
	private ArrayList<ObstacleController> obstacles;

	private SHMUP game;
	private GameSprite player;
	TexturedCircle texCircle;
	
	private int level;
	private float levelTimer;

	Rectangle bounds;
	
	public RoundMap(SHMUP shmup) {
		this.game = shmup;

		qt = new QuadTree(SPAWN_RADIUS * 2, SPAWN_RADIUS * 2);
		activeUnits = new ArrayList<EnemyController>(MAX_ENEMIES);
		obstacles = new ArrayList<ObstacleController>();

		level = 0;
		levelTimer = 0;
		
		texCircle = new TexturedCircle();
		texCircle.circle = new Circle(0, 0, SPAWN_RADIUS + 40);
		texCircle.girth = 40;
		texCircle.count = 100;
		texCircle.texRegion = game.skn.getRegion("bg_ring");
		texCircle.generate();
		
		bounds = new Rectangle(-SPAWN_RADIUS - 50, -SPAWN_RADIUS - 50, SPAWN_RADIUS * 2 + 100, SPAWN_RADIUS * 2 + 100);
		
		load("round_config_one");
		
	}

	public void load(String levelFile){
		XmlReader reader = new XmlReader();
		try{
			Element root = reader.parse(Gdx.files.internal("levels/" + levelFile + ".xml"));
			System.out.println(root.getName());
			
			String s = root.getChildByName("size").getText();
			WIDTH = Float.parseFloat(s.substring(0, s.indexOf(':'))) / 2;
			HEIGHT = Float.parseFloat(s.substring(s.indexOf(':') + 1)) / 2;
			SPAWN_RADIUS = Math.max(WIDTH, HEIGHT);
			
			for(Element e : root.getChildrenByName("block")){
				GameSprite gs = null;
				
				if(e.getChildByName("sprite") != null){
					String sprite = e.getChildByName("sprite").getText();
					gs = new GameSprite(game.skn.getRegion(sprite));
					float rot = 0, x = 0, y = 0;
					float w = gs.getWidth();
					float h = gs.getHeight();
					
					if(e.getChildByName("pos") != null) {
						String spPos = e.getChildByName("pos").getText();
						x = Float.parseFloat(spPos.substring(0, spPos.indexOf(':')));
						y = Float.parseFloat(spPos.substring(spPos.indexOf(':') + 1));
					}
					if(e.getChildByName("rot") != null) {
						rot = Float.parseFloat(e.getChildByName("rot").getText());
					}
					if(e.getChildByName("size") != null) {
						String spSze = root.getChildByName("size").getText();
						w = Float.parseFloat(s.substring(0, spSze.indexOf(':')));
						h = Float.parseFloat(s.substring(spSze.indexOf(':') + 1));
					}
					gs.setBounds(x - w / 2, y - h / 2, w, h);
					gs.setRotation(rot);
				}
				
				if(gs != null){
					ObstacleController oc = new ObstacleController(gs);
					System.out.println("rot: " + gs.getRotation() + " - (" + gs.getX() + ", " + gs.getY() + ")");
					for(float f : oc.getHitBox().getTransformedVertices()){
						System.out.print(f + " ");
					}
					System.out.println();
					obstacles.add(new ObstacleController(gs));
					
				}
			}
			
			
		} catch (IOException ex){
			
		}
	}
	
	// ====================================================
	// ENEMY SPAWNING -------------------------------------
	// ====================================================

	@Override
	public void spawnNextLevel() {
		level++;
		int count = getEnemyCount();

		int shieldCount = getShieldCount();
		int explodeCount = getSplodeCount();
		int dashCount = count - (shieldCount + explodeCount);

		for (int i = 0; i < dashCount; i++) {
			float index = SHMUP.rng.nextFloat();
			spawnDashBro(spawnX(index), spawnY(index));
		}
		for (int i = 0; i < shieldCount; i++) {
			spawnShieldBro(spawnX(i / (float) shieldCount), spawnY(i
					/ (float) shieldCount));
		}
		for (int i = 0; i < explodeCount; i++) {
			float index = SHMUP.rng.nextFloat();
			spawnSplodeBro(spawnX(index), spawnY(index));
		}
	}

	private DashBroController spawnDashBro(float x, float y) {
		DashBroController ctrl = new DashBroController();
		ctrl.initialize(game.skn);
		ctrl.setTracked(player);
		ctrl.getControlled().setPosition(x, y);

		activeUnits.add(ctrl);
		return ctrl;
	}

	private ShieldBroController spawnShieldBro(float x, float y) {
		ShieldBroController ctrl = new ShieldBroController();
		ctrl.initialize(game.skn);
		ctrl.setTracked(player);
		ctrl.getControlled().setPosition(x, y);

		activeUnits.add(ctrl);
		return ctrl;
	}

	private SplodeBroController spawnSplodeBro(float x, float y) {
		SplodeBroController ctrl = new SplodeBroController();
		ctrl.initialize(game.skn);
		ctrl.setTracked(player);
		ctrl.getControlled().setPosition(x, y);

		activeUnits.add(ctrl);
		return ctrl;
	}

	private void despawn(int index) {
		activeUnits.remove(index);
	}

	public float spawnX(float i) {
		return (float) Math.cos(2 * Math.PI * i) * SPAWN_RADIUS;
	}

	public float spawnY(float i) {
		return (float) Math.sin(2 * Math.PI * i) * SPAWN_RADIUS;
	}

	// ====================================================
	// UPDATE LOOPS ---------------------------------------
	// ====================================================

	// TODO OPTIMIZE
	@Override
	public void update(float delta) {
		qt.clear();

		// update spawn timer
		levelTimer += delta;
		if (activeUnits.isEmpty()) {
			spawnNextLevel();
		}

		for(ObstacleController oc : obstacles){
			oc.update(delta);
		}
		
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

		Circle c = new Circle();
		for (EnemyController ec : activeUnits) {

			Vector2 push = SHMUP.vector_pool.obtain();

			c.x = ec.getControlled().getX();
			c.y = ec.getControlled().getY();
			c.radius = 75;
			for (UnitController uc : qt.controllersInRange(c)) {
				if (uc == ec) {
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
			leash(ec);
		}

	}

	@Override
	public boolean testCollisions(PlayerController player) {
		for (int i = 0; i < activeUnits.size(); i++) {
			if (!activeUnits.get(i).isDead()) {
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
						}
					}
				} else if (activeUnits.get(i) instanceof SplodeBroController) {
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
		
		// obstacles
		for(ObstacleController oc : obstacles){
			oc.collide(uc);
		}
	}

	// ====================================================
	// LEVEL INFORMATION ----------------------------------
	// ====================================================

	private int getEnemyCount() {
		return Math.min(MAX_ENEMIES, level * 2);
	}

	private int getShieldCount() {
		if (level < SHIELD_BRO_LEVEL) {
			return 0;
		}
		if (level == SHIELD_BRO_LEVEL) {
			return 1;
		}
		return SHMUP.rng.nextInt(SHIELD_BRO_LEVEL) + 1;
	}

	private int getSplodeCount() {
		if (level < SPLODE_BRO_LEVEL) {
			return 0;
		}
		return SHMUP.rng.nextInt(MAX_SPLODE_BROS) + 1;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public float getLevelSpeed() {
		if (level < SPEED_UP_LEVEL) {
			return 1;
		}
		return Math.min((level - SPEED_UP_LEVEL) * SPEED_UP_RATE + 1,
				SPEED_UP_MAX);
	}

	@Override
	public void reset(GameSprite playerSprite) {
		player = playerSprite;
		activeUnits.clear();
		level = 0;
	}

	@Override
	public void recordScore(Scoreboard board) {
		board.currentWave = level;
		board.currentTime = levelTimer;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public ArrayList<EnemyController> getActiveUnits() {
		return activeUnits;
	}

	// =====================================================
	// RENDERING ------------------------------------------
	// ====================================================
	@Override
	public void draw(ShapeRenderer renderer) {
		for (EnemyController ec : activeUnits) {
			ec.drawHitbox(renderer);
		}
		for(ObstacleController oc : obstacles){
			oc.drawHitbox(renderer);
		}
		renderer.circle(0, 0, SPAWN_RADIUS);
	}

	@Override
	public void draw(SpriteBatch batch) {
		for(ObstacleController oc : obstacles){
			oc.draw(batch);
		}
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
