package com.supermegadinamita;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.IntDict;

public class ASCIIDrawer {
    PApplet p;
    static final int res = 50;
    String[] ordered;
    PFont mono;
    boolean col = false;

    public ASCIIDrawer(PApplet parent) {
        this.p = parent;
        mono = p.createFont("Courier New Negrita", (float) p.width / 50);
        orderChars();
    }

    public void orderChars(){
        char[] char_list = new char[95];
        for (int i = 32; i < 127; i++) {
            char_list[i - 32] = (char)i;
        }

        PApplet.printArray(char_list);
        IntDict count = new IntDict();
        p.loadPixels();
        p.textAlign(p.CENTER, p.CENTER); p.textFont(mono); p.textSize((float)(res*0.65));

        p.background(0);
        p.noStroke();

        // Por cada pixel, evaluar su cantidad de pixeles en blanco respecto a los pixeles en negro. Representa su brillo.
        outerloop:
        for(int j = 0; j<10; j++){
            for(int i = 0; i<10; i++){
                if(i + j*10 > char_list.length - 1) break outerloop;
                p.fill(255);
                p.textAlign(p.CENTER, p.CENTER);
                p.text(char_list[i + j*10]+"", i*res, j*res,res,res);

                PImage area = p.get(i*res, j*res, res, res);
                count.set(char_list[i + j*10]+"", 0);
                for(int y = 0; y< area.height; y++){
                    for(int x = 0; x<area.width; x++){
                        if(p.brightness(area.pixels[x+y*area.width]) > 0){
                            count.increment(char_list[i + j*10]+"");
                        }
                    }
                }
            }
        }
        count.sortValues();
        for(String s : count.keyArray()){
            PApplet.println(s +":"+ count.get(s));
        }
        ordered  = count.keyArray();
    }

    PImage adjusted(PImage img, int w, int h){

        int sw, sh;

        if (img.width / img.height > w / h)
        {
            sw = w;
            sh = sw * img.height / img.width;
        }
        else
        {
            sh = h;
            sw = sh * img.width / img.height;
        }

        PApplet.println(sw, sh);
        PGraphics pg = p.createGraphics(w, h);
        pg.beginDraw();

        pg.imageMode(p.CENTER);
        pg.image(img, (float) w / 2, (float) h / 2, sw, sh);
        pg.endDraw();

        return pg.get(0,0 ,w, h);
    }

    PImage asciiImage(PImage img_o, int w, int h){
        //movie.volume(0.001);
        int N = 100;

        PImage img = adjusted(img_o, w, h);
        //PImage img = img_o;
        PApplet.print(img.width  == w);
        PApplet.print(img.height == h);

        PGraphics pg = p.createGraphics(w, h);
        pg.beginDraw();

        int M =  N *p.height / p.width;

        pg.image(img, 0,0, N, M);

        PImage pix = pg.get(0,0,N,M);
        pg.background(col? 255:0);
        pg.textSize((int) (p.width * 1.2 / N));

        for(int j = 0; j<M; j++){
            for(int i = 0; i<N; i++){


                pg.fill(255);

                pg.textAlign(p.LEFT, p.TOP);
                pg.text(ordered[PApplet.floor(PApplet.map(p.brightness(pix.pixels[i+j*pix.width]) ,0,255,col? ordered.length -1:0, col? 0:ordered.length -1))]
                        ,(float) i * w / N , (float) j * h / M);

            }
        }
        pg.endDraw();
        return pg.get(0,0,w,h);

    }
}
