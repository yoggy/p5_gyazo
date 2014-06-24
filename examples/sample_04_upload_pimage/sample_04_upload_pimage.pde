import p5_gyazo.*;

PImage img = loadImage("http://farm8.staticflickr.com/7118/7446163748_fd8ace754f.jpg");
size(img.width, img.height);

Gyazo gyazo = new Gyazo(this);  
gyazo.upload(img);

image(img, 0, 0);
