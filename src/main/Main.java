package main;
/*
 * @version 1.0
 * @author Shuangyuan Cao
 * @since 1.0
 * 
 * The Gameboy Adavance song data are collected from their original game,
 * with jpmac26's GBA-Mus Ripper.
 * @see https://github.com/jpmac26/gba-mus-ripper
 * 
 * Ripped Gameboy Advance games include:
 * Summon Night: Swordcraft Story 2 (Banpresto, 2004),
 * Harvest Moon: Friends of Mineral Town (Marvelous Interactive, 2003).
 * 
 * The Gameboy song data are recorded with GBFan Plus emulator of Maechiko.
 * The Gameboy MIDI file is not used in the final version because of recording's poor quality.
 * @see http://www.nextftp.com/huin/potatips/Gb2Mid.htm
 * 
 * Collected Gameboy game is:
 * The Legend of Zelda: Link's Awakening (Nintendo, 1993).
 * 
 * Song data of Green Greens from Kirby's Dream Land (Nintendo, 1992),
 * Space Junk Galaxy from Super Mario Galaxy (Nintendo, 2007),
 * and Sonja Theme from Advance Wars (Nintendo, 2001)
 * are retrieved from Video Game Music Archive.
 * @see https://vgmusic.com/
 * 
 * The code of pre-decoding MIDI files references Sami Koivu's example on StackOverFlow.
 * @see https://stackoverflow.com/questions/3850688/reading-midi-files-in-java
 * 
 * Using Processing 3 library, version 3.5.4.
 * 
 * Importing MIDI files whose length are longer than 4,294,967,296 may lead to errors
 */

import java.awt.Toolkit;
import java.io.File;

import canvas.RiverCanvas;
import canvas.StarCanvas3D;
import canvas.TestCanvas;
import canvas.WaveCanvas;
import processing.core.PApplet;
import processing.core.PSurface;

public class Main extends PApplet {
	//final protected static String DEFAULT_FILE_PATH = "mid/Summon Night - Character Select.mid";
	final protected static String DEFAULT_FILE_PATHS[] = {
			"mid/Kirby's Dream Land - Green Greens.mid",
			"mid/Summon Night - Character Select.mid",
			"mid/Harvest Moon - Intro.mid",
			"mid/Super Mario Galaxy - Space Junk Galaxy.mid",
			"mid/Advance Wars - Sonja Theme.mid"
	};
	final protected static int DEFAULT_VISUAL_STYLE = 2;
	//For printing with 40*40cm, 600dpi ((40cm/2.54)inch*600pixel)
	final private static int PREFERRED_WIDTH = 9448, PREFERRED_HEIGHT = 9448;
	public static String screenshotPath = "screen.png";
	private static int screenSize;
	private static int visualStyle;
	private static String pdfAbsPath;
	private static PApplet mainApplet;
	
	private static float screenScale;
	private static Canvas mainCanvas;
	private static Canvas3D canvas3D;
	private static PSurface surface;
	
	public static boolean streamPDF = false, streamEnd = false;
	
	public static void main(String[] args) {
		//Get device resolution and load init session
		MidiReader.initSequencer();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize().height;
		(new Initialize()).loadGUI(screenSize);
	}
	
	@Override
	public void settings() {
		//Set Processing's size and renderer
		if(screenSize > 480) {
			screenScale = (float)(screenSize - 200) / (float)PREFERRED_HEIGHT;
		}else {
			screenScale = (float)(screenSize) / (float)PREFERRED_HEIGHT;
		}
		
		size((int)(PREFERRED_WIDTH * screenScale), (int)(PREFERRED_HEIGHT * screenScale), P2D);
	}
	
	private static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
	
	@Override
	public void setup() {
		Main.mainApplet = this;
		noiseSeed(1066841);
		
		//Disable resizing
		surface = this.getSurface();
		surface.setResizable(false);
		
		String midiPath = getFileNameNoEx(MidiReader.getAbsolutePath());
		pdfAbsPath = midiPath.concat(".pdf");
		screenshotPath = midiPath.concat(".png");
		
		//Set visual style (0-4)
		switch (visualStyle) {
		case 0:
			mainCanvas = new TestCanvas(PREFERRED_WIDTH, PREFERRED_HEIGHT, pdfAbsPath, this);
			break;
		case 1:
			mainCanvas = new WaveCanvas(PREFERRED_WIDTH, PREFERRED_HEIGHT, pdfAbsPath, this);
			break;
		case 2:
			MidiReader.streamMidi(this);
			this.startPdfStream(pdfAbsPath);
			break;
		case 3:
			canvas3D = new StarCanvas3D(PREFERRED_WIDTH, PREFERRED_HEIGHT, pdfAbsPath, this);
			break;
		case 4:
			mainCanvas = new RiverCanvas(PREFERRED_WIDTH, PREFERRED_HEIGHT, pdfAbsPath, this);
			break;
			
		default:
			mainCanvas = new TestCanvas(PREFERRED_WIDTH, PREFERRED_HEIGHT, pdfAbsPath, this);
			break;
		}
		MidiReader.playMidi();
		
		if(!streamPDF) {
			background(0);
			textSize(50);
			stroke(255);
			text("Painting...", width / 2 - 110, height / 2);
		}
	}
	
	@Override
	public void draw() {
		//Draw the frame once and save it as an image for printing
		noLoop();
		if(canvas3D != null) {
			canvas3D.draw();
			image(canvas3D, 0, 0, width, height);
		}else if(streamPDF) {
			if(streamEnd) {
				//Save PDF and end streaming
				endRecord();
				save(screenshotPath);
			}else {
				/* Canvas 3 (visual style == 2) is a special one, which can stream frames in vector format and save the final frame as PDF,
				 * so enable loop for this canvas.*/
				loop();
				drawPDF();
				//Skip process below before the final frame is drawn.
				return;
			}
		}else {
			mainCanvas.draw();
			image(mainCanvas, 0, 0, width, height);
		}
		String absPath = new File(screenshotPath).getAbsolutePath();
		if(!streamPDF) {
			surface.setTitle("Image saved: " + absPath);
		}else {
			surface.setTitle("PDF saved: " + pdfAbsPath);
		}
		System.out.println("\nImage saved: " + absPath);
		System.out.println("PDF saved: " + pdfAbsPath);
	}
	
	public static float getNoise(float x, float y, float z) {
		return mainApplet.noise(x, y, z);
	}
	
	public static float getNoise(float x, float y) {
		return mainApplet.noise(x, y);
	}
	
	public static float getNoise(float x) {
		return mainApplet.noise(x);
	}
	
	public static float getRandom(float min, float max, long seed) {
		mainApplet.randomSeed(seed);
		return mainApplet.random(min, max);
	}
	
	public static float getRandom(float min, float max) {
		return mainApplet.random(min, max);
	}
	
	public static void setVisualStyle(int styleNum) {
		visualStyle = styleNum;
	}
	
	
	
	//-----------------------Code for real-time decoding-----------------------//
	/* This canvas' default song is Intro from Harvest Moon: Friends of Mineral Town.
	 * Ch0, Ch5, Ch6 - Piano chord
	 * Ch1, Ch3, Ch7 - Bass chord
	 * Ch2, Ch4 - Treble percession
	 * Ch8, Ch9 - Theme
	 */
	private RealtimeMidiReader realtimeReader;
	private int chnKey[];
	private float maxRadium;
	private int drawInterval, drawTimer;
	private int percussionBuffer = 0, sparkleChannel = 0;
	
	private void startPdfStream(String path) {
		beginRecord(PDF, path);
		streamPDF = true;
		this.realtimeReader = MidiReader.getReatimeMidiReader();
		background(0);
		
		chnKey = new int[realtimeReader.totalChannels];
		for(int i=0; i<chnKey.length; i++) {
			chnKey[i] = 0;
		}
		
		//Half of canvas edge length multiplies square root of 2 as radium to fill up the canvas
		//maxRadium = (width / 2) * 1.414f;
		maxRadium = width / 2;
		
		//Processing's PDF canvas only support 32767 objects in one file
		//Each ring of the swirl has vertices with the same number of the song's channel number
		int ringNumber = 32767 / realtimeReader.totalChannels * 3;
		drawInterval = (int) (realtimeReader.tickLength / ringNumber);
		drawTimer = drawInterval;
	}
	
	public static void endPdfStream() {
		streamEnd = true;
		System.out.println("PDF stream ended.");
	}
	
	//This function draws a swirl canvas with real-time MIDI decoding
	private void drawPDF() {
		noFill();
		colorMode(HSB);
		stroke(255);
		translate(width/2, height/2);
		int chn = realtimeReader.totalChannels;
		
		if(drawTimer > 0) {
			drawTimer --;
		}else {
			long tick = realtimeReader.getCurrentTick();
			float r = map(tick, 0, realtimeReader.tickLength, 0, maxRadium);
			float hue = 0.0f;
			
			float alpha = 0.0f;
			//15000 is the division of prelude and the theme
			if(tick < 2000) {
				alpha = map(tick, 0, 2000, 0, 20);
				hue = map(tick, 0, 2000, 30, 45);
			}else if(tick < 14000) {
				alpha = map(tick, 0, 15000, 20, 60);
				hue = map(tick, 0, 15000, 45, 75);
			}else if(tick < 16000) {
				alpha = map(tick, 0, 15000, 60, 80);
				hue = map(tick, 0, 15000, 75, 90);
			}else {
				alpha = map(tick, 15000, realtimeReader.tickLength, 80, 20);
				hue = map(tick, 15000, realtimeReader.tickLength, 90, 150);
			}
			
			//Draw a sparkle when receiving notes from a percussion channel
			boolean drawSparkle = false;
			float sparkleXY[] = new float[2];
			if(percussionBuffer > 0) {
				percussionBuffer = 0;
				sparkleChannel ++;
				if(sparkleChannel >= chn) {
					sparkleChannel = 0;
				}
				drawSparkle = true;
			}
			
			stroke(hue, 210, 210, alpha * 0.6f);
			beginShape(POLYGON);
			curveTightness(0.0f);
			curveVertex(r, 0);
			for(int i=0; i<chn; i++) {
				float angle = map(i,0,chn,0,2*PI); 
				float tr = r * map(chnKey[i], 0, 255, 1.0f, 2.0f);
				float x = cos(angle) * tr;
				float y = sin(angle) * tr;
				curveVertex(x, y);
				
				if(drawSparkle && i == sparkleChannel) {
					sparkleXY[0] = x;
					sparkleXY[1] = y;
				}
			}
			curveVertex(r, 0);
			endShape(CLOSE);
			
			if(drawSparkle) {
				fill(255, alpha * 2);
				noStroke();
				circle(sparkleXY[0] + random(-5, 5), sparkleXY[1] + random(-5, 5), r * 0.02f);
				noFill();
				strokeWeight(1);
			}
			
			//Draw a sub ring (or Cobweb?)
			push();
			scale(0.8f);
			beginShape(POLYGON);
			curveTightness(-2.5f);
			curveVertex(r, 0);
			for(int i=0; i<chn; i++) {
				float angle = map(i,0,chn,0,2*PI); 
				float tr = r * map(chnKey[i], 0, 255, 1.0f, 2.0f);
				float x = cos(angle) * tr;
				float y = sin(angle) * tr;
				curveVertex(x, y);
			}
			curveVertex(r, 0);
			endShape(CLOSE);
			pop();
			
			drawTimer = drawInterval;
		}
			
		MidiReader.checkStreamEnd();
	}
	
	public void startNote(int key, int channel) {
		if(channel < chnKey.length) {
			chnKey[channel] = key;
			
			//If the received note is on a percussion channel, send it to buffer.
			if(channel == 2 || channel == 4) {
				percussionBuffer = key;
			}
		}
	}
	
	public void endNote(int channel) {
		if(channel < chnKey.length) {
			chnKey[channel] = 0;
		}
	}
	
}
