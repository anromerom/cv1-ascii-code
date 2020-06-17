PShape can;
float angle;
PShader BWShader;
PShader defShader;
PImage img;
int charW = 40;
int charH = 47;
int quadW = 40;
int quadH = 47;

void setup() {
  size(1044, 640, P3D);
  BWShader = loadShader("BWfrag.glsl", "vert.glsl");
  defShader = loadShader("normFrag.glsl");
  img = loadImage("matrix.png");
}

void draw() {
  /*background(0); 
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
  */
  
  for(int i=0;i<81;i++){
    int x = i%9;
    int y = i/9;
    int tx = charW*x;
    int ty = charH*y;
    int qx = quadW * x;
    int qy = quadH * y;
    beginShape();
    shader(defShader);
    texture(img);
    vertex(qx, qy, tx, ty);
    vertex(qx + quadW, qy, tx+charW, ty);
    vertex(qx + quadW, qy + quadH, tx+charW, ty+charH);
    vertex(qx, qy + quadH, tx, ty+charH);
    endShape();
  }
}

void drawChar(int charIndex, int charCoordx, int charCoordy){
    int x = charIndex%9;
    int y = charIndex/9;
    int tx = charW*x;
    int ty = charH*y;
    int qx = quadW * charCoordx;
    int qy = quadH * charCoordy;
    beginShape();
    shader(defShader);
    texture(img);
    vertex(qx, qy, tx, ty);
    vertex(qx + quadW, qy, tx+charW, ty);
    vertex(qx + quadW, qy + quadH, tx+charW, ty+charH);
    vertex(qx, qy + quadH, tx, ty+charH);
    endShape();
}
