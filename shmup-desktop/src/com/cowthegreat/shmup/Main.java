package com.cowthegreat.shmup;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;
import com.cowthegreat.shmup.SHMUP;

public class Main {
	public static void main(String[] args) throws IOException {

		// SHMUP.USE_EXTERNAL_RESOURCES = true;
		if (SHMUP.USE_EXTERNAL_RESOURCES) {
			Settings settings = new Settings();
			settings.minHeight = settings.minWidth = 2;
			settings.maxHeight = settings.maxWidth = 1024;
			settings.filterMag = TextureFilter.Nearest;
			settings.filterMin = TextureFilter.Nearest;
			settings.pot = true;

			TexturePacker2.process(settings, "../images",
					"../shmup-android/assets", "shmup");

			TexturePacker2.process(settings, "../images", "./", "shmup");

			File sourceDir = new File("../particles/");
			File destDir = new File("../shmup-android/assets/particles/");

			for (File f : sourceDir.listFiles()) {
				FileUtils.copyFileToDirectory(f, destDir);
			}
		}
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "shmup";
		cfg.useGL20 = true;
		cfg.width = 480;
		cfg.height = 320;
		
		new LwjglApplication(new SHMUP(new PCPlayerController()), cfg);
	}
}
