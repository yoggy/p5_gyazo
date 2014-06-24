import p5_gyazo.*;

Gyazo gyazo;
  
void setup() {
  size(640, 480);
  gyazo = new Gyazo(this, "http://192.168.1.1:10080/upload.cgi"); // upload to local Gyazo server
}

void draw() {
  ellipse(mouseX, mouseY, 100, 100);
}

void keyPressed() {
  if (key == 'g')
  gyazo.upload();  // upload screen image to Gyazo
}

void onGyazoUploadFinished(String image_url) {
  println("image_url=" + image_url);
}

void onGyazoUploadError(String error_message) {
  println("error_message=" + error_message);
}

