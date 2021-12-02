package main;

import processing.core.PApplet;
import processing.opengl.PGraphics3D;
//import processing.pdf.PGraphicsPDF;

public abstract class Canvas3D extends PGraphics3D {
	//private PGraphicsPDF gPDF;
	//private String pdfPath;
	
	//Another homebrew subclass for 3D graphics
	public Canvas3D(int width, int height, String absPath, PApplet parent) {
		super();
		this.setParent(parent);
		this.setPrimary(false);
		this.setSize(width, height);
		//this.gPDF = (PGraphicsPDF) parent.createGraphics(width, height, PDF, absPath);
		//this.pdfPath = absPath;
	}
	
	public void draw() {
		beginDraw();
		//beginRaw(gPDF);
		drawContent();
		save(Main.screenshotPath);
		//endRaw();
		//System.out.println("\nPDF saved: " + pdfPath);
		endDraw();
	}
	
	public abstract void drawContent();
}
