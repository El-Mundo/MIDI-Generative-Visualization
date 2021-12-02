package canvas;

import java.util.ArrayList;

import main.Canvas;
import main.Main;
import main.MidiConverter;
import main.MidiConverter.MidiNote;
import processing.core.PApplet;
import processing.core.PGraphics;

public class RiverCanvas extends Canvas {
	/* The default song for this canvas is Sonja Theme from Advance Wars (Nintendo, 2001).
	 * Ch0, Ch1, Ch10, Ch12, Ch16 - Brass chord
	 * Ch2, Ch3, Ch4, Ch5, Ch6 - Percussions
	 * Ch7, Ch8, Ch9, Ch13 - Bass chord
	 * Ch11 - Theme
	 * Ch14, Ch15 - Empty
	 */
	
	PGraphics subCanvas;
	
	public RiverCanvas(int width, int height, String absPath, PApplet parent) {
		super(width, height, absPath, parent);
	}
	
	@Override
	public void draw() {
		//Init random seed based on music
		Main.getRandom(0, 255, MidiConverter.song.totalTicks / MidiConverter.song.totalChannels);
		
		subCanvas = parent.createGraphics(width, height, P2D);
		subCanvas.beginDraw();
		drawSubContent();
		subCanvas.endDraw();
		
		beginDraw();
		drawContent();
		save(Main.screenshotPath);
		endDraw();
	}
	
	@Override
	public void drawContent() {
		background(240);
		colorMode(HSB, 255);
		
		drawPercussions(6);
		drawPercussions(7);
		
		colorMode(RGB, 255);
		
		for(int i=0; i<24; i++) {
			float t = PApplet.map(i, 0, 48, 160, 0);
			tint(t, 255-t, 255-t, t);
			image(subCanvas, 0, height * (0.3f - 0.005f * i));
		}
		
		for(int i=0; i<24; i++) {
			float t = PApplet.map(i, 0, 48, 160, 0);
			tint(255, 510-t, t, t);
			image(subCanvas, 0, height * (0.3f + 0.005f * i));
		}
		
		noTint();
		image(subCanvas, 0, height * 0.3f);
	}

	//A mixture of first sample in TestCanvas and WaveCanvas
	private void drawSubContent() {
		subCanvas.colorMode(HSB, 255);
		subCanvas.strokeWeight(16);
		subCanvas.noFill();
		for(int c=0; c<MidiConverter.song.totalTicks; c++) {
			drawSubChannel(c);
		}
	}
	
	private void drawSubChannel(int chn) {
		ArrayList<MidiNote> chNotes = new ArrayList<MidiNote>();
		int keyMax = 0, keyMin = 255;
		int tickLength = (int) MidiConverter.song.totalTicks;
		float hue = PApplet.map(chn, 0, MidiConverter.song.totalChannels, 30, 60);
		
		for(MidiNote note : MidiConverter.notes) {
			if(note.channel == chn) {
				chNotes.add(note);
				if(note.key > keyMax) {
					keyMax = note.key;
				}
				if(note.key < keyMin) {
					keyMin = note.key;
				}
			}
		}
		
		subCanvas.beginShape(POLYGON);
		subCanvas.stroke(30, 210, 240, 80);
		subCanvas.vertex(-1, 0);
		for(MidiNote note : chNotes) {
			float x = PApplet.map(note.startTick, 0, tickLength, 0, width);
			float y = PApplet.map(note.key, 0, 255, 0, height);
			
			float bright = PApplet.map(note.key, 0, 96, 120, 180);
			subCanvas.stroke(hue, 180, bright, 80);
			subCanvas.curveVertex(x, y);
		}
		subCanvas.stroke(30, 210, 240, 80);
		subCanvas.vertex(width, 0);
		subCanvas.endShape();
		
	}
	
	private void drawPercussions(int chn) {
		noStroke();
		ArrayList<MidiNote> notes = new ArrayList<MidiNote>();
		int tickLength = (int) MidiConverter.song.totalTicks;
		int keyMax = 0, keyMin = 255;
		long tickMin = MidiConverter.song.totalTicks, tickMax = 0;
		boolean start = true;
		
		for(MidiNote note : MidiConverter.notes) {
			if(note.channel == chn) {
				notes.add(note);
				if(note.key > keyMax) {
					keyMax = note.key;
				}
				if(note.key < keyMin) {
					keyMin = note.key;
				}
				if(start) {
					tickMin = note.startTick;
					start = false;
				}
				if(note.startTick > tickMax) {
					tickMax = note.startTick;
				}
			}
		}
		
		if(notes.size() < 5) {
			return;
		}
		
		for(MidiNote note : notes) {
			float x = PApplet.map(note.startTick, tickMin, tickMax, 0, width);
			float y = PApplet.map(note.key, keyMin, keyMax, height * 0.2f, height * 0.8f);
			float hue = PApplet.map(note.startTick, 0, tickLength, 90, 150);
			float bri = PApplet.map(note.startTick, 0, tickLength, 200, 120);
			fill(hue, 210, bri, Main.getRandom(60, 140));
			circle(x, y, 150);
		}
	}
	
}
