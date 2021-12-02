package canvas;

import java.util.ArrayList;

import main.Canvas;
import main.Main;
import main.MidiConverter;
import main.MidiConverter.MidiNote;
import processing.core.PApplet;

public class WaveCanvas extends Canvas {
	/* The default song for this canvas is Character Select from Summon Night: Swordcraft Story 2
	 * Ch0, Ch1, Ch3 - Theme
	 * Ch2, Ch4 - Main chord
	 * Ch5, Ch6 - Percussions
	 * Ch7, Ch8, Ch9 - Bass chord
	 */
	
	final private static int OFFSET_DIVISION = 240;
	final private static float OFFSET_FACTOR = 2.5F;
	final private static float EASING_FACTOR = 0.004F;
	final private static float HUE_RATE = 60 / OFFSET_DIVISION;
	final private static int CHORD_DIVISION = 10;
	private float baseY;
	private int tickLength;

	public WaveCanvas(int width, int height, String absPath, PApplet parent) {
		super(width, height, absPath, parent);
	}
	
	/* Draw function has been override to avoid generating PDF file,
	 * as it will be too large for this canvas. */
	@Override
	public void draw() {
		beginDraw();
		drawContent();
		save(Main.screenshotPath);
		endDraw();
	}
	
	@Override
	public void drawContent() {
		colorMode(HSB, 255);
		background(240);
		strokeWeight(5);
		noFill();
		
		tickLength = (int) MidiConverter.song.totalTicks;
		baseY = -(height / MidiConverter.song.totalChannels * 2.0f);
		
		for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			//Draw percussion and bass chord channels first, as random circles
			switch (c) {
			case 5:
			case 6:
			case 8:
			case 9:
			case 10:
				drawPercussions(c);
				break;
			default:
				break;
			}
		}
		
		for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			drawChannel(c);
		}
	}
	
	private void drawChannel(int chn) {
		ArrayList<MidiNote> chNotes = new ArrayList<MidiNote>();
		strokeWeight(5);
		
		int keyMax = 0;
		for(MidiNote note : MidiConverter.notes) {
			if(note.channel == chn) {
				chNotes.add(note);
				//Get the highest note in this channel for filtering
				if(note.key > keyMax) {
					keyMax = note.key;
				}
			}
		}
		boolean filterChn = keyMax > 80;
		float hue = 0.0f;
		if(filterChn) {
			hue = 120.0f;
		}else {
			hue = 45.0f;
		}
		
		//Ignore channels that are too short
		if(chNotes.size() <= 3) return;
		baseY += height / MidiConverter.song.totalChannels;
		
		float stretchTarget = PApplet.map(chn, 0, MidiConverter.song.totalChannels, 0.7f, 1.5f);
		float brightOff = PApplet.map(chn, 0, MidiConverter.song.totalChannels, 0, 50);
		
		//Stretch to fit the screen
		float border = width * 0.1f;
		
		for(int offset=0; offset<OFFSET_DIVISION; offset++) {
			hue += HUE_RATE;
			beginShape(POLYGON);
			for(MidiNote note : chNotes) {
				float x = PApplet.map(note.startTick, 0, tickLength, 0-border, width+border);
				float y = PApplet.map(note.key, 0, 255, 0, height) * PApplet.map(offset, 0, OFFSET_DIVISION, OFFSET_FACTOR/2, OFFSET_FACTOR);
				float stretch = PApplet.map(PApplet.abs(width/2-x), 0, width/2, 1.0f, stretchTarget);
				//Filter vertices with easing  to avoid sharp edges
				if(filterChn) {
					y *= (255 - note.key) * EASING_FACTOR;
					hue += HUE_RATE;
				}
				y *= stretch;
				y += baseY;
				
				float bright = PApplet.map(note.startTick, 0, tickLength, 220 - brightOff, 140 - brightOff);
				
				stroke(hue, 200, bright, 40);
				curveVertex(x, y);
			}
			curveVertex(width+border, baseY);
			endShape();
		}
	}
	
	private void drawPercussions(int chn) {
		float hue = 90.0f;
		float border = height * 0.35f;
		strokeWeight(12);
		for(MidiNote note : MidiConverter.notes) {
			if(note.channel == chn) {
				float r = PApplet.constrain(PApplet.map(note.tickLength, 0, 300, 200, 500), 200, 500);
				float x = Main.getRandom(0, width, note.startTick * (int) Main.getRandom(0, 255));
				float y = Main.getRandom(border, height - border, note.key * (int) Main.getRandom(0, 255));
				for(int offset=0; offset<CHORD_DIVISION; offset++) {
					hue += HUE_RATE;
					stroke(hue, 200, 160, PApplet.map(offset, 0, CHORD_DIVISION, 210, 0));
					circle(x, y, PApplet.map(offset, 0, CHORD_DIVISION, 30, r));
				}
			}
		}
	}

}
