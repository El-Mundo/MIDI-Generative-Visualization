package canvas;

import main.Canvas;
import main.MidiConverter;
import main.MidiConverter.MidiNote;
import processing.core.PApplet;

public class TestCanvas extends Canvas {

	public TestCanvas(int width, int height, String absPath, PApplet parent) {
		super(width, height, absPath, parent);
	}

	
	///////////////////Rhythm-Color Strips
	@Override
	public void drawContent() {
		colorMode(HSB, 255);
		background(255);
		noStroke();
		for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			drawChannel(c);
		}
	}
	
	private void drawChannel(int channel) {
		float x1 = PApplet.map(channel, 0, MidiConverter.song.totalChannels, 0, width);
		float x2 = x1 + width / MidiConverter.song.totalChannels;
		int tickLength = (int) MidiConverter.song.totalTicks;
		float hue = PApplet.map(channel, 0, MidiConverter.song.totalChannels, 0, 255);
		beginShape(QUAD_STRIP);
		fill(hue, 0, 0);
		vertex(x1, height);
		vertex(x2+1, height);
		for(MidiNote note : MidiConverter.notes) {
			if(note.channel == channel) {
				float y1 = PApplet.map(note.startTick, 0, tickLength, height, 0);
				float color = PApplet.map(note.key, 32, 96, 0, 255);
				fill(hue, color, color);
				vertex(x1, y1);
				vertex(x2+1, y1);
			}
		}
		fill(0);
		vertex(x1, 0);
		vertex(x2, 0);
		endShape();
	}
	
	/*///////////////Swirl
	@Override
	public void drawContent() {
		colorMode(HSB, 255);
		background(0);
		noFill();
		strokeWeight(80);
		float angle = 2.0f*PApplet.PI / (float)MidiConverter.song.totalChannels;
		translate(width/2, height/2);
		for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			drawChannel(c);
			rotate(angle);
		}
	}
	
	private void drawChannel(int channel) {
		beginShape(LINE_STRIP);
		boolean first = true;
		int tickLength = (int)MidiConverter.song.totalTicks;
		float hue = PApplet.map(channel, 0, MidiConverter.song.totalChannels, 0, 255);
		float y1 = 0.0f;
		for(MidiNote note : MidiConverter.notes) {
			if(note.channel == channel) {
				float x1 = 0.0f;
				if(!first)
					x1 = PApplet.map(note.startTick, 0, tickLength, 0, width/2);
				else 
					first = false;
					
				y1 = PApplet.map(note.key, 32, 128, 0, PApplet.map(x1, 0, width/2, 0, -height/4));
				stroke(hue, 255, PApplet.constrain(-y1, 0, 128)*2);
				vertex(x1, y1);
			}
		}
		stroke(hue, 0, 0);
		vertex(width*0.75f, y1/2);
		endShape();
	}*/
	
	/*///////////////LINE_STRIP SWIRL
	@Override
	public void drawContent() {
		colorMode(HSB, 255);
		background(255);
		noFill();
		strokeWeight(5);
		translate(width/2, height/2);
		stroke(0, 0, 0);
		for(int t=0; t<MidiConverter.song.totalTicks; t++) {
			drawTick(t);
		}
	}
	int temp = 0;
	float chnOffest[] = new float[16];
	public void drawTick(int tick) {
		for(MidiNote note : MidiConverter.notes) {
			if(note.startTick == tick) {
				chnOffest[note.channel] = PApplet.map(note.key, 0, 128, 0.0f, 360.0f);
				System.out.println(tick+":"+note.toString());
			}
		}
		if(tick%2!=0)return;
		int chn = MidiConverter.song.totalChannels;
		beginShape(POLYGON);
		float r = PApplet.map(tick, 0, MidiConverter.song.totalTicks, height, 0);
		float bri = PApplet.map(r, 0, height/2, 0, 255);
		for(int c=0; c<chn; c++) {
			float angle = PApplet.map(c, 0, chn, 0, 2*PApplet.PI);
			float hue = PApplet.map(c, 0, chn, 0, 255);
			PVector point = new PVector(PApplet.cos(angle)*r, PApplet.sin(angle)*r);
			point.setMag(point.mag()+chnOffest[c]);
			stroke(hue, 255, bri, 60);
			curveVertex(point.x, point.y);
		}
		endShape(CLOSE);
	}*/
	
	/*@Override
	public void drawContent() {
		colorMode(HSB, 255);
		background(0);
		fill(0);
		rect(-1, -1, width+1, height+1);
		noFill();
		stroke(255);
		translate(width/2, height/2);
		strokeWeight(3);
		float offset = 0.0f;
		for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			beginShape(POLYGON);
			for(float off = 0; off < 360; off += 0.2f) {
				float r = PApplet.map(off, 0, 360, 0, height/2);
				vertex(PApplet.cos(PApplet.radians(off)) * r * offset, PApplet.sin(PApplet.radians(off)) * r);
				/*for(MidiNote note : MidiConverter.notes) {
					if(note.channel == c) {
						float y1 = PApplet.map(note.startTick, 0, MidiConverter.song.totalTicks, height/2, 0);
						float x1 = PApplet.map(note.startTick, 0, MidiConverter.song.totalTicks, height, 0);
						float color = PApplet.map(note.key, 32, 96, 0, 255);
						fill(255, color, color);
						vertex(x1, y1);
						vertex(x2+1, y1);
					}
				}
			}
			endShape();
			rotate(PApplet.radians(32));
		}
	}*/
	
	/*----------------------An older version of RiverCanvas--------------------//
	final private static int SAMPLE_NUMBER = 1200, SAMPLE_SIZE = 1200;
	final private static float ACC = 2.2F, MAX_SHIFT = 128.0F;
	final private static int[] THEME_CHANNELS = {0, 4, 5, 10};
	final private static int CHN_DIV = 40;
	final private static float CHN_WIDTH_RATE = 1.3f;

	private float[] samples = new float[SAMPLE_NUMBER];
	private float chnWidth;
	
	public RiverCanvas(int width, int height, String absPath, PApplet parent) {
		super(width, height, absPath, parent);
	}
	
	@Override
	public void draw() {
		beginDraw();
		drawContent();
		save(Main.screenshotPath);
		endDraw();
	}

	@Override
	public void drawContent() {
		//Divide channel width with channel number
		chnWidth = width / MidiConverter.song.totalChannels;
		colorMode(HSB, 255);
		background(240);
		noFill();
		strokeWeight(3);
		
		push();
		translate(0, height/2);
		//getThemeCurve(THEME_CHANNELS);
		
		for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			getThemeCurve(c);
			drawBackground(c);
		}
		pop();
		
		//push();
		//translate(width/2, height/2);
		//scale(-1, 1);
		//for(int c=0; c<MidiConverter.song.totalChannels; c++) {
			//getThemeCurve(c);
			//drawBackground(c);
			//translate(width/MidiConverter.song.totalChannels/2, 0);
		//}
		//pop();
	}
	
	private void drawBackground(int chn) {
		int channels = MidiConverter.song.totalChannels;
		//Symmetric hue
		float hue = PApplet.map(PApplet.abs(channels/2-chn), 0, channels, 15, 60);
		float color = 0.0f;
		int minKey = 255, maxKey = 0;
		int tickLength = (int) MidiConverter.song.totalTicks;
		ArrayList<SampleColor> colors = new ArrayList<SampleColor>();
		int loadedSample = 0;
		
		for(MidiNote note : MidiConverter.notes) {
			if(note.channel == chn) {
				int noteKey = note.key;
				if(noteKey > maxKey) {
					maxKey = noteKey;
				}
				if(noteKey < minKey) {
					minKey = noteKey;
				}
				colors.add(new SampleColor((int) PApplet.map(note.startTick, 0, tickLength, 0, SAMPLE_NUMBER), noteKey));
			}
		}
		if(colors.size() <= 0) {
			return;
		}
		translate(width/MidiConverter.song.totalChannels, 0);
		
		for(int d=0; d<CHN_DIV; d++) {
			//Clear loaded sample colors
			loadedSample = 0;
			
			push();
			translate(PApplet.map(d, 0, CHN_DIV, 0, chnWidth * CHN_WIDTH_RATE), 0);
			beginShape(QUAD_STRIP);
			for(int i=0; i<SAMPLE_NUMBER; i++) {
				if(loadedSample < colors.size()) {
					if(i == colors.get(loadedSample).sampleCode) {
						color = PApplet.map(colors.get(loadedSample).noteKey, minKey, maxKey, 0, 255);
						loadedSample ++;
						//strokeWeight(60);
						//stroke(hue, color, color);
						//point(samples[i], PApplet.map(i, 0, SAMPLE_NUMBER, height/2, -height/2));
					}
				}
				stroke(hue, color, color, 40);
				vertex(samples[i], PApplet.map(i, 0, SAMPLE_NUMBER, height/2, -height/2));
			}
			endShape();
			pop();
		}
	}
	
	//Similar to the generation of StarCanvas3D, with more controlling over the movements in rhythm
	public void getThemeCurve(int[] themeChannels) {
		int tickLength = (int)MidiConverter.song.totalTicks;
		ArrayList<PVector> points = new ArrayList<PVector>();
		float lastX = 0.0f;
		
		for(MidiNote note : MidiConverter.notes) {
			for(int c=0; c<themeChannels.length; c++) {
				if(note.channel == themeChannels[c]) {
					float py = PApplet.map(note.startTick+note.tickLength/2, 0, tickLength, height/2, -height/2);
					float px = PApplet.map(note.key, 0, 255, -SAMPLE_SIZE, SAMPLE_SIZE);
					points.add(new PVector(px, py));
					lastX = px;
				}
			}
		}
		//Ignore empty channels
		if(points.size() <= 5) {
			return;
		}
		points.add(new PVector(lastX, -height/2));
		
		float x = 0.0f, y = 0.0f, xSpeed = 0.0f, target = 0.0f;
		int pointCode = 0;
		target = (points.get(pointCode).x - x) / 2;
		
		for(int i=0; i<SAMPLE_NUMBER; i++) {
			y = PApplet.map(i, 0, SAMPLE_NUMBER, height/2, -height/2);
			
			//Update target X-Axis
			if(y < points.get(pointCode).y && pointCode < points.size() - 1) {
				float prevX = points.get(pointCode).x;
				pointCode ++;
				float newX = points.get(pointCode).x;
				//Ease the curve when the rhythm doesn't change
				if(newX == prevX && (xSpeed > -0.5f || xSpeed < 0.5f)) {
					xSpeed *= 0.8f;
				}
				target = (newX - x) / 2;
			}
			if(x > target && x > -MAX_SHIFT) {
				xSpeed -= ACC;
			}else if(x < target && x < MAX_SHIFT) {
				xSpeed += ACC;
			}
			x += xSpeed;
			
			samples[i] = x;
		}
	}
	
	public void getThemeCurve(int chn) {
		int[] i = {chn};
		getThemeCurve(i);
	}
	
	private class SampleColor {
		int sampleCode, noteKey;
		
		SampleColor(int sampleCode, int key) {
			this.sampleCode = sampleCode;
			this.noteKey = key;
		}
		
		@Override
		public String toString() {
			return "Note " + noteKey + " at " + sampleCode;
		}
	}*/

}
