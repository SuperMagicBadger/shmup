package com.cowthegreat.shmup.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Matrix4;
import com.cowthegreat.shmup.SHMUP;

public class AuraMesh {
	private ShaderProgram shader;
	private Mesh aura;
	
	private SHMUP game;
	
	private int sections = 20;
	private int verticies = sections + 1;
	private int indicies = sections * 6;
	
	public AuraMesh(SHMUP shmupgame){
		game = shmupgame;
		
		shader = new ShaderProgram(
				Gdx.files.internal("shaders/aura_shader.vsh"),
				Gdx.files.internal("shaders/aura_shader.fsh"));
		aura = new Mesh(false, verticies, indicies,
				new VertexAttribute(Usage.Position, 3, "a_position"));
		
		System.out.println(shader.isCompiled() ? "aura shader compiled" : "aura:\n" + shader.getLog());
	}
	
	public void draw(Matrix4 proj, Circle circ, Color col){
		float[] points = new float[verticies * 3];
		short[] order = new short[sections + 2];
		int i = 0;
		
		// center
		points[i++] = circ.x;
		points[i++] = circ.y;
		points[i++] = 0;
		
		order[0] = 0;
		
		for(int j = 0; j < sections; j++){
			points[i++] = circleX(((float)j) / sections) * circ.radius;
			points[i++] = circleY(((float)j) / sections) * circ.radius;
			points[i++] = 0;
			order[j+1] = (short) (j + 1);
		}
		
		order[order.length -1] = 1;
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		aura.setVertices(points);
		aura.setIndices(order);
		shader.begin();
		shader.setUniformf("u_color", col.r, col.g, col.b, col.a);
		shader.setUniformf("u_radius", game.gameWidth);
		shader.setUniformf("u_center", circ.x, circ.y);
		shader.setUniformf("u_resolution", game.gameWidth, game.gameHeight);
		shader.setUniformMatrix("u_proj", proj);
		aura.render(shader, GL20.GL_TRIANGLE_FAN);
		shader.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private float circleX(float i) {
		return (float) Math.cos(2 * Math.PI * i);
	}

	private float circleY(float i) {
		return (float) Math.sin(2 * Math.PI * i);
	}
}
