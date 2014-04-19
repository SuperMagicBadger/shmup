package com.cowthegreat.shmup;

import java.text.Format;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.cowthegreat.shmup.controllers.PlayerController;

public class PCPlayerController extends PlayerController {

	@Override
	public void update(float delta) {
		float x = 0;
		float y = 0;
		if (!isDead()) {
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				setDash(Gdx.input.getX(), Gdx.input.getY());
			}

			// update left/right input
			if (Gdx.input.isKeyPressed(Keys.LEFT)
					|| Gdx.input.isKeyPressed(Keys.A)) {
				x = -1;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)
					|| Gdx.input.isKeyPressed(Keys.D)) {
				x = 1;
			}

			// update up/down input
			if (Gdx.input.isKeyPressed(Keys.UP)
					|| Gdx.input.isKeyPressed(Keys.W)) {
				y = 1;
			} else if (Gdx.input.isKeyPressed(Keys.DOWN)
					|| Gdx.input.isKeyPressed(Keys.S)) {
				y = -1;
			}
		}

		// finish the update
		updateMovement(delta, x, y);
	}
	
	@Override
	public void reset(){
		
	}

	@Override
	public void setMesage(Label l, Format f) {
		// TODO Auto-generated method stub
		
	}

}
