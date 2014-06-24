import p5_gyazo.*;

Gyazo gyazo;
  
void setup() {
  size(400, 400);
  gyazo = new Gyazo(this); // upload to gyazo.com
}

void draw() {
  ellipse(mouseX, mouseY, 100, 100);
}

void keyPressed() {
  if (key == 'g') {
    gyazo.upload();  // upload screen image to Gyazo
  }
}
