package com.cowthegreat.shmup;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.cowthegreat.shmup.controllers.UnitController;

public class QuadTree {

	class Node {
		int level;
		Rectangle bounds;
		ArrayList<Vector2> points;
		ArrayList<UnitController> ucs;
		Node[] children;

		Node() {
			bounds = new Rectangle();
			children = new Node[4];
			points = new ArrayList<Vector2>();
			ucs = new ArrayList<UnitController>();
		}

		boolean insert(Vector2 pos, UnitController uc) {
			if (bounds.contains(pos)) {				
				if(children[0] != null){
					for (Node c : children) {
						if (c.insert(pos, uc)) {
							return true;
						}
					}	
				} else if (points.size() < perNode || level > maxDepth) {
					points.add(pos);
					ucs.add(uc);
					return true;
				} else {
					split();
					insert(pos, uc);
				}
			}
			return false;
		}

		void split() {
			float w = bounds.width / 2;
			float h = bounds.height / 2;

			children[0] = nodePool.obtain();
			children[0].level = level + 1;
			children[0].bounds.set(bounds.x, bounds.y, w, h);

			children[1] = nodePool.obtain();
			children[1].level = level + 1;
			children[1].bounds.set(bounds.x + w, bounds.y, w, h);

			children[2] = nodePool.obtain();
			children[2].level = level + 1;
			children[2].bounds.set(bounds.x, bounds.y + h, w, h);

			children[3] = nodePool.obtain();
			children[3].level = level + 1;
			children[3].bounds.set(bounds.x + w, bounds.y + h, w, h);

			for (int i = 0; i < points.size(); i++) {
				for (Node c : children) {
					if (c.insert(points.get(i), ucs.get(i))) {
						break;
					}
				}
			}
			points.clear();
			ucs.clear();
		}

		void clear() {
			for (Vector2 v : points) {
				SHMUP.vector_pool.free(v);
			}
			points.clear();
			ucs.clear();
			if (children[0] != null) {
				for (int i = 0; i < 4; i++) {
					children[i].clear();
					nodePool.free(children[i]);
					children[i] = null;
				}
			}
		}
	}

	Node root;
	int perNode = 1;
	int maxDepth = 4;

	Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	Pool<Node> nodePool = new Pool<Node>() {
		@Override
		protected Node newObject() {
			return new Node();
		}
	};

	public QuadTree(float width, float height) {
		root = nodePool.obtain();
		root.bounds.setSize(width, height);
		root.bounds.setCenter(0, 0);
		root.level = 0;
	}

	public void insert(UnitController uc) {
		Vector2 position = SHMUP.vector_pool.obtain();
		position.set(uc.getControlled().getX(), uc.getControlled().getY());
		root.insert(position, uc);
	}

	public void insert(Vector2 pos) {
		root.insert(pos, null);
	}

	public void clear() {
		root.clear();
	}

	public void drawDebug(ShapeRenderer renderer) {
		Color old = renderer.getColor();
		renderer.setColor(Color.GRAY);
		drawDebug(renderer, root);
		renderer.setColor(old);
	}

	private void drawDebug(ShapeRenderer renderer, Node n) {
		if (n.children[0] != null) {
			for (Node c : n.children) {
				drawDebug(renderer, c);
			}
		}
		switch (n.level) {
		case 0:
			renderer.setColor(Color.GRAY);
			break;
		case 1:
			renderer.setColor(Color.ORANGE);
			break;
		case 2:
			renderer.setColor(Color.BLUE);
			break;
		case 3:
			renderer.setColor(Color.GREEN);
			break;
		default:
			renderer.setColor(Color.RED);
			break;
		}
		for (Vector2 v : n.points) {
			renderer.circle(v.x, v.y, 5);
		}
		renderer.rect(n.bounds.x, n.bounds.y, n.bounds.width, n.bounds.height);
	}
	
	public ArrayList<Vector2> pointsInRange(Circle c){
		ArrayList<Vector2> neighbors = new ArrayList<Vector2>();
		
		neighbors = pointsInRange(neighbors, c, root);
		
		return neighbors;
	}
	
	private ArrayList<Vector2> pointsInRange(ArrayList<Vector2> neighbors, Circle c, Node n){
		if(Intersector.overlaps(c, n.bounds)){
			if(n.children[0] != null){
				for(Node child : n.children){
					pointsInRange(neighbors, c, child);
				}
				return neighbors;
			} else {
				for(Vector2 v : n.points){
					if(c.contains(v)){
						neighbors.add(v);
					}
				}
			}
		}
		
		return neighbors;
	}
	
	public ArrayList<UnitController> controllersInRange(Circle c){
		ArrayList<UnitController> neighbors = new ArrayList<UnitController>();
		return controllersInRange(neighbors, c, root);
	}
	
	private ArrayList<UnitController> controllersInRange(ArrayList<UnitController> neighbors, Circle c, Node n){
		if(Intersector.overlaps(c, n.bounds)){
			if(n.children[0] != null){
				for(Node child : n.children){
					controllersInRange(neighbors, c, child);
				}
				return neighbors;
			} else {
				for(int i = 0; i < n.points.size(); i++){
					if(c.contains(n.points.get(i))){
						neighbors.add(n.ucs.get(i));
					}
				}
			}
		}
		
		return neighbors;
	}
}
