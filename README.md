p5_gyazo
=========

p5_gyazo is a simple Gyazo client library for Processing.
You can easily upload a sketch image to Gyazo.

* [gyazo](http://gyazo.com)

Usage
=========

<pre>
import p5_gyazo.*;

Gyazo gyazo;

void setup() {
  size(640, 480);
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

void onGyazoUploadFinished(String image_url) {
  println("image_url=" + image_url);
}
</pre>

Libraries
========
p5_gyazo uses the following libraries.

Apache HttpComponents
* http://hc.apache.org/httpcomponents-client-ga/

Apache Commons FileUpload
* http://commons.apache.org/proper/commons-fileupload/

Apache Commons Logging
* http://commons.apache.org/proper/commons-logging/

