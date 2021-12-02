package canvas;

import java.util.ArrayList;

import main.Canvas3D;
import main.MidiConverter;
import main.MidiConverter.MidiNote;
import processing.core.PApplet;
import processing.core.PVector;

public class StarCanvas3D extends Canvas3D {
	/* The default song for this canvas is Space Junk Galaxy from Super Mario Galaxy
	 * Ch0, Ch1, Ch6, Ch7, Ch8, Ch9 - String Chord
	 * Ch2, Ch3, Ch4, Ch5 - Piano chord
	 * Ch10, Ch11 - Theme
	 */
	
	final private static int NEAR_PLANE = 6400;
	final private static int X_PLANE = 3000, Y_PLANE = 320, DEPTH = 5600;
	final private static int X_DIV = 240, Y_OFFSET = 10;
	final private static float SPHERE_SIZE = 8.0F;
	//The smaller Y's acceleration is, the smoother the channels are drawn
	final private static float Y_ACC = 2.5F;
	
	float zMove;

	public StarCanvas3D(int width, int height, String absPath, PApplet parent) {
		super(width, height, absPath, parent);
	}

	@Override
	public void drawContent() {
		background(24);
		//Yellow light
		directionalLight(255, 120, 0, 1, 1, 0);
		directionalLight(126, 126, 126, 0, -1, -1);
		pointLight(255, 120, 0, 0, 0, 0);
		ambientLight(64, 96, 124);
		noStroke();
		
		perspective(PI/3.0f, 1.0f, cameraZ/10.0f, cameraZ*20.0f);
		
		//Lower yellow surface
		push();
		translate(width/2, height*0.62f, NEAR_PLANE);
		zMove = DEPTH / MidiConverter.song.totalChannels;
		for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			fill(PApplet.map(c, 0, MidiConverter.song.totalChannels, 255, 120));
			drawChannel(c);
		}
		pop();
		
		//Blue light
		noLights();
		directionalLight(0, 120, 255, 1, -1, 0);
		directionalLight(126, 126, 126, 0, 1, -1);
		pointLight(0, 120, 255, 0, height, 0);
		ambientLight(124, 96, 64);
		
		//Upper blue surface
		push();
		translate(width/2, height*0.38f, NEAR_PLANE);
		scale(1, -1, 1);
		for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			fill(PApplet.map(c, 0, MidiConverter.song.totalChannels, 255, 120));
			drawChannel(c);
		}
		pop();
	}
	
	private static float[] pointYs = new float[X_DIV];
	
	public void drawChannel(int chn) {
		int tickLength = (int)MidiConverter.song.totalTicks;
		ArrayList<PVector> points = new ArrayList<PVector>();
		float x = 0.0f, y = 0.0f, ySpeed = 0.0f, target = 0.0f;
		int pointCode = 0;
		
		for(MidiNote note : MidiConverter.notes) {
			if(note.channel == chn) {
				float px = PApplet.map(note.startTick+note.tickLength/2, 0, tickLength, -X_PLANE, X_PLANE);
				float py = PApplet.map(note.key, 0, 255, -Y_PLANE, Y_PLANE);
				points.add(new PVector(px, py));
			}
		}
		//Ignore empty channels
		if(points.size() <= 5) {
			return;
		}
		translate(0, -Y_OFFSET, -zMove);
		target = (points.get(pointCode).y - y) / 2;
		
		for(int i=0; i<X_DIV; i++) {
			x = PApplet.map(i, 0, X_DIV, -X_PLANE, X_PLANE);
			
			//Update target Y-Axis
			if(x > points.get(pointCode).x && pointCode < points.size() - 1) {
				pointCode ++;
				target = (points.get(pointCode).y - y) / 2;
			}
			
			if(y > target) {
				ySpeed -= Y_ACC;
			}else {
				ySpeed += Y_ACC;
			}
			
			push();
			y += ySpeed;
			translate(x, y);
			sphere(SPHERE_SIZE);
			pop();
			
			//Connect with corresponding point on last channel
			beginShape(QUAD_STRIP);
			vertex(x-2, y-2, 0); vertex(x-2, pointYs[i]-2+Y_OFFSET, zMove);
			vertex(x+2, y-2, 0); vertex(x+2, pointYs[i]-2+Y_OFFSET, zMove);
			vertex(x+2, y+2, 0); vertex(x+2, pointYs[i]+2+Y_OFFSET, zMove);
			vertex(x-2, y+2, 0); vertex(x+2, pointYs[i]+2+Y_OFFSET, zMove);
			endShape();
			
			pointYs[i] = y;
		}
	}

}
