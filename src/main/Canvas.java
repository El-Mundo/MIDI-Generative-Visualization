package main;

import processing.core.PApplet;
import processing.opengl.PGraphics2D;
import processing.pdf.PGraphicsPDF;

public abstract class Canvas extends PGraphics2D {
	private PGraphicsPDF gPDF;
	private String pdfPath;
	
	//A homebrew subclass to replace createGraphics()
	public Canvas(int width, int height, String absPath, PApplet parent) {
		super();
		this.setParent(parent);
		this.setPrimary(false);
		this.setSize(width, height);
		this.gPDF = (PGraphicsPDF) parent.createGraphics(width, height, PDF, absPath);
		this.pdfPath = absPath;
	}
	
	public void draw() {
		beginDraw();
		beginRaw(gPDF);
		drawContent();
		save(Main.screenshotPath);
		endRaw();
		System.out.println("\nPDF saved: " + pdfPath);
		endDraw();
	}
	
	public abstract void drawContent();
}