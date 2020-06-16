PShape can;
float angle;
PShader ConvShader;
PShader defShader;
PImage img;

float[][] convMat = { {  -1, -1, -1 },
                      {  -1, 8, -1 },
                      {  -1, -1, -1 } };
                      
int weight;
                      
PMatrix3D convM;
                      
void setup() {
  size(1064, 518, P3D);
  ConvShader = loadShader("Convfrag.glsl", "vert.glsl");
  defShader = loadShader("normFrag.glsl");
  img = loadImage("perro.JPG");
  
  convM = new PMatrix3D(convMat[0][0],convMat[0][1],convMat[0][2],0,convMat[1][0],convMat[1][1],convMat[1][2],0,convMat[2][0],convMat[2][1],convMat[2][2],0,0,0,0,0);
  weight = 0;
  for(int i=0;i<3;i++){
    for(int j=0;j<3;j++){
      weight += (int)convMat[i][j];
    }
  }
  if(weight<=0){
    weight = 1;
  }
  print(weight);
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
  ConvShader.set("convMat", convM,true);
  ConvShader.set("weight",weight);
  shader(ConvShader); 
  texture(img);
  vertex(img.width, 0, 0, 0);
  vertex(img.width*2, 0, img.width, 0);
  vertex(img.width*2, img.height, img.width, img.height);
  vertex(img.width, img.height, 0, img.height);
  endShape();  
}
