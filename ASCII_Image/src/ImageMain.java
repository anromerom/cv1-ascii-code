import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.io.File;


public class ImageMain extends PApplet {


    PImage download, img;
    PFont ancizar;
    ASCIIDrawer drawer;
    ImageProcessor preliminar;
    File[] files;

    PImage[] imgs;

    int curImage = 0;
    static String title = "Original";

    public void settings() {

        size(1600, 800);
    }



    public void setup() {

        ancizar = createFont("AncizarSans-Regular_02042016.otf", 32);

        drawer = new ASCIIDrawer(this);
        preliminar = new ImageProcessor(this, drawer);

        files = listFiles(sketchPath("data/image"));

        imgs = new PImage[files.length];
        for (int i = 0; i < files.length; i++) {
            imgs[i] = loadImage(files[i].toString());
        }

        preliminar.load(imgs[0], (int) (0.45 * width), (int) (0.55 * height));
        img = preliminar.getProcessed();
        drawer.createAsciiImage(img);
        download = loadImage("data/download.png");


    }


    public void keyPressed() {

        if (key == ' ') {

            curImage = (curImage + 1) % files.length;
            preliminar.load(imgs[curImage], (int) (0.45 * width), (int) (0.55 * height));

        }
        if (key == CODED) {
            if (keyCode == UP) {
                preliminar.upFilter();
            } else if (keyCode == DOWN) {
                preliminar.downFilter();
            }
        }


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
        fill(255);

        rectMode(CORNERS);

        text(title, (int) (0.02 * w), 0, (int) (w * 0.45), (int) (h * 0.075));
        text("ASCII", (int) (0.52 * w), 0, (int) (w * 0.95), (int) (h * 0.075));


        fill(0);
        noStroke();
        rectMode(CORNER);
        rect(0.024f * w, 0.0749f * h, 0.452f * w, 0.552f * h);
        rect(0.524f * w, 0.0749f * h, 0.452f * w, 0.552f * h);


        image(preliminar.getProcessed(), 0.025f * w, 0.075f * h, 0.45f * w, 0.55f * h);
        image(drawer.asciiImage(), 0.525f * w, 0.075f * h, 0.45f * w, 0.55f * h);

        preliminar.drawHistogram(0.25f * w, 0.65f * h, 0.5f * w, 0.35f * h);

        fill(255, 0, 0);
        drawButtons();
        text(frameRate, mouseX, mouseY);


    }


    public void drawButtons() {
        int w = width, h = height;
        rectMode(CORNER);
        textFont(ancizar, 18);

        boolean[] st = btnStates();

        fill(st[0] ? color(151, 194, 231) : color(68, 146, 212));
        rect(0.025f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
        fill(33);
        text("Luma", 0.025f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
        fill(st[1] ? color(151, 194, 231) : color(68, 146, 212));
        rect(0.095f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
        fill(33);
        text("Threshold", 0.095f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
        fill(st[2] ? color(151, 194, 231) : color(68, 146, 212));
        rect(0.165f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
        fill(33);
        text("Inverse", 0.165f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
        fill(st[3] ? color(151, 194, 231) : color(68, 146, 212));
        rect(0.025f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
        fill(33);
        text("Convolution", 0.025f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
        fill(st[4] ? color(151, 194, 231) : color(68, 146, 212));
        rect(0.095f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
        fill(33);
        text("Color", 0.095f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
        fill(st[5] ? color(151, 194, 231) : color(68, 146, 212));
        rect(0.165f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
        fill(33);
        text("Background", 0.165f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);

        imageMode(CENTER);
        image(download, w * 0.5f, h * 0.65f, 0.035f * w, 0.035f * w);


        noStroke();
        fill(255, 70);
        switch (checkButton(mouseX, mouseY)) {
            case 1:
                rect(0.025f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
                break;
            case 3:
                rect(0.095f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
                break;
            case 5:
                rect(0.165f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
                break;
            case 2:
                rect(0.025f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
                break;
            case 4:
                rect(0.095f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
                break;
            case 6:
                rect(0.165f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
                break;
            case 7:
                image(download, w * 0.5f, h * 0.65f, 0.04f * w, 0.04f * w);
                break;
        }

        imageMode(CORNER);

    }


    boolean[] btnStates() {


        return new boolean[]{
                drawer.getParam(1),
                preliminar.getParam(3),
                preliminar.getParam(5),
                preliminar.getParam(2),
                drawer.getParam(4),
                drawer.getParam(6)
        };
    }


    int checkButton(int x, int y) {

        int w = width, h = height;
        //ellipse(w*0.5f, h* 0.65f, 0.0175f*w,0.0175f*w );


        if ((x - w * 0.5f) * (x - w * 0.5f) + (y - h * 0.65f) * (y - h * 0.65f) < 0.02f * w * 0.02f * w) {
            return 7;
        }


        boolean row;

        if (y >= 0.85f * h && y <= (0.83f + 0.0625f) * h) { //Fila 1
            row = true;
        } else if (y >= 0.92f * h && y <= (0.92f + 0.0625f) * h) {
            row = false;
        } else return -1;


        if (x >= 0.025f * w && x <= (0.025f + 0.0625f) * w) { //Fila 1
            if (row) return 1;
            else return 2;

        } else if (x >= 0.095f * w && x <= (0.095f + 0.0625f) * w) {
            if (row) return 3;
            else return 4;
        } else if (x >= 0.165f * w && x <= (0.165f + 0.0625f) * w) {
            if (row) return 5;
            else return 6;
        }


        return -1;

    }


    @Override
    public void mouseWheel(MouseEvent event) {
        super.mouseWheel(event);

        drawer.updateN(event.getCount());


    }

    @Override
    public void mousePressed() {
        super.mousePressed();

        println(checkButton(mouseX, mouseY));
        switch (checkButton(mouseX, mouseY)) {
            case 1:
                drawer.updateParam(1);
                preliminar.updateParam(1);
                break; // Luma
            case 3:
                preliminar.updateParam(3);
                break;// Threshold
            case 5:
                preliminar.updateParam(5);
                break; // Inverse
            case 2:
                preliminar.updateParam(2);
                break; // Convolution
            case 4:
                drawer.updateParam(4);
                break; // Color
            case 6:
                drawer.updateParam(6);
                break; // Black and White
            case 7:
                download();
                //
        }
    }

    private void download() {

        println("Download");
        PImage im = drawer.asciiImage();
        PGraphics pg = createGraphics(im.width, im.height);
        pg.beginDraw();
        pg.image(im, 0, 0);
        pg.endDraw();

        pg.save("ascii-output.png");

    }


    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"ImageMain"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

}
