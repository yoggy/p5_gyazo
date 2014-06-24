import p5_gyazo.*;

PImage img = loadImage("http://farm8.staticflickr.com/7118/7446163748_fd8ace754f.jpg");
Gyazo gyazo = new Gyazo(this);  
size(img.width, img.height);

// draw image
image(img, 0, 0);

// upload image
gyazo.upload(img);

