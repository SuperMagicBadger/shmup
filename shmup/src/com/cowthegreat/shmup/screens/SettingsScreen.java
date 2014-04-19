package com.cowthegreat.shmup.screens;

import java.text.DecimalFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.graphics.ParallaxBackground;

public class SettingsScreen implements Screen {
	public static final String screenTag = "settings_screen";
	private static String dtt = "Dashing Towards  Touch";
	private static String dtf = "Dashing Towards Facing";
	private static String hhb = "Hiding  Hit Boxes";
	private static String shb = "Showing Hit Boxes";

	DecimalFormat format;
	
	SHMUP shmupgame;
	Stage uiStage;

	ParallaxBackground bg;

	ScrollPane scrollPane;
	Table optionsTable;

	Label sensitivityXLabel, sensitivityValueXLabel;
	Slider sensitivityXSlider;

	Label sensitivityYLabel, sensitivityValueYLabel;
	Slider sensitivityYSlider;

//	Label smoothLabel, smoothValueLabel;
//	Slider smoothSlider;

	TextButton controlStyle, showHitbox, resetTilt, clearHighScores;
	TextButton backButton;

	public SettingsScreen(SHMUP game) {
		shmupgame = game;
		uiStage = new Stage(game.gameWidth, game.gameHeight);		
		format = new DecimalFormat("#.##");
		format.setMinimumIntegerDigits(1);
		format.setMaximumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);

		ScrollPaneStyle spStyle = new ScrollPaneStyle();
		spStyle.vScrollKnob = shmupgame.skn.getDrawable("square_knob");

		SliderStyle sstyle = new SliderStyle();
		sstyle.background = shmupgame.skn.getDrawable("slider_bar");
		sstyle.knob = shmupgame.skn.getDrawable("slider_knob");

		TextButtonStyle tbstyle = shmupgame.skn.get("ui_text_button",
				TextButtonStyle.class);

		LabelStyle lStyle = shmupgame.skn.get("ui_white_label",
				LabelStyle.class);

		sensitivityXLabel = new Label("Tilt Sensitivity:   - ", lStyle);
		sensitivityValueXLabel = new Label(" + (5)", lStyle);

		sensitivityXSlider = new Slider(1, 50, 1, false, sstyle);
		sensitivityXSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				shmupgame.settings.sensitivityX = sensitivityXSlider.getValue();
				sensitivityValueXLabel.setText(" +  ("
						+ ((int) shmupgame.settings.sensitivityX) + ")");
			}
		});

		sensitivityYLabel = new Label("Tilt Y Sensitivity:   - ", lStyle);
		sensitivityValueYLabel = new Label(" + (0)", lStyle);

		sensitivityYSlider = new Slider(1, 50, 1, false, sstyle);
		sensitivityYSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				shmupgame.settings.sensitivityY = sensitivityYSlider.getValue();
				sensitivityValueYLabel.setText(" +  ("
						+ ((int) shmupgame.settings.sensitivityY) + ")");
			}
		});

//		smoothLabel = new Label("Input Smoothing:   + ", lStyle);
//		smoothValueLabel = new Label(" - 5", lStyle);
//
//		smoothSlider = new Slider(0.1f, 0.9f, 0.05f, false, sstyle);
//		smoothSlider.addListener(new ChangeListener() {
//			@Override
//			public void changed(ChangeEvent event, Actor actor) {
//				shmupgame.settings.smoothing = smoothSlider.getValue();
//				smoothValueLabel.setText(" -  ("
//						+ (format.format(shmupgame.settings.smoothing)) + ")");
//			}
//		});

		controlStyle = new TextButton("Dashing Towards Touch", tbstyle);
		controlStyle.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				shmupgame.settings.dashTowardTap = !shmupgame.settings.dashTowardTap;
				if (shmupgame.settings.dashTowardTap) {
					controlStyle.setText(dtt);
				} else {
					controlStyle.setText(dtf);
				}
			}
		});

		showHitbox = new TextButton("Hiding Hit Boxes", tbstyle);
		showHitbox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				shmupgame.settings.drawHitboxes = !shmupgame.settings.drawHitboxes;
				if (shmupgame.settings.drawHitboxes) {
					showHitbox.setText(shb);
				} else {
					showHitbox.setText(hhb);
				}
			}
		});

		resetTilt = new TextButton("Reset Tilt", tbstyle);
		resetTilt.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				shmupgame.playerControls.reset();
			}
		});

		backButton = new TextButton("back", tbstyle);
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				shmupgame.back();
			}
		});
		backButton.setPosition(uiStage.getWidth() / 2 - backButton.getWidth()
				/ 2, 10);

		optionsTable = new Table();
		optionsTable.add(sensitivityXLabel).padBottom(20);
		optionsTable.add(sensitivityXSlider).padBottom(20);
		optionsTable.add(sensitivityValueXLabel).padBottom(20).row();
		optionsTable.add(sensitivityYLabel).padBottom(20);
		optionsTable.add(sensitivityYSlider).padBottom(20);
		optionsTable.add(sensitivityValueYLabel).padBottom(20).row();
//		optionsTable.add(smoothLabel).padBottom(20);
//		optionsTable.add(smoothSlider).padBottom(20);
//		optionsTable.add(smoothValueLabel).padBottom(20).row();
		optionsTable.add(resetTilt).colspan(3).fill().fillX().padBottom(10)
				.row();
		optionsTable.add(showHitbox).colspan(3).fill().fillX();
		optionsTable.pack();
		optionsTable.setPosition(
				uiStage.getWidth() / 2 - optionsTable.getWidth() / 2,
				uiStage.getHeight() / 2 - optionsTable.getHeight() / 2 + 20);

		uiStage.addActor(backButton);
		uiStage.addActor(optionsTable);

		bg = new ParallaxBackground(uiStage.getCamera());
		bg.addLayer(game.skn.getRegion("biggrid"), 2f, -0.25f);
		bg.addLayer(game.skn.getRegion("littlegrid"), 2f, 2f);
		bg.addLayer(game.skn.getRegion("bluestars"), 2f, 0f);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		bg.slide(delta * 20, delta * 20);
		uiStage.getSpriteBatch().begin();
		bg.draw(uiStage.getSpriteBatch());
		uiStage.getSpriteBatch().end();
		uiStage.act(delta);
		uiStage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		if (shmupgame.settings.dashTowardTap) {
			controlStyle.setText(dtt);
		} else {
			controlStyle.setText(dtf);
		}
		if (shmupgame.settings.drawHitboxes) {
			showHitbox.setText(shb);
		} else {
			showHitbox.setText(hhb);
		}
		sensitivityXSlider.setValue(shmupgame.settings.sensitivityX);
		sensitivityValueXLabel.setText(" +  ("
				+ ((int) shmupgame.settings.sensitivityX) + ")");

		sensitivityYSlider.setValue(shmupgame.settings.sensitivityY);
		sensitivityValueYLabel.setText(" +  ("
				+ ((int) shmupgame.settings.sensitivityY) + ")");

//		smoothSlider.setValue(shmupgame.settings.smoothing);
//		smoothValueLabel.setText(" -  ("
//				+ (format.format(shmupgame.settings.smoothing)) + ")");

		Gdx.input.setInputProcessor(uiStage);
	}

	@Override
	public void hide() {
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
	}

}
