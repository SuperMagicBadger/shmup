package com.cowthegreat.shmup.graphics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;
import com.cowthegreat.shmup.controllers.EnemyController;
import com.cowthegreat.shmup.controllers.PlayerController;

public class Radar {
	private static final float SCALING_FACTOR = 1.75f;
	
	float width, height;

	public Radar(SHMUP g) {
		width = g.gameWidth;
		height = g.gameHeight;
	}

	public void draw(SpriteBatch batch, Camera cam,
			ArrayList<EnemyController> activeUnits) {
		Vector2 workingVec = SHMUP.vector_pool.obtain();

		for (EnemyController uc : activeUnits) {
			TextureRegion region = uc.radarMarker();
			
			workingVec.set(uc.getControlled().getOriginPosX(), uc
					.getControlled().getOriginPosY());
			workingVec.sub(cam.position.x, cam.position.y);
				float scale = SCALING_FACTOR * PlayerController.dashDistance / workingVec.len();
			
			float diagSlope = (cam.viewportHeight * (0.5f))
					/ (cam.viewportWidth * (0.5f));
			float lineSlope = (workingVec.y) / (workingVec.x);

			boolean draw = false;
			if (Math.abs(lineSlope) > diagSlope) {
				if (workingVec.y > cam.viewportHeight / 2f) {
					workingVec.x = (1 / lineSlope) * (cam.viewportHeight / 2f);
					workingVec.y = cam.viewportHeight / 2f - region.getRegionHeight() * scale;
					draw = true;
				} else if(workingVec.y < cam.viewportHeight / -2f){
					workingVec.x = (1 / lineSlope) * -(cam.viewportHeight / 2f);
					workingVec.y = -cam.viewportHeight / 2f; 
					draw = true;
				}
			} else {
				if (workingVec.x > cam.viewportWidth / 2f) {
					workingVec.x = cam.viewportWidth / 2f - region.getRegionWidth() * scale;
					workingVec.y = lineSlope * (cam.viewportWidth / 2f);
					draw = true;
				} else if (workingVec.x < cam.viewportWidth / -2f) {
					workingVec.x = -cam.viewportWidth / 2f;
					workingVec.y = lineSlope * -(cam.viewportWidth / 2f);
					draw = true;
				}
			}
			workingVec.add(cam.position.x, cam.position.y);
			if(draw){
				batch.draw(region, workingVec.x, workingVec.y, region.getRegionWidth() * scale, region.getRegionHeight() * scale);
			}
		}
		SHMUP.vector_pool.free(workingVec);
	}

	public void draw(ShapeRenderer shapes, Camera cam,
			ArrayList<EnemyController> activeUnits) {
		Color old = shapes.getColor();
		Vector2 workingVec = SHMUP.vector_pool.obtain();

		for (EnemyController uc : activeUnits) {
			workingVec.set(uc.getControlled().getOriginPosX(), uc
					.getControlled().getOriginPosY());
			workingVec.sub(cam.position.x, cam.position.y);

			float diagSlope = (cam.viewportHeight * (0.5f))
					/ (cam.viewportWidth * (0.5f));
			float lineSlope = (workingVec.y) / (workingVec.x);

			boolean draw = false;
			if (Math.abs(lineSlope) > diagSlope) {
				if (workingVec.y > cam.viewportHeight / 2f) {
					shapes.setColor(Color.RED);
					workingVec.x = (1 / lineSlope) * (cam.viewportHeight / 2f);
					workingVec.y = cam.viewportHeight / 2f;
					draw = true;
				} else if(workingVec.y < cam.viewportHeight / -2f){
					workingVec.x = (1 / lineSlope) * -(cam.viewportHeight / 2f);
					workingVec.y = -cam.viewportHeight / 2f; 
					draw = true;
					shapes.setColor(Color.BLUE);
				}
			} else {
				if (workingVec.x > cam.viewportWidth / 2f) {
					shapes.setColor(Color.GREEN);
					workingVec.x = cam.viewportWidth / 2f;
					workingVec.y = lineSlope * (cam.viewportWidth / 2f);
					draw = true;
					shapes.setColor(Color.GREEN);
				} else if (workingVec.x < cam.viewportWidth / -2f) {
					shapes.setColor(Color.YELLOW);
					workingVec.x = -cam.viewportWidth / 2f;
					workingVec.y = lineSlope * -(cam.viewportWidth / 2f);
					draw = true;
				}
			}
			workingVec.add(cam.position.x, cam.position.y);
			if(draw){
				shapes.line(cam.position.x, cam.position.y, workingVec.x,
						workingVec.y);
				shapes.circle(workingVec.x, workingVec.y, 100);
			}
		}
		SHMUP.vector_pool.free(workingVec);
		shapes.setColor(old);
	}

}
