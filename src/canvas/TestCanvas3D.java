package canvas;

import main.Canvas3D;
import processing.core.PApplet;

public class TestCanvas3D extends Canvas3D {

	public TestCanvas3D(int width, int height, String absPath, PApplet parent) {
		super(width, height, absPath, parent);
	}

	@Override
	public void drawContent() {
		background(0);
		fill(0);
		rect(0,0,width,height);
		fill(255);
		lights();
		directionalLight(255, 0, 0, 1.0f, 0.5f, 0.0f);
		noStroke();
		ortho(-200, 200, 200, -200, -1000, 1000);
		rotateY(20);
		rotateX(10);
		box(100);
		sphere(100);
	}

}
