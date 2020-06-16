package com.supermegadinamita;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class ImageProcessor {

    PApplet p;
    PImage raw;
    boolean border = false;
    boolean sharpen = true;
    boolean invert = true;


    public ImageProcessor(PApplet parent) {
        this.p = parent;
    }


    public void load (PImage img){
        this.raw = img;

    }

    void updateType(String s){

        switch (s){
            case "border":

                border = true;
                invert = false;
                sharpen = false;
                break;

            case "invert":

                border = false;
                invert = true;
                sharpen = false;
                break;

            case "sharpen":

                border = false;
                invert = false;
                sharpen = true;
                break;

        }


    }



    public PImage process(int w, int h){

        //PImage filtered = adjusted(raw,w,h);
        PImage filtered = raw.copy();

        if(sharpen) {
            filtered.filter(p.THRESHOLD, (float) 0.75);
        }

        if(invert) filtered.filter(p.INVERT);
        if(border){
            float[][] matrix = { {  -1, -1,  -1 },
                                 { -1,  9, -1 },
                                 {  -1, -1,  -1 } };
            filtered = convolute(filtered,matrix);
        }

        filtered = adjusted(filtered, w, h);



        return adjusted(filtered, w, h);



    }

    private int mirror(int a, int N){
        if(a < 0) return -a - 1;
        else if(a > N - 1) return N + N - 1 - a;
        else return a;
    }


    PImage convolute(PImage img, float[][] matrix){



        int w = img.width;
        int h = img.height;
        PImage new_img = p.createImage(w,h, p.RGB);
        int kS = matrix.length;
        int k = (kS - 1) / 2;


        for (int cx = 0; cx < w; cx ++){
            for (int cy = 0; cy < h; cy ++){

                int R = 0;
                int G = 0;
                int B = 0;

                for( int j = -k; j<= k ; j++){

                    int sR = 0;
                    int sG = 0;
                    int sB = 0;

                    int ny = mirror(cy + j, h);

                    for( int i = -k; i<= k ; i++){
                        int nx = mirror(cx + i, w);
                        //sR+= (uint8_t) *(img + channels*( nx + ny*w ));
                        sR+= p.red(img.pixels[nx + ny*w]) * matrix[i + k ][j + k];
                        sG+= p.green(img.pixels[nx + ny*w]) * matrix[i + k][j + k];
                        sB+= p.blue(img.pixels[nx + ny*w]) * matrix[i + k][j + k];

                    }
                    R+= sR;
                    G+= sG;
                    B+= sB;
                }
                R/=kS;
                G/=kS;
                B/=kS;
                new_img.pixels[cx +cy*w] = p.color(R,G,B);
            }
        }

        return new_img;
    }




    PImage adjusted(PImage img, double w, double h){

        float sw, sh;

        if ( (double) img.width / (double) img.height > w / h)
        {
            sw = (float) w;
            sh = sw * img.height / img.width;
        }
        else
        {
            sh = (float) h;
            sw = sh * img.width / img.height;
        }

        PGraphics pg = p.createGraphics((int) w, (int) h);
        pg.beginDraw();

        pg.imageMode(p.CENTER);
        pg.image(img, (float) w / 2, (float) h / 2, sw, sh);
        pg.endDraw();

        return pg.get(0,0 ,(int) w,(int) h);

    }

    public void drawHistogram(float xo, float yo, float w, float h) {

        int[] hist = new int[256];

        // Calculate the histogram

        for (int i = 0; i < raw.width; i++) {
            for (int j = 0; j < raw.height; j++) {
                int bright = (int)(p.brightness(raw.get(i, j)));
                hist[bright]++;
            }
        }
        int histMax = PApplet.max(hist);
        p.rectMode(p.CORNERS);
        p.noStroke();
        p.fill(255);
        // Draw half of the histogram (skip every second value)
        for (int i = 0; i < raw.width; i += 2) {
            // Map i (from 0..img.width) to a location in the histogram (0..255)
            int which = (int)(PApplet.map(i, 0, raw.width, 0, 255));
            // Convert the histogram value to a location between
            // the bottom and the top of the picture
            int y = (int)(PApplet.map(hist[which], 0,histMax, yo + h, yo));

            p.rect(xo + i* (w/ raw.width), yo + h, xo + (i+1) *( w/ raw.width), y);
        }

    }

}
