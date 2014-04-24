package com.cowthegreat.shmup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Settings {
	public static final String SETTINGS_FILE = "SETTINGS";

	public float sensitivityX = 1;
	public float sensitivityY = 1;
	public float smoothing = 0.95f;
	public boolean dashTowardTap = false;
	public boolean drawHitboxes = false;

	void open() {
		FileHandle file = Gdx.files.local(SETTINGS_FILE);

		if (file.exists()) {
			BufferedReader reader = new BufferedReader(file.reader());

			try {
				sensitivityX = Float.parseFloat(reader.readLine());
				sensitivityY = Float.parseFloat(reader.readLine());
				smoothing = Float.parseFloat(reader.readLine());
				dashTowardTap = Boolean.parseBoolean(reader.readLine());
				drawHitboxes = Boolean.parseBoolean(reader.readLine());
				reader.close();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void close() {
		FileHandle file = Gdx.files.local(SETTINGS_FILE);

		BufferedWriter writer = new BufferedWriter(file.writer(false));
		try {
			writer.write(String.valueOf(sensitivityX) + "\n");
			writer.write(String.valueOf(sensitivityY) + "\n");
			writer.write(String.valueOf(smoothing) + "\n");
			writer.write(String.valueOf(dashTowardTap) + "\n");
			writer.write(String.valueOf(drawHitboxes) + "\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
