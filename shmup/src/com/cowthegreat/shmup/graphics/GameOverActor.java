package com.cowthegreat.shmup.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cowthegreat.shmup.SHMUP;

public class GameOverActor extends Table {

	public static class Listener {
		public void onResume() {
		}

		public void onReset() {
		}

		public void onMainMenu() {
		}

		public void onResetTilt() {
		}

		public void onHealMax() {
		}

		public void onShowSettings() {
		}
	}

	SHMUP game;
	Listener listener;
	boolean dirty;

	Label mesageLabel;
	TextButton resumeBtn, resetBtn, mainMenuBtn, resetTileBtn, imortalBtn,
			settingsBtn;

	public GameOverActor(SHMUP game) {
		// set up table
		this.game = game;
		 setBackground(game.skn.getDrawable("blue_bg"));

		// styles
		LabelStyle ls = game.skn.get("ui_label", LabelStyle.class);
		TextButtonStyle tbs = game.skn.get("ui_text_button",
				TextButtonStyle.class);

		// set up labels
		mesageLabel = new Label("No Mesage", ls);

		// set up buttons
		resumeBtn = new TextButton("Resume", tbs);
		resetBtn = new TextButton("Reset", tbs);
		mainMenuBtn = new TextButton("Main Menu", tbs);
		resetTileBtn = new TextButton("Reset Tilt", tbs);
		imortalBtn = new TextButton("Max Health", tbs);
		settingsBtn = new TextButton("Settings", tbs);

		// register listeners
		resumeBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (listener != null) {
					listener.onResume();
				}
			}
		});
		resetBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (listener != null) {
					listener.onReset();
				}
			}
		});
		mainMenuBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (listener != null) {
					listener.onMainMenu();
				}
			}
		});
		resetTileBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (listener != null) {
					listener.onResetTilt();
				}
			}
		});
		imortalBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (listener != null) {
					listener.onHealMax();
				}
			}
		});
		settingsBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (listener != null) {
					listener.onShowSettings();
				}
			}
		});

		dirty = true;
	}

	public void setMesage(String msg) {
		mesageLabel.setText(msg);
		dirty = true;
	}

	public void showMesage(boolean set) {
		mesageLabel.setVisible(set);
		dirty = true;
	}

	public void showResume(boolean set) {
		resumeBtn.setVisible(set);
		dirty = true;
	}

	public void showReset(boolean set) {
		resetBtn.setVisible(set);
		dirty = true;
	}

	public void showMainMenuBtn(boolean set) {
		mainMenuBtn.setVisible(set);
		dirty = true;
	}

	public void showResetTiltBtn(boolean set) {
		resetTileBtn.setVisible(set);
		dirty = true;
	}

	public void showSettingsBtn(boolean set) {
		settingsBtn.setVisible(set);
		dirty = true;
	}

	public void showImortalBtn(boolean set) {
		imortalBtn.setVisible(set);
		dirty = true;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (dirty) {
			pack();
		}
		super.draw(batch, parentAlpha);
	}

	@Override
	public void pack() {
		clearChildren();
		if (mesageLabel.isVisible()) {
			int count = 0;
			if (resumeBtn.isVisible())
				count++;
			if (resetBtn.isVisible())
				count++;
			if (mainMenuBtn.isVisible())
				count++;
			if (resetTileBtn.isVisible())
				count++;
			if (imortalBtn.isVisible())
				count++;
			if (settingsBtn.isVisible())
				count++;

			add(mesageLabel).colspan(count).center().padBottom(10);
			row();
		}

		if (resumeBtn.isVisible()) {
			add(resumeBtn);
		}
		if (resetBtn.isVisible()) {
			add(resetBtn);
		}
		if (mainMenuBtn.isVisible()) {
			add(mainMenuBtn);
		}
		if (resetTileBtn.isVisible()) {
			add(resetTileBtn);
		}
		if (imortalBtn.isVisible()) {
			add(imortalBtn);
		}
		if (settingsBtn.isVisible()) {
			add(settingsBtn);
		}
		super.pack();
		dirty = false;
	}

	public void setListener(Listener l) {
		listener = l;
	}
}
