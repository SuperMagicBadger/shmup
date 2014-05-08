package com.cowthegreat.shmup.screens;

import java.text.Format;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.Scoreboard.Score;
import com.cowthegreat.shmup.graphics.GSImage;
import com.cowthegreat.shmup.graphics.GameSprite;
import com.cowthegreat.shmup.graphics.ParallaxBackground;

public class MainMenuScreen implements Screen {
	public static String screenTag = "main_menu_2";

	SHMUP game;

	SpriteBatch batch;
	Stage uiStage;
	ParallaxBackground bg;

	Table mainTable;
	Table aboutTable;
	Table highScoreTable;
	Table newGameTable;

	long themeID = -1;
	
	boolean drawDebug = false;
	Array<Table> dt = new Array<Table>();

	Format format;

	Array<GameSprite> gsList = new Array<GameSprite>();

	Pool<Action> revealBottomUpPool = new Pool<Action>() {
		protected Action newObject() {
			return new RevealBottomUp();
		}
	};
	Pool<Action> hideBottomDownPool = new Pool<Action>() {
		protected Action newObject() {
			return new HideBottomDown();
		}
	};
	Pool<Action> hideSlideHorizPool = new Pool<Action>() {
		@Override
		protected Action newObject() {
			return new HideSlideLeft();
		}
	};
	Pool<Action> revealSlideHorizPool = new Pool<Action>() {
		@Override
		protected Action newObject() {
			return new RevealSlideRight();
		}
	};
	Pool<Action> hideSlideVertPool = new Pool<Action>(){
		protected Action newObject() {
			return new HideSlideVert();
		};
	};
	Pool<Action> revealSlideVertPool = new Pool<Action>(){
		protected Action newObject() {
			return new RevealSlideVert();
		};
	};
	
	private class RevealBottomUp extends SequenceAction {
		
		@Override
		public void setActor(Actor actor) {
			super.setActor(actor);
			if (actor != null) {
				// setup the initial conditions
				ParallelAction initial = Actions.parallel();
				initial.addAction(Actions.moveTo(uiStage.getWidth() / 2
						- getActor().getWidth() / 2, uiStage.getHeight() / 2
						- getActor().getHeight() / 2));
				initial.addAction(Actions.alpha(0));
				initial.addAction(Actions.scaleTo(0.15f, 0.15f));

				// do the reveal
				ParallelAction action = new ParallelAction();
				action.addAction(Actions.alpha(1, 0.25f));
				action.addAction(Actions.scaleTo(1, 1, 1f));

				// compile everything
				addAction(initial);
				addAction(Actions.show());
				addAction(action);

				// set the pool to return to
				setPool(revealBottomUpPool);

			}
		}
	}

	private class HideBottomDown extends SequenceAction {

		@Override
		public void setActor(Actor actor) {
			super.setActor(actor);
			if (actor != null) {
				// setup the initial conditions
				ParallelAction initial = Actions.parallel();
				initial.addAction(Actions.moveTo(uiStage.getWidth() / 2
						- getActor().getWidth() / 2, uiStage.getHeight() / 2
						- getActor().getHeight() / 2));
				initial.addAction(Actions.alpha(1));
				initial.addAction(Actions.scaleTo(1, 1));

				// do the hide
				ParallelAction action = new ParallelAction();
				action.addAction(Actions.alpha(0, 0.25f));
				action.addAction(Actions.scaleTo(0.25f, 0.25f, 0.25f));

				// compile everything
				addAction(initial);
				addAction(action);
				addAction(Actions.hide());

				// set the pool to return to
				setPool(hideBottomDownPool);
			}
		}

	}

	private class RevealSlideRight extends SequenceAction {

		@Override
		public void setActor(Actor actor) {
			super.setActor(actor);
			if (actor != null) {
				// setup the initial conditions
				ParallelAction initial = Actions.parallel();
				initial.addAction(Actions.moveTo(uiStage.getWidth() / 2
						- getActor().getWidth() / 2 - uiStage.getWidth(),
						uiStage.getHeight() / 2 - getActor().getHeight() / 2));
				initial.addAction(Actions.alpha(1));
				initial.addAction(Actions.scaleTo(1, 1));

				// do the hide
				ParallelAction action = new ParallelAction();
				action.addAction(Actions.moveBy(uiStage.getWidth(), 0, 0.25f));

				// compile everything
				addAction(initial);
				addAction(Actions.show());
				addAction(action);

				// set the pool to return to
				setPool(revealSlideHorizPool);
			}
		}
	}

	private class HideSlideLeft extends SequenceAction {

		@Override
		public void setActor(Actor actor) {
			super.setActor(actor);
			if (actor != null) {
				// setup the initial conditions
				ParallelAction initial = Actions.parallel();
				initial.addAction(Actions.moveTo(uiStage.getWidth() / 2
						- getActor().getWidth() / 2, uiStage.getHeight() / 2
						- getActor().getHeight() / 2));
				initial.addAction(Actions.alpha(1));
				initial.addAction(Actions.scaleTo(1, 1));

				// do the hide
				ParallelAction action = new ParallelAction();
				action.addAction(Actions.moveBy(-uiStage.getWidth(), 0, 0.25f));

				// compile everything
				addAction(initial);
				addAction(action);
				addAction(Actions.hide());

				// set the pool to return to
				setPool(hideSlideHorizPool);
			}
		}
	}

	private class HideSlideVert extends SequenceAction {

		@Override
		public void setActor(Actor actor) {
			super.setActor(actor);
			if (actor != null) {
				// setup the initial conditions
				ParallelAction initial = Actions.parallel();
				initial.addAction(Actions.moveTo(uiStage.getWidth() / 2
						- getActor().getWidth() / 2, uiStage.getHeight() / 2
						- getActor().getHeight() / 2));
				initial.addAction(Actions.alpha(1));
				initial.addAction(Actions.scaleTo(1, 1));

				// do the hide
				ParallelAction action = new ParallelAction();
				action.addAction(Actions.moveBy(-uiStage.getWidth(), 0, 0.25f));

				// compile everything
				addAction(initial);
				addAction(action);
				addAction(Actions.hide());

				// set the pool to return to
				setPool(hideSlideHorizPool);
			}
		}
	}
	
	private class RevealSlideVert extends SequenceAction {
		
		@Override
		public void setActor(Actor actor) {
			super.setActor(actor);
			if (actor != null) {
				// setup the initial conditions
				ParallelAction initial = Actions.parallel();
				initial.addAction(Actions.moveTo(uiStage.getWidth() / 2
						- getActor().getWidth() / 2 - uiStage.getWidth(),
						uiStage.getHeight() / 2 - getActor().getHeight() / 2));
				initial.addAction(Actions.alpha(1));
				initial.addAction(Actions.scaleTo(1, 1));

				// do the hide
				ParallelAction action = new ParallelAction();
				action.addAction(Actions.moveBy(uiStage.getWidth(), 0, 0.25f));

				// compile everything
				addAction(initial);
				addAction(Actions.show());
				addAction(action);

				// set the pool to return to
				setPool(revealSlideHorizPool);
			}
		}
	}
	
	// --------------------------------
	// CONSTRUCTOR --------------------
	// ================================

	public MainMenuScreen(SHMUP shmupgame) {
		game = shmupgame;
		uiStage = new Stage();
		uiStage.setViewport(game.gameWidth, game.gameHeight);
		mainTable = generateMainMenuTable();
		aboutTable = generateAboutTable();
		highScoreTable = generateHighScoreTable();
		newGameTable = generateNewGameTable();
		uiStage.addActor(mainTable);
		uiStage.addActor(aboutTable);
		uiStage.addActor(highScoreTable);
		uiStage.addActor(newGameTable);

		batch = uiStage.getSpriteBatch();

		bg = new ParallaxBackground(uiStage.getCamera());
		bg.addLayer(game.skn.getRegion("biggrid"), 2f, -0.25f);
		bg.addLayer(game.skn.getRegion("littlegrid"), 2f, 2f);
		bg.addLayer(game.skn.getRegion("bluestars"), 2f, 0f);
	}

	// --------------------------------
	// RENDERING ----------------------
	// ================================

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		for (GameSprite gs : gsList) {
			gs.update(delta);
		}

		bg.slide(delta * 20, delta * 20);

		Color c = batch.getColor();
		float oa = c.a;
		c.a = 1;
		batch.setColor(c);
		batch.begin();
		bg.draw(batch);
		batch.end();
		c.a = oa;
		batch.setColor(c);

		uiStage.act(delta);
		uiStage.draw();
		
		if (drawDebug) {
			mainTable.debug();
			aboutTable.debug();
			highScoreTable.debug();
			for (Table t : dt) {
				t.debug();
			}
			Table.drawDebug(uiStage);
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		resetHighScores();
		
		// show main table
		mainTable.setPosition(
				uiStage.getWidth() / 2 - mainTable.getWidth() / 2,
				uiStage.getHeight() / 2 - mainTable.getHeight() / 2);
		mainTable.setVisible(true);

		// hide others
		aboutTable.setVisible(false);
		highScoreTable.setVisible(false);
		newGameTable.setVisible(false);

		themeID = SHMUP.theme.loop();
		
		Gdx.input.setInputProcessor(uiStage);
	}

	@Override
	public void hide() {
		SHMUP.theme.stop(themeID);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		uiStage.dispose();
		revealBottomUpPool.clear();
		hideBottomDownPool.clear();
		revealSlideHorizPool.clear();
		hideSlideHorizPool.clear();
		revealBottomUpPool.clear();
	}

	public void newGame() {
		game.setScreen(GameScreen.screenTag);
	}

	public void resetHighScores() {
		LabelStyle lStyle = game.skn.get("title_label", LabelStyle.class);
		Table t = (Table) highScoreTable.getChildren().get(0);
		t.clear();

		for (Score s : game.score.highScores) {
			Label l = new Label(" Wave: " + s.wave + " Score: " + s.scoreValue
					+ " Time: " + s.time, lStyle);
			t.add(l).row();
		}
	}

	// ------------------------------------------
	// GENERATE TABLES --------------------------
	// ==========================================

	private Table generateMainMenuTable() {

		// STYLES
		LabelStyle lStyle = game.skn.get("title_label", LabelStyle.class);
		lStyle.fontColor = Color.WHITE;
		TextButtonStyle tbStyle = game.skn.get("ui_text_button",
				TextButtonStyle.class);

		// BUILD THE TABLE
		Table table = new Table();
		Table menuTable = new Table();
		Label title = new Label("Vector\nDash", lStyle);
		TextButton about = new TextButton("instructions", tbStyle);
		TextButton newGame = new TextButton("new game", tbStyle);
		TextButton settings = new TextButton("settings", tbStyle);
		TextButton highScores = new TextButton("high scores", tbStyle);

		menuTable.addActor(title);
		title.setPosition(50, uiStage.getHeight() * 2f / 3f);
		menuTable.add(about).fill().expandX().pad(0, 0, 10, 0).row();
		menuTable.add(newGame).fill().expandX().pad(0, 0, 10, 0).row();
		menuTable.add(settings).fill().expandX().pad(0, 0, 10, 0).row();
		menuTable.add(highScores).fill().expandX();
		menuTable.pack();

		// MAKE BUTTONS DO THINGS
		newGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				newGameTable.addAction(revealBottomUpPool.obtain());
				mainTable.addAction(hideSlideHorizPool.obtain());
			}
		});

		about.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				aboutTable.addAction(revealBottomUpPool.obtain());
				mainTable.addAction(hideSlideHorizPool.obtain());
			}
		});

		highScores.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				resetHighScores();
				highScoreTable.addAction(revealBottomUpPool.obtain());
				mainTable.addAction(hideSlideHorizPool.obtain());
			}
		});

		settings.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(SettingsScreen.screenTag, screenTag);
			}
		});

		table.addActor(title);
		title.setPosition(50, uiStage.getHeight() * 2f / 3f);
		table.addActor(menuTable);
		menuTable.setPosition(uiStage.getWidth() - menuTable.getWidth() - 50,
				40);
		table.setWidth(uiStage.getWidth());
		table.setHeight(uiStage.getHeight());

		dt.add(menuTable);

		return table;
	}
	
	private Table generateNewGameTable(){
		Table t = new Table();
		
		// STYLES
		LabelStyle lStyle = game.skn.get("title_label", LabelStyle.class);
		lStyle.fontColor = Color.WHITE;
		TextButtonStyle tbStyle = game.skn.get("ui_text_button",
				TextButtonStyle.class);
		
		// BUILD THE COMPONENTS
		Label instrLabel = new Label("Select Tilt", lStyle);
		ImageButton angleButton = new ImageButton(game.skn.getDrawable("45tilt"));
		ImageButton flatButton = new ImageButton(game.skn.getDrawable("0tilt"));
		ImageButton customButton = new ImageButton(game.skn.getDrawable("ctilt"));
		TextButton backBtn = new TextButton("back", tbStyle);
		
		// MAKE BUTTONS DO THINGS
		angleButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.playerControls.setAngle(SHMUP.x45, SHMUP.y45, SHMUP.z45);
				newGame();
			}
		});
		flatButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.playerControls.setAngle(SHMUP.x0, SHMUP.y0, SHMUP.z0);
				newGame();
			}
		});
		customButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.playerControls.reset();
				newGame();
			}
		});
		backBtn.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				mainTable.addAction(revealSlideHorizPool.obtain());
				newGameTable.addAction(hideBottomDownPool.obtain());
			}
		});
		
		// BUILD THE TABLE
		t.add(instrLabel).colspan(3).center().row();
		t.add(angleButton).pad(10);
		t.add(flatButton).pad(10);
		t.add(customButton).pad(10).row();
		t.add(backBtn).colspan(3).center();
		
		return t;
	}

	private Table generateHighScoreTable() {
		Table table = new Table();

		// STYLES
		LabelStyle lStyle = game.skn.get("title_label", LabelStyle.class);
		lStyle.fontColor = Color.WHITE;
		TextButtonStyle tbStyle = game.skn.get("ui_text_button",
				TextButtonStyle.class);

		// BUILD THE TABLE
		Table scoreTable = new Table();

		TextButton tBtn = new TextButton("back", tbStyle);
		tBtn.setPosition(uiStage.getWidth() / 2 - tBtn.getWidth() / 2,
				15 - uiStage.getHeight());

		// MAKE BUTTONS DO STUFF
		tBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				highScoreTable.addAction(hideBottomDownPool.obtain());
				mainTable.addAction(revealSlideHorizPool.obtain());
			}
		});

		table.add(scoreTable).padBottom(10).row();
		table.add(tBtn);

		dt.add(scoreTable);

		return table;
	}

	private Table generateAboutTable() {
		Table table = new Table();

		// STYLES
		LabelStyle lStyle = game.skn.get("title_label", LabelStyle.class);
		lStyle.fontColor = Color.WHITE;
		TextButtonStyle tbStyle = game.skn.get("ui_text_button",
				TextButtonStyle.class);

		// BUiLD THE SPRITES
		GameSprite gsPlayer = new GameSprite(game.skn.getRegion("player"));
//		GameSprite gsBro = new GameSprite(game.skn.getRegion("bro"));
//		GameSprite gsDashBro = new GameSprite(game.skn.getRegion("dash_bro"));
//		GameSprite gsDashBroGlow = new GameSprite(new Animation(0.1f, game.skn
//				.getAtlas().findRegions("dash_bro_center"),
//				Animation.LOOP_PINGPONG));
//		GameSprite gsShieldBro = new GameSprite(
//				game.skn.getRegion("shield_bro"));
//		GameSprite gsShieldBroShield = new GameSprite(new Animation(0.2f,
//				game.skn.getAtlas().findRegions("shield_bro_shield_anim"),
//				Animation.LOOP_PINGPONG));
//		GameSprite gsSplodeBro = new GameSprite(
//				game.skn.getRegion("sploode_bro"));
//		;
//		GameSprite gsSplodeBroGlow = new GameSprite(new Animation(0.1f,
//				game.skn.getAtlas().findRegions("splode_bro_center"),
//				Animation.LOOP_PINGPONG));

		gsPlayer.rotationalVelocity = 90;
//		gsBro.rotationalVelocity = 90;
//		gsDashBro.rotationalVelocity = 90;
//		gsShieldBro.rotationalVelocity = 90;
//		gsSplodeBro.rotationalVelocity = 90;
//
//		gsDashBro.addChild(gsDashBroGlow);
//		gsShieldBro.addChild(gsShieldBroShield);
//		gsSplodeBro.addChild(gsSplodeBroGlow);

		gsList.add(gsPlayer);
//		gsList.add(gsBro);
//		gsList.add(gsDashBro);
//		gsList.add(gsShieldBro);
//		gsList.add(gsSplodeBro);

		// MAKE THE STRINGS
		String playerDesc = "tilt the screen to move. Tap\n"
				+ "to dash. Dashing through\n" + "enemies destorys them.";

		String spawnDesc = "As you destroy enemies the spawn rate increases\n"
				+ "and more enemy types are introduced.";

		// BUILD THE TABLE COMPONENTS
		Table abt = new Table(game.skn);

		GSImage imgPlayer = new GSImage(gsPlayer);
		// GSImage imgIdiotBro = new GSImage(gsBro);
		// GSImage imgDashBro = new GSImage(gsDashBro);
		// GSImage imgShieldBro = new GSImage(gsShieldBro);
		// GSImage imgSplodeBro = new GSImage(gsSplodeBro);

		TextButton backBtn = new TextButton("back", tbStyle);
		backBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				aboutTable.addAction(hideBottomDownPool.obtain());
				mainTable.addAction(revealSlideHorizPool.obtain());
			}
		});

		// ASSEMBLE THE TABLE
		abt.defaults().center().pad(20);
		abt.add(imgPlayer).minSize(imgPlayer.getWidth(), imgPlayer.getHeight());
		abt.add(playerDesc, "title_label").row();
		abt.add(spawnDesc, "title_label").colspan(2).row();

		// abt.add(imgIdiotBro).minSize(imgIdiotBro.getWidth(),
		// imgIdiotBro.getHeight()).row();
		// abt.add(imgDashBro).minSize(imgDashBro.getWidth(),
		// imgDashBro.getHeight()).row();
		// abt.add(imgShieldBro).minSize(imgShieldBro.getWidth(),
		// imgShieldBro.getHeight()).row();
		// abt.add(imgSplodeBro).minSize(imgSplodeBro.getWidth(),
		// imgSplodeBro.getHeight()).row();

		table.add(abt).padBottom(50f).top().row();
		table.add(backBtn).bottom();

		table.setWidth(uiStage.getWidth() * 2f / 3f);
		table.setHeight(uiStage.getHeight() * 2f / 3f);

		dt.add(abt);

		return table;
	}

}
