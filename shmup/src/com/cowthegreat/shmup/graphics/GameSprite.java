package com.cowthegreat.shmup.graphics;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GameSprite extends Sprite {

	public float rotationalVelocity;
	public Vector2 velocity;
	private Animation anim;
	private float timer;
	private boolean visible;

	private ArrayList<ParticleEffect> emitterList;

	private HashMap<ParticleEffect, ParticleEffectListener> effectListeners;

	public interface ParticleEffectListener {
		public void effectFinished();
	}

	public GameSprite(TextureRegion region) {
		super(region);

		emitterList = new ArrayList<ParticleEffect>();
		effectListeners = new HashMap<ParticleEffect, GameSprite.ParticleEffectListener>();
		velocity = new Vector2();
		rotationalVelocity = 0;

		visible = true;
	}

	public GameSprite(Animation a) {
		anim = a;
		timer = 0;
		setRegion(anim.getKeyFrame(timer));

		emitterList = new ArrayList<ParticleEffect>();
		effectListeners = new HashMap<ParticleEffect, GameSprite.ParticleEffectListener>();
		velocity = new Vector2();
		rotationalVelocity = 0;

		visible = true;
	}

	public void update(float delta) {
		if (anim != null) {
			timer += delta;
			TextureRegion tr = anim.getKeyFrame(timer);
			setSize(tr.getRegionWidth(), tr.getRegionHeight());
			setRegion(anim.getKeyFrame(timer));
		}

		for (int i = emitterList.size() - 1; i >= 0; i--) {
			emitterList.get(i).setPosition(getX() + getWidth() / 2f,
					getY() + getHeight() / 2f);
			emitterList.get(i).update(delta);
			if (emitterList.get(i).isComplete()) {
				if (effectListeners.containsKey(emitterList.get(i))) {
					ParticleEffectListener listener = effectListeners
							.get(emitterList.get(i));
					listener.effectFinished();
					effectListeners.remove(emitterList.get(i));
				}
				emitterList.remove(i);
			}
		}

		rotate(rotationalVelocity * delta);

		if (getRotation() > 360)
			rotate(-360);
		if (getRotation() < 0)
			rotate(360);

		setX(getX() + velocity.x * delta);
		setY(getY() + velocity.y * delta);
	}

	public void move(Vector2 v) {
		move(v.x, v.y);
	}
	
	public void move(float x, float y){
		setX(x + getX());
		setY(y + getY());
	}

	public void setVisible(boolean set) {
		visible = set;
	}

	public void toggleVisible() {
		visible = !visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void addParticles(ParticleEffect effect,
			ParticleEffectListener listener) {
		emitterList.add(effect);
		if (listener != null) {
			effectListeners.put(effect, listener);
		}
	}

	public void clearParticles() {
		emitterList.clear();
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		if (!visible)
			return;
		for (int i = 0; i < emitterList.size(); i++) {
			emitterList.get(i).draw(spriteBatch);
		}
		super.draw(spriteBatch);
	}

	public float getOriginPosX() {
		return getX() + getOriginX();
	}

	public float getOriginPosY() {
		return getY() + getOriginY();
	}
	
	public float right(){
		return getX() + getWidth();
	}
	
	public float top(){
		return getY() + getHeight();
	}
}