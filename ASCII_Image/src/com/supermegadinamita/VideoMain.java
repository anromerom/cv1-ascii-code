package com.supermegadinamita;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.video.Movie;


public class VideoMain extends PApplet {


    PImage test;
    PFont  ancizar;
    ASCIIDrawer drawer;
    ImageProcessor preliminar;
    Movie movie;


    public void settings() {

        size(1600,900);
    }


    public void setup() {


        ancizar = createFont("AncizarSans-Regular_02042016.otf", 32);

        drawer = new ASCIIDrawer(this);
        preliminar = new ImageProcessor(this);

        test = loadImage("data/DIO.png");


        movie = new Movie(this, "data/breakdown.mp4");
        movie.loop();



    }


    public void draw() {

        float w = width;
        float h = height;
        //asciiImage(test, 500, 500);
        background(12);
        //fill(255,0,0);
        //image(adjusted(test, width, height), 0, 0);
        //image(drawer.asciiImage(test, width, height),0,0);


        textFont(ancizar, 50);
        stroke(255);
        text("Original", (int) (0.02*w), 0, (int)  (w*0.45), (int) (h*0.075));
        text("ASCII", (int) (0.52*w), 0, (int)  (w*0.95), (int) (h*0.075));



        fill(0);
        noStroke();
        rectMode(CORNER);
        rect((float) 0.024 *w,(float) 0.0749 * h, (float) 0.452 * w, (float) 0.552 * h );
        rect((float) 0.524 *w,(float) 0.0749 * h, (float) 0.452 * w, (float) 0.552 * h );

        preliminar.load(movie);
        PImage img = preliminar.process( (int)(0.45 * w), (int) (0.55 * h));

        image(   img                  , (float) 0.025 * w,  (float) 0.075 * h, (float) 0.45 * w, (float) 0.55 * h );
        image( drawer.asciiImage(img), (float) 0.525 * w,  (float) 0.075 * h, (float) 0.45 * w, (float) 0.55 * h );

        preliminar.drawHistogram((float) 0.25*w,(float) 0.65*h, (float)  0.5 * w, (float) 0.35* h);




    }



    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "com.supermegadinamita.VideoMain" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

}
