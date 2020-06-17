PShape can;
float angle;
PShader BWShader;
PShader defShader;
PImage img;

void setup() {
  size(874, 337, P3D);
  BWShader = loadShader("BWfrag.glsl", "vert.glsl");
  defShader = loadShader("normFrag.glsl");
  img = loadImage("esponja.PNG");
}

void draw() {
  background(0); 
  beginShape();
  shader(defShader);
  texture(img);
  vertex(0, 0, 0, 0);
  vertex(img.width, 0, img.width, 0);
  vertex(img.width, img.height, img.width, img.height);
  vertex(0, img.height, 0, img.height);
  endShape();
  
  beginShape();
  shader(BWShader); 
  texture(img);
  vertex(img.width, 0, 0, 0);
  vertex(img.width*2, 0, img.width, 0);
  vertex(img.width*2, img.height, img.width, img.height);
  vertex(img.width, img.height, 0, img.height);
  endShape();  
}
