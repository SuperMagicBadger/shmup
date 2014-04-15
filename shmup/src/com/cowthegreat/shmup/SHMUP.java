package com.cowthegreat.shmup;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pool;
import com.cowthegreat.shmup.controllers.PlayerController;
import com.cowthegreat.shmup.screens.GameScreen;
import com.cowthegreat.shmup.screens.MainMenuScreen;
import com.cowthegreat.shmup.screens.SettingsScreen;

public class SHMUP extends Game {
	public static boolean USE_EXTERNAL_RESOURCES = false;
	public static final String app_name = "VECTOR DASH";
	public static final Random rng = new Random();
	
	public static Sound theme;
	public static Sound explosion;
	public static Sound charged;
	
	public Skin skn;
	public Scoreboard score;
	public Settings settings;
	public PlayerController playerControls;
	public float gameWidth = 512, gameHeight;
	public float screenRatio;
	private HashMap<String, Screen> screenMap;
	
	public static ParticleEffectPool explosion_particles;
	public static ParticleEffectPool dash_particles;
	public static ParticleEffectPool gsplode_particles;
	public static ParticleEffectPool gsplode_charge_particles;
	
	public static final Pool<Vector2> vector_pool = new Pool<Vector2>(){
		protected Vector2 newObject() {
			return new Vector2();
		}
		public Vector2 obtain() {
			Vector2 v = super.obtain();
			return v.set(0,0);
		};
		public void free(Vector2 object) {
			object.set(Vector2.Zero);
			super.free(object);
		};
	};
	
	public SHMUP(PlayerController playerControls){
		super();
		this.playerControls = playerControls;
	}
	
	@Override
	public void create() {		
		gameHeight = gameWidth * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		TextureAtlas atlas;
		
		if(USE_EXTERNAL_RESOURCES && Gdx.app.getType() == ApplicationType.Desktop){
			atlas = new TextureAtlas(Gdx.files.local("shmup.atlas"));
			skn = new Skin(Gdx.files.local("shmup.json"), atlas);
		} else {
			atlas = new TextureAtlas(Gdx.files.internal("shmup.atlas"));
			skn = new Skin(Gdx.files.internal("shmup.json"), atlas);
		}

		if(USE_EXTERNAL_RESOURCES && Gdx.app.getType() == ApplicationType.Desktop){
			theme = Gdx.audio.newSound(Gdx.files.local("../audio/theme.wav"));
			explosion = Gdx.audio.newSound(Gdx.files.local("../audio/sfx.wav"));
			charged = Gdx.audio.newSound(Gdx.files.local("../audio/shield.wav"));
		} else {
			theme = Gdx.audio.newSound(Gdx.files.internal("audio/theme.wav"));
			explosion = Gdx.audio.newSound(Gdx.files.internal("audio/sfx.wav"));
			charged = Gdx.audio.newSound(Gdx.files.internal("audio/shield.wav"));
		}
		
		if(USE_EXTERNAL_RESOURCES && Gdx.app.getType() == ApplicationType.Desktop){ 
			ParticleEffect effect;
			FileHandle particle;
			
			effect = new ParticleEffect();
			particle = Gdx.files.local("../particles/explosion.p");
			effect.load(particle, atlas);
			explosion_particles = new ParticleEffectPool(effect, 3, 15);
			
			effect = new ParticleEffect();
			particle = Gdx.files.local("../particles/dash_charge.p");
			effect.load(particle, atlas);
			dash_particles = new ParticleEffectPool(effect, 3, 15);
			
			effect = new ParticleEffect();
			particle = Gdx.files.local("../particles/gsplode.p");
			effect.load(particle, atlas);
			gsplode_particles = new ParticleEffectPool(effect, 1,5);
			
			effect = new ParticleEffect();
			particle = Gdx.files.internal("../particles/gsplode_charge.p");
			effect.load(particle, atlas);
			gsplode_charge_particles = new ParticleEffectPool(effect, 1, 5);
		} else {
			ParticleEffect effect;
			FileHandle particle;
			
			effect = new ParticleEffect();
			particle = Gdx.files.internal("particles/explosion.p");
			effect.load(particle, atlas);
			explosion_particles = new ParticleEffectPool(effect, 3, 15);
			
			effect = new ParticleEffect();
			particle = Gdx.files.internal("particles/dash_charge.p");
			effect.load(particle, atlas);
			dash_particles = new ParticleEffectPool(effect, 3, 15);
			
			effect = new ParticleEffect();
			particle = Gdx.files.internal("particles/gsplode.p");
			effect.load(particle, atlas);
			gsplode_particles = new ParticleEffectPool(effect, 1, 5);
			
			effect = new ParticleEffect();
			particle = Gdx.files.internal("particles/gsplode_charge.p");
			effect.load(particle, atlas);
			gsplode_charge_particles = new ParticleEffectPool(effect, 1, 5);
		}
		
		score = new Scoreboard();
		score.loadScores();

		settings = new Settings();
		settings.open();
		
		screenMap = new HashMap<String, Screen>();
		screenMap.put(MainMenuScreen.screenTag, new MainMenuScreen(this));
		screenMap.put(GameScreen.screenTag, new GameScreen(this));
		screenMap.put(SettingsScreen.screenTag, new SettingsScreen(this));
		
		setScreen(MainMenuScreen.screenTag);
	}
	
	public void setScreen(String screenTag){
		if(screenTag == null) {
			System.out.println("back is null");
			return;
		}
		back = null;
		setScreen(screenMap.get(screenTag));
	}
	
	String back;
	public void setScreen(String screenTag, String backTag){
		setScreen(screenTag);
		back = backTag;
	}
	
	public void back(){
		setScreen(back);
	}
	
	@Override
	public void dispose() {
		for(Screen s : screenMap.values()){
			s.dispose();
		}
		
		theme.dispose();
		explosion.dispose();
		charged.dispose();
		
		score.saveScores();
		settings.close();
		skn.dispose();
	}
}
