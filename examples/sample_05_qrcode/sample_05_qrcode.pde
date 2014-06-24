import p5_gyazo.*;

Gyazo gyazo;
SampleStopWatch stop_watch;
PImage qrcode_img;

void setup() {
  size(400, 400);
  gyazo = new Gyazo(this);

  stop_watch = new SampleStopWatch();
  stop_watch.center.x = width / 2;
  stop_watch.center.y = height / 2;
  stop_watch.radius = height / 2 - 30;
}

void draw() {
  background(0, 32, 64);
  stop_watch.draw();

  if (qrcode_img != null) {
    image(qrcode_img, 0, 0);
  }
}

void keyPressed() {
  if (key == 'g') {
    gyazo.upload();
  }  
}

void onGyazoUploadFinished(String image_url) {
  // QR code API http://goqr.me/api/  
  String text = image_url + ".png";
  String qrcode_url = "https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=" + text + "&dummy=.png";
  qrcode_img = loadImage(qrcode_url);
}

