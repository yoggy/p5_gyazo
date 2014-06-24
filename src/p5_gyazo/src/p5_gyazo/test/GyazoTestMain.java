package p5_gyazo.test;

import p5_gyazo.Gyazo;
import processing.core.PApplet;

public class GyazoTestMain extends PApplet {
	private static final long serialVersionUID = 4258434090200713048L;

	Gyazo gyazo;

	public void setup() {
		size(480, 480);
		gyazo = new Gyazo(this); // gyazo.com
		//gyazo = new Gyazo(this, "http://192.168.1.1/upload"); // local gyazo server
	}

	public void draw() {
		// draw something
		if (frameCount % 300 == 0) {
			background(random(255), random(255), random(255));
		}

		int x = (int) random(width);
		int y = (int) random(height);
		int r = (int) random(50) + 30;

		noStroke();
		fill(random(255), random(255), random(255));
		ellipse(x, y, r, r);
	}

	public void keyPressed() {
		if (key == 'g') {
			gyazo.upload(); // upload screen image to Gyazo
		}
	}

	public void onGyazoUploadFinished(String url) {
		println("url=" + url);
	}

	public void onGyazoUploadError(String message) {
		println("message=" + message);
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "p5_gyazo.test.GyazoTestMain" });
	}
}
