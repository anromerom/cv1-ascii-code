import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.event.MouseEvent;


public class ImageMain extends PApplet {


    PImage test, img;
    PFont  ancizar;
    ASCIIDrawer drawer;
    ImageProcessor preliminar;




    public void settings() {

        size(1600,900);
    }


    public void setup() {


        ancizar = createFont("AncizarSans-Regular_02042016.otf", 32);

        drawer = new ASCIIDrawer(this);
        preliminar = new ImageProcessor(this);

        test = loadImage("data/DIO.png");
        preliminar.load(test);
        img = preliminar.process( (int)(0.45 * width), (int) (0.55 * height));
        drawer.createAsciiImage(img);
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
        text("Original", (int) (0.02*w), 0, (int)  (w*0.45), (int) (h*0.075));
        text("ASCII", (int) (0.52*w), 0, (int)  (w*0.95), (int) (h*0.075));

        fill(0);
        noStroke();
        rectMode(CORNER);
        rect( 0.024f *w, 0.0749f * h,  0.452f * w,  0.552f * h );
        rect( 0.524f *w, 0.0749f * h,  0.452f * w,  0.552f * h );





        image(   img                  ,  0.025f * w,   0.075f * h,  0.45f * w, 0.55f * h );
        image( drawer.asciiImage(),  0.525f * w,   0.075f * h,  0.45f * w,  0.55f * h );

        preliminar.drawHistogram(0.25f*w,0.65f*h,  0.5f * w, 0.35f* h);

        fill(255,0,0);

        drawButtons();

        text(frameRate, mouseX, mouseY);


    }


    public void drawButtons(){
        int w =  width, h = height;
        rectMode(CORNER);
        fill(68, 146, 212); rect(0.025f * w ,0.83f* h, w*0.0625f, h*0.0625f);
        fill(68, 146, 212); rect(0.095f * w ,0.83f* h, w*0.0625f, h*0.0625f);
        fill(68, 146, 212); rect(0.165f * w ,0.83f* h, w*0.0625f, h*0.0625f);

        fill(68, 146, 212); rect(0.025f * w ,0.92f* h, w*0.0625f, h*0.0625f);
        fill(68, 146, 212); rect(0.095f * w ,0.92f* h, w*0.0625f, h*0.0625f);
        fill(68, 146, 212); rect(0.165f * w ,0.92f* h, w*0.0625f, h*0.0625f);

        fill(255, 100);
        switch (checkButton(mouseX, mouseY)) {
            case 1 -> rect(0.025f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
            case 3 -> rect(0.095f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
            case 5 -> rect(0.165f * w, 0.83f * h, w * 0.0625f, h * 0.0625f);
            case 2 -> rect(0.025f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
            case 4 -> rect(0.095f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
            case 6 -> rect(0.165f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
        }


    }


    int checkButton(int x, int y){

        int w =  width, h = height;

        boolean row;

        if(y >= 0.85f*h && y <= (0.83f +0.0625f)*h){ //Fila 1
            row = true;
        }
        else if (y >= 0.92f*h && y <= (0.92f +0.0625f)*h) {
            row = false;
        }
        else return - 1;



        if(x >= 0.025f * w && x <= (0.025f +0.0625f)*w){ //Fila 1
            if(row) return 1;
            else return 2;

        }
        else if (x >= 0.095f * w && x <= (0.095f +0.0625f)*w) {
            if(row) return 3;
            else return 4;
        }
        else if (x >= 0.165f * w && x <= (0.165f +0.0625f)*w) {
            if(row) return 5;
            else return 6;
        }


        return - 1;

    }


    @Override
    public void mouseWheel(MouseEvent event) {
        super.mouseWheel(event);

        print("Wheel");
        drawer.updateN(event.getCount());


    }

    @Override
    public void mousePressed() {
        super.mousePressed();

        print("Mouse");


        /*switch (checkButton(mouseX, mouseY)) {
            case 1 -> rect(0.025f * w, 0.85f * h, w * 0.0625f, h * 0.0625f);
            case 3 -> rect(0.095f * w, 0.85f * h, w * 0.0625f, h * 0.0625f);
            case 5 -> rect(0.165f * w, 0.85f * h, w * 0.0625f, h * 0.0625f);
            case 2 -> rect(0.025f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
            case 4 -> rect(0.095f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
            case 6 -> rect(0.165f * w, 0.92f * h, w * 0.0625f, h * 0.0625f);
        }*/
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "ImageMain" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

}
