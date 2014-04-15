package com.cowthegreat.shmup.graphics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;

public class TexturedCircle {

	public float girth;
	public Circle circle;
	public TextureRegion texRegion;

	public int count = 10;
	public float alhpa = 1;
	
	ArrayList<Vector2> outerPoints = new ArrayList<Vector2>();
	ArrayList<Vector2> innerPoints = new ArrayList<Vector2>();

	public TexturedCircle() {
	}

	public void draw(ImmediateModeRenderer renderer) {
		if(outerPoints.isEmpty()){
			generate();
		}
		Vector2 inner, outer;
		Vector2 inner2, outer2;

		renderer.color(1, 0, 0, 1);

		float u, v, u2, v2;
		u = texRegion.getU();
		v = texRegion.getV();
		u2 = texRegion.getU2();
		v2 = texRegion.getV2();

		texRegion.getTexture().bind();
		
		for (int i = 0; i < outerPoints.size() - 1; i++) {
			outer = outerPoints.get(i);
			outer2 = outerPoints.get(i + 1);
			inner = innerPoints.get(i);
			inner2 = innerPoints.get(i + 1);

			renderer.color(1, 1, 1, alhpa);
			renderer.texCoord(u, v2);
			renderer.vertex(outer.x, outer.y, 0);
			renderer.color(1, 1, 1, alhpa);
			renderer.texCoord(u2, v2);
			renderer.vertex(inner.x, inner.y, 0);
			renderer.color(1, 1, 1, alhpa);
			renderer.texCoord(u2, v);
			renderer.vertex(inner2.x, inner2.y, 0);

			renderer.color(1, 1, 1, alhpa);
			renderer.texCoord(u, v2);
			renderer.vertex(outer.x, outer.y, 0);
			renderer.color(1, 1, 1, alhpa);
			renderer.texCoord(u, v);
			renderer.vertex(outer2.x, outer2.y, 0);
			renderer.color(1, 1, 1, alhpa);
			renderer.texCoord(u2, v);
			renderer.vertex(inner2.x, inner2.y, 0);

		}

		outer = outerPoints.get(outerPoints.size() - 1);
		outer2 = outerPoints.get(0);
		inner = innerPoints.get(innerPoints.size() - 1);
		inner2 = innerPoints.get(0);

		renderer.color(1, 1, 1, alhpa);
		renderer.texCoord(u, v2);
		renderer.vertex(outer.x, outer.y, 0);
		renderer.color(1, 1, 1, alhpa);
		renderer.texCoord(u2, v2);
		renderer.vertex(inner.x, inner.y, 0);
		renderer.color(1, 1, 1, alhpa);
		renderer.texCoord(u2, v);
		renderer.vertex(inner2.x, inner2.y, 0);

		renderer.color(1, 1, 1, alhpa);
		renderer.texCoord(u, v2);
		renderer.vertex(outer.x, outer.y, 0);
		renderer.color(1, 1, 1, alhpa);
		renderer.texCoord(u, v);
		renderer.vertex(outer2.x, outer2.y, 0);
		renderer.color(1, 1, 1, alhpa);
		renderer.texCoord(u2, v);
		renderer.vertex(inner2.x, inner2.y, 0);
	}

	public void generate() {
		for (int i = 0; i < outerPoints.size(); i++) {
			SHMUP.vector_pool.free(outerPoints.get(i));
			SHMUP.vector_pool.free(innerPoints.get(i));
		}
		outerPoints.clear();
		innerPoints.clear();

		for (int i = 0; i < count; i++) {
			Vector2 outer = SHMUP.vector_pool.obtain();
			Vector2 inner = SHMUP.vector_pool.obtain();

			float value = ((float) i) / count;

			outer.set(circleX(value), circleY(value)).scl(circle.radius).add(circle.x, circle.y);
			inner.set(circleX(value), circleY(value))
					.scl(circle.radius - girth).add(circle.x, circle.y);

			outerPoints.add(outer);
			innerPoints.add(inner);
		}
	}

	private float circleX(float i) {
		return (float) Math.cos(2 * Math.PI * i);
	}

	private float circleY(float i) {
		return (float) Math.sin(2 * Math.PI * i);
	}
}
