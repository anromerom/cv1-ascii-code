
import nub.primitives.*;
import nub.core.*;
import nub.processing.*;

Scene scene;
Node obj,light;
PShader waterShader;
PGraphics buffer;
PImage normalMap;

int w = 1000;
int h = 700;

//SHADER PARAMS
PVector tint = new PVector(0,0,255);
float fillAmount = 0.2;
PVector topColor = new PVector(10,10,10);
PVector foamLineColor = new PVector(205,134,210);
float foamLineWidth = 0.1;
PVector rimColor = new PVector(194,194,194);
float rimPower = 1;

//WOOBLE PARAMS
float maxWobble = 0.03;
float wobbleSpeed = 1;
float recovery = 1;

float wobbleToAddX;
float wobbleToAddZ;
float pulse;
float time = 0.01;
float prevTime = 0;
float deltaTime;

//Uniform variables for shaders
float wobbleAmountX;
float wobbleAmountZ;
Vector viewDir;
//Velocity
Vector velocity,angularVelocity,lastPos,lastRot;



void settings() {
  size(w, h, P3D);
}

void setup(){
  waterShader = loadShader("frag.glsl","vert.glsl");
  waterShader.set("_FillAmount",fillAmount);
  waterShader.set("_Tint",tint);
  waterShader.set("_TopColor",topColor);
  waterShader.set("_FoamColor",foamLineColor);
  waterShader.set("_Rim",foamLineWidth);
  waterShader.set("_RimColor",rimColor);
  waterShader.set("_RimPower",rimPower);
  shader(waterShader);
  
  scene = new Scene(this);
  scene.togglePerspective();
  scene.setRadius(max(w, h) / 3);
  scene.fit(1);
  //Init sphere
  obj = new Node() {
    @Override
    public void graphics(PGraphics pg){
      pg.pushMatrix();
      pg.fill(0xff222222);
      pg.sphere(50);
      pg.popMatrix();
    }
  };
  
  //Value initialization
  velocity = new Vector();
  angularVelocity = new Vector();
  lastPos = new Vector();
  lastRot = new Vector();
  viewDir = new Vector();
  
  TimingTask task = new TimingTask(){
    @Override
    public void execute(){
      prevTime = time;
      time = millis()/1000.0;  //NECESARIO EN SEGUNDOS? ---TO DO---   ---TO DO---  ---TO DO--- 
      //time = millis();
      deltaTime = time-prevTime;
      //Reducir ondulacion con el tiempo
      wobbleToAddX = lerp(wobbleToAddX,0,deltaTime*recovery);
      wobbleToAddZ = lerp(wobbleToAddZ,0,deltaTime*recovery);
      //Hacer una onda sinusoidal de la ondulacion
      pulse = TWO_PI * wobbleSpeed;
      wobbleAmountX = wobbleToAddX * sin(pulse*time);
      wobbleAmountZ = wobbleToAddZ * sin(pulse*time);
      viewDir = Vector.subtract(obj.position(),scene.eye().position());
      //Velocidad
      velocity = Vector.multiply( Vector.subtract(lastPos,obj.position() ) , 1/deltaTime); 
      angularVelocity = Vector.subtract(obj.orientation().eulerAngles(),lastRot);
      //Agregar velocidad restringida a la ondulación
      wobbleToAddX = clamp((velocity.x() + (angularVelocity.z() * 0.2))*maxWobble,-maxWobble,maxWobble);
      wobbleToAddZ = clamp((velocity.z() + (angularVelocity.x() * 0.2))*maxWobble,-maxWobble,maxWobble);
      //Mantener posición anterior
      lastPos = obj.position();
      lastRot = obj.orientation().eulerAngles();
    }
  };
  //task.setPeriod(50);
  task.run();
}

void draw(){  
  
  /*prevTime = time;
  time = millis()/1000.0;  //NECESARIO EN SEGUNDOS? ---TO DO---   ---TO DO---  ---TO DO--- 
  //time = millis();
  deltaTime = time-prevTime;
  //Reducir ondulacion con el tiempo
  wobbleToAddX = lerp(wobbleToAddX,0,deltaTime*recovery);
  wobbleToAddZ = lerp(wobbleToAddZ,0,deltaTime*recovery);
  //Hacer una onda sinusoidal de la ondulacion
  pulse = TWO_PI * wobbleSpeed;
  wobbleAmountX = wobbleToAddX * sin(pulse*time);
  wobbleAmountZ = wobbleToAddZ * sin(pulse*time);
  viewDir = Vector.subtract(obj.position(),scene.eye().position());
  */
  
  waterShader.set("_WobbleX",wobbleAmountX);
  waterShader.set("_WobbleZ",wobbleAmountZ);     
  waterShader.set("viewDirProc",viewDir.x(),viewDir.y(),viewDir.z());
  shader(waterShader);
  //println("delta: "+deltaTime);
  //println("AngVel: "+angularVelocity);
  /*
  //Velocidad
  velocity = Vector.multiply( Vector.subtract(lastPos,obj.position() ) , 1/deltaTime); 
  angularVelocity = Vector.subtract(obj.orientation().eulerAngles(),lastRot);
  //Agregar velocidad restringida a la ondulación
  wobbleToAddX = clamp((velocity.x() + (angularVelocity.z() * 0.2))*maxWobble,-maxWobble,maxWobble);
  wobbleToAddZ = clamp((velocity.z() + (angularVelocity.x() * 0.2))*maxWobble,-maxWobble,maxWobble);
  //Mantener posición anterior
  lastPos = obj.position();
  lastRot = obj.orientation().eulerAngles();
  */
  
  background(125);
  scene.render();
}

void mouseMoved() {
  scene.mouseTag();
}

void mouseDragged() {
  if (mouseButton == LEFT)
    scene.mouseSpin();
  else if (mouseButton == RIGHT)
    scene.mouseTranslate();
  else
    scene.moveForward(mouseX - pmouseX);
}

float clamp(float val, float min, float max){
  if(val<=min){
    return min;
  }else if(val>=max){
    return max;
  }
  return val;  
}
