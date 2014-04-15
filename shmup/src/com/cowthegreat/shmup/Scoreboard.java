package com.cowthegreat.shmup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.Collections;

public class Scoreboard {

	private static final String score_file = "SCORES";

	public class Score implements Comparable<Score>{
		public float time;
		public int wave;
		public int scoreValue;
		
		@Override
		public int compareTo(Score s) {
			if(s.scoreValue == scoreValue){
				if(s.wave == wave){
					return (int) (s.time - time);
				}
				return s.wave - wave;
			}
			return s.scoreValue - scoreValue;
		}
		
		@Override
		public String toString() {
			return "Wave " + wave + " with " + scoreValue + " kills in " + (int)(time) + "s"; 
		}
	}
	
	public ArrayList<Score> highScores;
	public int currentKills;
	public int currentWave;
	public float currentTime;

	public Scoreboard(){
		highScores = new ArrayList<Scoreboard.Score>();
	}
	
	public void loadScores() {
		FileHandle handle = Gdx.files.local(score_file);
		if (handle.exists()) {
			try{
				BufferedReader reader = new BufferedReader(handle.reader());
				String wave = reader.readLine();
				String score = reader.readLine();
				String time = reader.readLine();
				while(wave != null){
					Score s = new Score();
					s.wave = Integer.parseInt(wave);
					s.time = Float.parseFloat(time);
					s.scoreValue = Integer.parseInt(score);
					highScores.add(s);
					wave = reader.readLine();
					score = reader.readLine();
					time = reader.readLine();
				}
			} catch (IOException ex){
				highScores.clear();
			}
		}
		if(highScores.size() < 10){
			for(int i = highScores.size(); i < 10; i++){
				Score s = new Score();
				s.wave = 0;
				s.time = 0;
				s.scoreValue = 0;
				highScores.add(s);
			}
		}
		Collections.sort(highScores);
	}

	public void saveScores() {
		FileHandle handle = Gdx.files.local(score_file);
		try {
			BufferedWriter writer = new BufferedWriter(handle.writer(false));
			for(int i = 0; i < highScores.size(); i++){
				writer.write(String.valueOf(highScores.get(i).wave) + "\n");
				writer.write(String.valueOf(highScores.get(i).scoreValue) + "\n");
				writer.write(String.valueOf(highScores.get(i).time) + "\n");
			}
			writer.close();
		} catch (IOException ex) {
			System.out.println("did not write");
		}
	}
	
	public void updateHighScores(){
		if(currentKills > highScores.get(highScores.size() - 1).scoreValue){
			highScores.get(highScores.size() - 1).scoreValue = currentKills;
			highScores.get(highScores.size() - 1).wave = currentWave;
			highScores.get(highScores.size() - 1).time = currentTime;
			Collections.sort(highScores);
		}
	}
}
