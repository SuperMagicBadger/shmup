package com.cowthegreat.shmup.screens;

import java.text.DecimalFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.controllers.PlayerController;
import com.cowthegreat.shmup.graphics.GameOverActor;
import com.cowthegreat.shmup.graphics.GameOverActor.Listener;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.ParallaxBackground;
import com.cowthegreat.shmup.graphics.ParallaxCamera;
import com.cowthegreat.shmup.graphics.Radar;
import com.cowthegreat.shmup.level.ContinousRoundMap;
import com.cowthegreat.shmup.level.GameMap;

public class GameScreen implements Screen {
	SHMUP game;

	public static final String screenTag = "game_scr";

	private ParallaxCamera camera;
	private SpriteBatch batch;
	private SpriteBatch unitBatch;
	private ShapeRenderer shapes;
	private ImmediateModeRenderer immediate;
	private DecimalFormat format;

	private Vector2 cameraVelocity;
	
	private GameMap gm;
	
	private GameOverActor goa;

	private PlayerController playerContrller;

	boolean isPaused = false;
	boolean isGameOver = true;

	private ShaderProgram prgm;
	
	private Stage stage;
	private Label fpsLabel, scoreLabel, acceleromiterLabel;

	private Radar radar;

	public GameScreen(SHMUP shmupGame) {
		game = shmupGame;
		playerContrller = game.playerControls;
		playerContrller.setGame(shmupGame);
		batch = new SpriteBatch();
		unitBatch = new SpriteBatch();
		shapes = new ShapeRenderer();
		immediate = new ImmediateModeRenderer20(false, true, 1);
		camera = new ParallaxCamera(game.gameWidth, game.gameHeight);
		camera.bg = new ParallaxBackground(camera);
		camera.bg.addLayer(game.skn.getRegion("bluestars"), 0.25f);
		camera.bg.addLayer(game.skn.getRegion("biggrid"), 0.5f);
		camera.bg.addLayer(game.skn.getRegion("littlegrid"), 1);

		cameraVelocity = SHMUP.vector_pool.obtain();
		
		gm = new ContinousRoundMap(shmupGame);

		format = new DecimalFormat("##.##");
		format.setMinimumIntegerDigits(2);
		format.setMaximumIntegerDigits(2);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);

		stage = new Stage(game.gameWidth, game.gameHeight, false, batch);
		
		fpsLabel = new Label("asdf", game.skn, "font_20", Color.WHITE);
		scoreLabel = new Label("0", game.skn, "font_20", Color.WHITE);

		fpsLabel.setPosition(0, stage.getHeight() - fpsLabel.getHeight());
		scoreLabel.setPosition(stage.getWidth() - scoreLabel.getWidth(),
				stage.getHeight() - scoreLabel.getHeight());
		
		acceleromiterLabel = new Label("", game.skn, "font_20", Color.WHITE);
		acceleromiterLabel.setPosition(0, 50);

		// SET UP GAME OVER WIDGET
		goa = new GameOverActor(game);
		goa.showResetTiltBtn(false);
		goa.showReset(false);
		goa.pack();

		goa.setListener(new Listener() {
			@Override
			public void onReset() {
				reset();
			}

			@Override
			public void onResume() {
				isPaused = false;
				goa.setVisible(false);
			}

			@Override
			public void onMainMenu() {
				isGameOver = true;
				GameScreen.this.game.setScreen(MainMenuScreen.screenTag);
			}

			@Override
			public void onResetTilt() {
				playerContrller.reset();
			}

			@Override
			public void onHealMax() {
				playerContrller.health = Integer.MAX_VALUE;
			}

			public void onShowSettings() {
				GameScreen.this.game.setScreen(SettingsScreen.screenTag, screenTag);
			};
		});

		goa.setVisible(false);

		stage.addActor(fpsLabel);
		stage.addActor(scoreLabel);
		stage.addActor(acceleromiterLabel);
		stage.addActor(goa);

		radar = new Radar(game);
		
		ShaderProgram.pedantic = false;
		prgm = new ShaderProgram(Gdx.files.internal("shaders/passthrough.vsh"), Gdx.files.internal("shaders/passthrough.fsh"));
		System.out.println(prgm.isCompiled() ? "shader working" : prgm.getLog());
		unitBatch.setShader(prgm);
		
	}

	// ==============================================================
	// RENDERING ----------------------------------------------------
	// ==============================================================
	@Override
	public void render(float delta) {
		// SCREEN UPDATE
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		// GAME UPDATE
		if (!isPaused) {
			gm.update(delta);
			updatePlayer(delta);
			camera.update();
		}

		
		// BG
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		camera.bg.draw(batch);
		radar.draw(batch, camera, gm.getActiveUnits());
		batch.end();
		
		// IMMEDIATES
		Gdx.gl.glEnable(GL20.GL_BLEND);
		gm.draw(immediate, camera.combined);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		// SPRITE RENDERING
		unitBatch.setProjectionMatrix(camera.combined);
		unitBatch.begin();
		gm.draw(unitBatch);
		playerContrller.draw(unitBatch);
		unitBatch.end();

		// HITBOX RENDERING
		if (game.settings.drawHitboxes) {
			shapes.setProjectionMatrix(camera.combined);
			shapes.begin(ShapeType.Line);
			shapes.setColor(Color.RED);
			playerContrller.drawHitbox(shapes);
			gm.draw(shapes);
			shapes.end();
			
		}
		
		// UI RENDERING
		fpsLabel.setText("" + Gdx.graphics.getFramesPerSecond());
//		playerContrller.setMesage(acceleromiterLabel, format);
//		acceleromiterLabel.pack();
//		acceleromiterLabel.setPosition(50, 50);
		scoreLabel.setText("Wave " + gm.getLevel() + " Score: "
				+ game.score.currentKills);
		scoreLabel.pack();
		scoreLabel.setPosition(stage.getWidth() - scoreLabel.getWidth(),
				stage.getHeight() - scoreLabel.getHeight());
		stage.act(delta);
		stage.draw();
	}

	private void updatePlayer(float delta) {
		delta *= gm.getLevelSpeed();
		playerContrller.update(delta);

		if (!isGameOver) {
			// test for collision against enemies
			if (gm.testCollisions(playerContrller)) {
				gameOver();
			}
			gm.leash(playerContrller);
		}

		// update camera and background
		GameSprite player = playerContrller.getControlled();
		cameraVelocity.set(player.velocity).nor();
		cameraVelocity.scl(25).add(player.getOriginPosX(), player.getOriginPosY());
		cameraVelocity.x = ((cameraVelocity.x - camera.position.x) * 5 * delta);
		cameraVelocity.y = ((cameraVelocity.y - camera.position.y) * 5 * delta);
		camera.slide(cameraVelocity.x, cameraVelocity.y, gm.getBounds());
	}

	public void gameOver() {
		isGameOver = true;

		goa.setMesage("Game Over");
		goa.setVisible(true);
		goa.showResume(false);
		goa.showReset(true);
		goa.showMainMenuBtn(true);
		goa.showResetTiltBtn(false);
		goa.showSettingsBtn(false);
		goa.showImortalBtn(false);
		goa.pack();
		goa.setPosition(stage.getWidth() / 2 - goa.getWidth() / 2, 60);


		gm.recordScore(game.score);
		game.score.updateHighScores();
	}

	public void reset() {
		goa.setVisible(false);

		playerContrller.initialize(game.skn);
		playerContrller.setCamera(camera);
		playerContrller.health = PlayerController.STARTING_PLAYER_HEALTH;

		gm.reset(playerContrller.getControlled());

		game.score.currentKills = 0;

		isGameOver = false;
		isPaused = false;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		if(isGameOver){
			reset();
		}

		InputMultiplexer plexer = new InputMultiplexer();
		plexer.addProcessor(stage);
		plexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.BACKSPACE
						|| keycode == Input.Keys.BACK) {
					game.setScreen(MainMenuScreen.screenTag);
					return true;
				} else if (keycode == Input.Keys.MENU
						|| keycode == Input.Keys.ESCAPE) {
					if (!isGameOver) {
						if (!isPaused) {
							pause();
						} else {
							resume();
						}
					}
				}
				return false;
			}
		});

		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(plexer);
	}

	@Override
	public void hide() {
		pause();
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() {
		isPaused = true;


		goa.setPosition(0, 0);
		goa.setMesage("Paused");
		goa.showMainMenuBtn(true);
		goa.showReset(true);
		goa.showResume(true);
		goa.showResetTiltBtn(true);
		goa.showImortalBtn(false);
		goa.showSettingsBtn(true);
		goa.pack();
		goa.setPosition(stage.getWidth() / 2 - goa.getWidth() / 2, 60);
		goa.setVisible(true);
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
		shapes.dispose();
		immediate.dispose();
	}

}
