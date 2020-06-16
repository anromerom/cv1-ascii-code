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
    boolean luma = true;
    IntDict count;



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
        count = new IntDict();
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



    PImage asciiImage(PImage img){
        //movie.volume(0.001);
        int N = 125;

        //PImage img = img_o;

        int w = img.width;
        int h = img.height;

        PGraphics pg = p.createGraphics(w, h);
        pg.beginDraw();

        int M =  N *h / w;

        pg.image(img, 0,0, N, M);

        PImage pix = pg.get(0,0,N,M);
        pg.background(col? 255:0);

        pg.textSize((int) (w * 1.2 / N));

        for(int j = 0; j<M; j++){
            for(int i = 0; i<N; i++){


                pg.fill(255);

                pg.textAlign(p.LEFT, p.TOP);

                pg.text( mapCharlie(pix.pixels[i+j*pix.width])
                        ,(float) i * w / N , (float) j * h / M);

            }
        }
        pg.endDraw();
        return pg.get(0,0,w,h);

    }

    @Deprecated
    private String mapCharlieOld(int x){
        float value;
        if (luma){

            float r = p.red(x);
            float g = p.green(x);
            float b = p.blue(x);

            value = (float) (0.2126*r + 0.7152*g + 0.0722*b);

        }
        else value = p.brightness(x);

        return ordered[PApplet.floor(PApplet.map(value,0,255,col? ordered.length -1:0, col? 0:ordered.length -1) )];

    }

    private String mapCharlie(int x){
        float value;
        if (luma){

            float r = p.red(x);
            float g = p.green(x);
            float b = p.blue(x);

            value = (float) (0.2126*r + 0.7152*g + 0.0722*b);

        }
        else value = p.brightness(x);

        String index = ordered[nearest(value)];


        return ordered[nearest(value)];

    }

    //Tomado de https://stackoverflow.com/questions/30245166/find-the-nearest-closest-value-in-a-sorted-list
    public int nearest(float value) {
        count.sortValues();
        int[] a = count.valueArray();

        if(value < a[0]) {
            return 0;
        }
        if(value > a[a.length-1]) {
            return a.length-1;
        }

        int lo = 0;
        int hi = a.length - 1;

        while (lo <= hi) {
            int mid = (hi + lo) / 2;

            if (value < a[mid]) {
                hi = mid - 1;
            } else if (value > a[mid]) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        // lo == hi + 1
        return (a[lo] - value) < (value - a[hi]) ? lo : hi;
    }



}
