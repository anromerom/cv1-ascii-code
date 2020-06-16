package com.supermegadinamita;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.IntDict;

public class Main extends PApplet {


    PImage test;

    PFont  ancizar;

    boolean mode = true;
    boolean col = false;
    ASCIIDrawer drawer;



    @Override
    public void settings() {
        super.settings();
        size(600,1000);
    }


    @Override
    public void setup() {
        super.setup();

        ancizar = createFont("AncizarSans-Regular_02042016.otf", 32);

        drawer = new ASCIIDrawer(this);




        test = loadImage("data/jojo.jpg");

    }

    @Override
    public void draw() {
        super.draw();
        //asciiImage(test, 500, 500);
        background(0);
        //fill(255,0,0);
        rect(0,0,500,500);
        //image(adjusted(test, width, height), 0, 0);
        image(drawer.asciiImage(test, width, height),0,0);


    }




    public static void main(String[] args) {
        PApplet.main("com.supermegadinamita.Main");
    }
}
