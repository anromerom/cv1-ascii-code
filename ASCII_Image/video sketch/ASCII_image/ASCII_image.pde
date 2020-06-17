import processing.video.*;
import java.util.*;

char[] char_list;
int N = 100;
IntDict count;
String[] ordered;
PFont mono;
PImage test;
int res = 50;
boolean mode = true;
boolean col = false;
PImage[] imgs;
int d= 1;

Movie movie;
int index = 0;
void setup(){
  
  fullScreen();
 //size(1000,1200);
 
  print(PFont.list());
  mono = createFont("Courier New Negrita", width/50);
  char_list = new char[95];
  for (int i = 32; i < 127; i++) {
    char_list[i - 32] = (char)i;
  }
  count = new IntDict();
  printArray(char_list);
    
  loadPixels();
  textAlign(CENTER, CENTER); textFont(mono); textSize(res*0.65);
  
  background(0); 
  noStroke();
  outerloop:
  for(int j = 0; j<10; j++){
    for(int i = 0; i<10; i++){
      if(i + j*10 > char_list.length - 1) break outerloop;      
        fill(255);
        textAlign(CENTER, CENTER);
        text(char_list[i + j*10]+"", i*res, j*res,res,res); 
            
        PImage area = get(i*res, j*res, res, res);
        count.set(char_list[i + j*10]+"", 0);        
        for(int y = 0; y< area.height; y++){
          for(int x = 0; x<area.width; x++){            
              if(brightness(area.pixels[x+y*area.width]) > 0){
                count.increment(char_list[i + j*10]+"");
              }              
          }
        }
      }  
  }
  count.sortValues();
  for(String s : count.keyArray()){
    println(s +":"+ count.get(s)); 
  }
  ordered  = count.keyArray();
  test = loadImage("abues.jpeg");
  //movie = new Movie(this, "breakdown.mp4");
  //movie.loop();
   File[] files = listFiles(sketchPath("data/photos"));
   imgs = new PImage[files.length];
   for (int i = 0; i< files.length; i++){ 
    imgs[i] = adjusted(loadImage(files[i].toString()));
   }

}


PImage adjusted(PImage img){

  PGraphics pg = createGraphics(width,height);

  pg.beginDraw();
  int w, h;
  
  if (img.width / img.height > width / height){

    w = img.width * width/height;
    h = height;
  }
  else {
    
    
        w = width;
    h = img.height * width/height;
  }
  println(w,h);
  pg.imageMode(CENTER);
  pg.image(img, width/2, height/2, w, h);
  pg.endDraw();

  
  return pg.get(0,0,width, height);
}

void movieEvent(Movie m) {
  m.read();
}


void draw(){
  
  asciiImage(imgs[index]);
 
}

// Function to list all the files in a directory
File[] listFiles(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    File[] files = file.listFiles();
    return files;
  } else {
    // If it's not a directory
    return null;
  }
}
PImage asciiImage(PImage img){
  //movie.volume(0.001);
  int M = (int)N *height/width;
  image(img, 0,0, N, M);
  
  PImage pix = get(0,0,N,M);
  background(col? 255:0);
  textSize(width*1.2/N);
  for(int j = 0; j<M; j++){
    for(int i = 0; i<N; i++){
       
        if(mode) fill(pix.pixels[i+j*pix.width]);
        else fill(col? 0:255); 
        textAlign(LEFT, TOP);
        text(ordered[floor(map(brightness(pix.pixels[i+j*pix.width]) ,0,255,col? ordered.length -1:0, col? 0:ordered.length -1))] 
        ,i*width/N , j*height/M);
      
    } 
  }

  return get(0,0,width,height);

} 


void keyPressed(){
  
  
  if (key == '+' && N <= width) N = (N + 2);
  else if(key == '-' && N >= 1) N = (N - 2);
  else if(key == 'm') mode = !mode;
  else if(key == 'g') col = !col;
  else if(key == 's') save("lau.png");
  else if(key == 'q') index = (index + 1) % imgs.length;
}
