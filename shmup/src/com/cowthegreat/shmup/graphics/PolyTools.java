package com.cowthegreat.shmup.graphics;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.cowthegreat.shmup.SHMUP;

public class PolyTools {
	public static boolean intersect(Polygon polyOne, Polygon polyTwo, Vector2 mtv){
		// test bounding rectangles
		if(!polyOne.getBoundingRectangle().overlaps(polyTwo.getBoundingRectangle())){
			return false;
		}
		
		float[] p1 = polyOne.getTransformedVertices();
		float[] p2 = polyTwo.getTransformedVertices();
		float magnitude = Float.MAX_VALUE;
		boolean a = true;
		
		// test polygon one axes
		for(int i = 0; i < p1.length; i+=2){
			
			// find the axis
			float x1 = p1[i];
			float y1 = p1[i + 1];
			float x2 = p1[(i + 2) % p1.length];
			float y2 = p1[(i + 3) % p1.length];
			Vector2 axis = findAxis(x1, y1, x2, y2).nor();
			
			// project polygon one
			Vector2 projection1 = project(axis, p1);
			float min1 = projection1.x;
			float max1 = projection1.y;
			SHMUP.vector_pool.free(projection1);
			
			// project polygon two
			Vector2 projection2 = project(axis, p2);
			float min2 = projection2.x;
			float max2 = projection2.y;
			SHMUP.vector_pool.free(projection2);
			
			// find the overlap
			float overlap1 = max1 - min2;
			float overlap2 = max2 - min2;
			float overlap = Math.abs(overlap1) < Math.abs(overlap2) ? overlap1 : overlap2;
			// if theres no overlap, we found the separating axis
			if(Math.min(max1, max2) - Math.max(min1, min2) <= 0){
				return false;
			} else if (Math.abs(overlap) < Math.abs(magnitude)){
				magnitude = overlap;
				mtv.set(axis);
			}
			
			SHMUP.vector_pool.free(axis);
		}
		
		// test polygon one axes
//		for(int i = 0; i < p1.length; i+=2){
//			
//			// find the axis
//			float x1 = p2[i];
//			float y1 = p2[i + 1];
//			float x2 = p2[(i + 2) % p1.length];
//			float y2 = p2[(i + 3) % p1.length];
//			Vector2 axis = findAxis(x1, y1, x2, y2).nor();
//			
//			// project polygon one
//			Vector2 projection1 = project(axis, p1);
//			float min1 = projection1.x;
//			float max1 = projection1.y;
//			SHMUP.vector_pool.free(projection1);
//			
//			// project polygon two
//			Vector2 projection2 = project(axis, p2);
//			float min2 = projection2.x;
//			float max2 = projection2.y;
//			SHMUP.vector_pool.free(projection2);
//			
//			// find the overlap
//			float overlap = (Math.min(max1, max2) - Math.max(min1, min2));
//			// if theres no overlap, we found the separating axis
//			if(overlap <= 0){
//				return false;
//			} else if (overlap < magnitude){
//				magnitude = overlap;
//				a = false;
//				mtv.set(axis);
//			}
//			
//			SHMUP.vector_pool.free(axis);
//		}
		
		if(mtv != null){
			mtv.scl(magnitude);
		}
		return true;
	}
	
	private static Vector2 findAxis(float x1, float y1, float x2, float y2){
		Vector2 v = SHMUP.vector_pool.obtain();
		v.set(y1 - y2, -(x1 - x2));
		return v;
	}
	
	private static float project(Vector2 axis, float x, float y){
		Vector2 v = SHMUP.vector_pool.obtain();
		v.set(x, y);
		float d = axis.dot(v);
		SHMUP.vector_pool.free(v);
		return d;
	}
	
	private static Vector2 project(Vector2 axis, float[] points){
		Vector2 v = SHMUP.vector_pool.obtain();
		v.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
		for(int i = 0; i < points.length; i+=2){
			float p = project(axis, points[i], points[i+1]);
			v.x = p < v.x ? p : v.x;
			v.y = p > v.y ? p : v.y;
		}
		return v;
	}
}
