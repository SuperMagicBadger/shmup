package com.cowthegreat.shmup.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.Scoreboard.Score;
import com.cowthegreat.shmup.graphics.ParallaxBackground;

public class MainMenuScreen implements Screen {

	public static final String screenTag = "menu_scr";
	Stage uiStage;
	SHMUP game;

	boolean doBack = false;
	ParallaxBackground bg;

	Table scoreTable;
	Table aboutTabelOne;
	Table aboutTableTwo;
	Table aboutShieldTable;
	Table aboutSplodeTable;
	
	ImageButton roundLevel, rectLevel;
	TextButton ngBackBtn;

	public MainMenuScreen(SHMUP game) {
		// INIT VARIABLES
		this.game = game;
		uiStage = new Stage();
		uiStage.setViewport(game.gameWidth, game.gameHeight);

		// SET UP THE BACKGROUND
		bg = new ParallaxBackground(uiStage.getCamera());
		bg.addLayer(game.skn.getRegion("biggrid"), 2f, -0.25f);
		bg.addLayer(game.skn.getRegion("littlegrid"), 2f, 2f);
		bg.addLayer(game.skn.getRegion("bluestars"), 2f, 0f);

		// LOAD STYLES
		LabelStyle lStyle = game.skn.get("title_label", LabelStyle.class);
		lStyle.fontColor = Color.WHITE;
		TextButtonStyle tbStyle = game.skn.get("ui_text_button",
				TextButtonStyle.class);

		// INITIALIZE MAIN MENU
		Table menuTable = new Table();
		Label title = new Label("Vector\nDash", lStyle);
		TextButton about = new TextButton("instructions", tbStyle);
		TextButton newGame = new TextButton("new game", tbStyle);
		TextButton settings = new TextButton("settings", tbStyle);
		TextButton highScores = new TextButton("high scores", tbStyle);

		title.setPosition(50, uiStage.getHeight() * 2f / 3f);
		menuTable.add(about).fill().expandX().pad(0, 0, 10, 0).row();
		menuTable.add(newGame).fill().expandX().pad(0, 0, 10, 0).row();
		menuTable.add(settings).fill().expandX().pad(0, 0, 10, 0).row();
		menuTable.add(highScores).fill().expandX();
		menuTable.pack();
		menuTable.setPosition(uiStage.getWidth() - menuTable.getWidth() - 50,
				40);

		newGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				newGame();
			}
		});

		about.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MoveToAction action = new MoveToAction();
				action.setPosition(0, -uiStage.getHeight());
				action.setDuration(0.5f);
				action.setInterpolation(Interpolation.linear);
				uiStage.addAction(action);
				doBack = true;
				Gdx.input.setCatchBackKey(true);
			}
		});

		highScores.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				scoreTable.clear();
				for (Score s : MainMenuScreen.this.game.score.highScores) {
					Label l = new Label(s.toString(),
							MainMenuScreen.this.game.skn, "font_25",
							Color.WHITE);
					scoreTable.add(l).align(Align.center).row();
				}
				scoreTable.pack();
				scoreTable.setPosition(
						uiStage.getWidth() / 2 - scoreTable.getWidth() / 2, -15
								- scoreTable.getHeight());

				MoveToAction action = new MoveToAction();
				action.setPosition(0, uiStage.getHeight());
				action.setDuration(0.5f);
				action.setInterpolation(Interpolation.linear);
				uiStage.addAction(action);
				doBack = true;
				Gdx.input.setCatchBackKey(true);
			}
		});
		
		settings.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y) {
				MainMenuScreen.this.game.setScreen(SettingsScreen.screenTag, screenTag);
			}
		});

		uiStage.addActor(title);
		uiStage.addActor(menuTable);

		// INITIALIZE ABOUT SCREENS

		// images and text for page one
		aboutTabelOne = new Table();

		Image playerImage = new Image(game.skn.getDrawable("player"));
		Label playerDesc = new Label(
				"tilt the screen to move.\ntap to dash through enemies.",
				lStyle);

		Image dashBroImage = new Image(game.skn.getDrawable("dash_bro"));
		Label dashBroDesc = new Label(
				"Dash-Bros mindlessly follow\nthe player and dash\nwhen in range",
				lStyle);

		aboutTabelOne.add(playerImage);
		aboutTabelOne.add(playerDesc).padBottom(50);
		aboutTabelOne.row();
		aboutTabelOne.add(dashBroImage);
		aboutTabelOne.add(dashBroDesc);
		aboutTabelOne.pack();

		aboutTabelOne.setPosition(
				uiStage.getWidth() / 2 - aboutTabelOne.getWidth() / 2 - 25,
				uiStage.getHeight() + uiStage.getHeight() / 2
						- aboutTabelOne.getHeight() / 2);

		// images and text for page two
		aboutTableTwo = new Table();

		Image shieldBroImage = new Image(game.skn.getDrawable("shield_bro"));
		Label shieldBroDesc = new Label(
				"Shield-Bros keep their distance\n and shield allies in range",
				lStyle);

		Image splodeBroImage = new Image(game.skn.getDrawable("sploode_bro"));
		Label splodeBroDesc = new Label(
				"Splode-Bros follow the player and\nexplode, destroying everything\nin range",
				lStyle);

		aboutTableTwo.add(shieldBroImage);
		aboutTableTwo.add(shieldBroDesc).padBottom(50);
		aboutTableTwo.row();
		aboutTableTwo.add(splodeBroImage);
		aboutTableTwo.add(splodeBroDesc);
		aboutTableTwo.pack();

		aboutTableTwo.setPosition(
				uiStage.getWidth() / 2 - aboutTableTwo.getWidth() / 2
						+ uiStage.getWidth() + 25, uiStage.getHeight()
						+ uiStage.getHeight() / 2 - aboutTableTwo.getHeight()
						/ 2);

		TextButton backButton = new TextButton("back", tbStyle);
		backButton.setPosition(uiStage.getWidth() / 2 - backButton.getWidth()
				/ 2, uiStage.getHeight() + 15);

		// back button
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MoveToAction action = new MoveToAction();
				action.setPosition(0, 0);
				action.setDuration(0.5f);
				action.setInterpolation(Interpolation.linear);
				uiStage.addAction(action);
				Gdx.input.setCatchBackKey(false);
			}
		});

		// about scroll buttons
		final TextButton leftBtn = new TextButton("<", tbStyle);
		final TextButton rightBtn = new TextButton(">", tbStyle);

		leftBtn.setPosition(10, uiStage.getHeight() + uiStage.getHeight() / 2);
		rightBtn.setPosition(uiStage.getWidth() - rightBtn.getWidth() - 10,
				uiStage.getHeight() + uiStage.getHeight() / 2);

		leftBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MoveByAction a = new MoveByAction();
				a.setAmount(uiStage.getWidth(), 0);
				a.setDuration(0.25f);
				a.setInterpolation(Interpolation.linear);
				aboutTabelOne.addAction(a);
				a = new MoveByAction();
				a.setAmount(uiStage.getWidth(), 0);
				a.setDuration(0.25f);
				a.setInterpolation(Interpolation.linear);
				aboutTableTwo.addAction(a);
				rightBtn.setVisible(true);
				leftBtn.setVisible(false);
			}
		});
		rightBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MoveByAction a = new MoveByAction();
				a.setAmount(-uiStage.getWidth(), 0);
				a.setDuration(0.25f);
				a.setInterpolation(Interpolation.linear);
				aboutTabelOne.addAction(a);
				a = new MoveByAction();
				a.setAmount(-uiStage.getWidth(), 0);
				a.setDuration(0.25f);
				a.setInterpolation(Interpolation.linear);
				aboutTableTwo.addAction(a);
				rightBtn.setVisible(false);
				leftBtn.setVisible(true);
			}
		});

		leftBtn.setVisible(false);

		uiStage.addActor(aboutTabelOne);
		uiStage.addActor(aboutTableTwo);

		uiStage.addActor(backButton);
		uiStage.addActor(leftBtn);
		uiStage.addActor(rightBtn);

		// SET UP HIGH SCORE SETUP
		scoreTable = new Table();
		TextButton highscoreBackButton = new TextButton("Back", tbStyle);

		highscoreBackButton.setPosition(uiStage.getWidth() / 2
				- highscoreBackButton.getWidth() / 2, 15 - uiStage.getHeight());

		highscoreBackButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MoveToAction action = new MoveToAction();
				action.setPosition(0, 0);
				action.setDuration(0.5f);
				action.setInterpolation(Interpolation.linear);
				uiStage.addAction(action);
				Gdx.input.setCatchBackKey(false);
			}
		});
		
		// NEW GAME LEVEL SELECTION
		
		
//		ngBackBtn = new TextButton("back", tbStyle);
//		ngBackBtn.setPosition(uiStage.getWidth() / 2 + ngBackBtn.getWidth() / 2, ngBackBtn.getHeight() + 50);
//		ngBackBtn.addListener(new ClickListener(){
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				ngBack();
//			}
//		});
//		

		uiStage.addActor(scoreTable);
		uiStage.addActor(highscoreBackButton);

	}

	public void newGame(){
		game.setScreen(GameScreen.screenTag);
	}
	
//	public void ngBack(){
//		
//	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		bg.slide(delta * 20, delta * 20);

		uiStage.act(delta);
		uiStage.getSpriteBatch().begin();
		bg.draw(uiStage.getSpriteBatch());
		uiStage.getSpriteBatch().end();
		uiStage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		InputMultiplexer plexer = new InputMultiplexer();
		// SET UP BACK KEY
		plexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if ((keycode == Keys.BACK || keycode == Keys.ESCAPE) && doBack) {
					MoveToAction action = new MoveToAction();
					action.setPosition(0, 0);
					action.setDuration(0.5f);
					action.setInterpolation(Interpolation.linear);
					uiStage.addAction(action);
					Gdx.input.setCatchBackKey(false);
					doBack = false;
					return true;
				}
				return false;
			}
		});
		plexer.addProcessor(uiStage);
		Gdx.input.setInputProcessor(plexer);
		SHMUP.theme.loop();
	}

	@Override
	public void hide() {
		SHMUP.theme.stop();
	}

	@Override
	public void pause() {
		SHMUP.theme.pause();
	}

	@Override
	public void resume() {
		SHMUP.theme.resume();
	}

	@Override
	public void dispose() {
		uiStage.dispose();
	}

}
