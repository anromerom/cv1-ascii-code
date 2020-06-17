import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.IntDict;

import javax.lang.model.element.UnknownElementException;
import java.rmi.UnexpectedException;

class ASCIIDrawer {
    PApplet p;
    static final int res = 50;
    String[] ordered;
    PFont mono;
    boolean col = false;
    boolean luma = false;
    boolean bw = false;


    IntDict count;
    int N;
    PImage ascii, img;
    private boolean Nchanged = false;


    public ASCIIDrawer(PApplet parent) {
        this.p = parent;
        mono = createFont("Courier New Negrita", (float) width / 50);
        orderChars();
        N = 125;

    }

    public void orderChars(){
        char[] char_list = new char[95];
        for (int i = 32; i < 127; i++) {
            char_list[i - 32] = (char)i;
        }


        count = new IntDict();
        loadPixels();
        textAlign(CENTER, CENTER); textFont(mono); textSize((float)(res*0.65));

        background(0);
        noStroke();

        // Por cada pixel, evaluar su cantidad de pixeles en blanco respecto a los pixeles en negro. Representa su brillo.
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
            PApplet.println(s +":"+ count.get(s));
        }
        ordered  = count.keyArray();
    }




    void createAsciiImage(PImage img){
        //movie.volume(0.001);


        //PImage img = img_o;
        this.img = img;
        int w = img.width;
        int h = img.height;

        PGraphics pg = createGraphics(w, h);
        pg.beginDraw();

        int M =  N *h / w;

        pg.image(img, 0,0, N, M);

        PImage pix = pg.get(0,0,N,M);
        pg.background(bw? 255:0);

        pg.textSize((int) (w * 1.2 / N));

        for(int j = 0; j<M; j++){
            for(int i = 0; i<N; i++){
                if (col) pg.fill(pix.pixels[i+j*pix.width]);
                else    pg.fill( bw ? 0:255);

                pg.textAlign(LEFT, TOP);

                pg.text( mapCharlie(pix.pixels[i+j*pix.width]),(float) i * w / N , (float) j * h / M);

            }
        }
        pg.endDraw();
        ascii =  pg.get(0,0,w,h);

    }

    @Deprecated
    private String mapCharlieOld(int x){
        float value;
        if (luma){

            float r = red(x);
            float g = green(x);
            float b = blue(x);

            value = (float) (0.2126*r + 0.7152*g + 0.0722*b);

        }
        else value = brightness(x);

        return ordered[PApplet.floor(PApplet.map(value,0,255,col? ordered.length -1:0, col? 0:ordered.length -1) )];

    }

    private String mapCharlie(int x){
        float value;
        if (luma){

            float r = red(x);
            float g = green(x);
            float b = blue(x);

            value = (float) (0.2126*r + 0.7152*g + 0.0722*b);

        }
        else value = brightness(x);

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


    public void updateN(int count) {

        N+= count;
        N = N <= 10 ? 10 : (Math.min(N, 200));
        Nchanged = true;

    }

    public PImage asciiImage() {

        if(Nchanged) {
            createAsciiImage(img);
            Nchanged = false;
        }
        return ascii;
    }

    public void updateParam(int i) {

        switch (i) {
            case 1: this.luma = !this.luma; break;
            case 4: this.col = !this.col; break;
            case 6: this.bw = !this.bw; break;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
        createAsciiImage(img);
    }

    public boolean getParam(int i) {
        switch (i) {
            case 1: return this.luma;
            case 4: return this.col;
            case 6: return this.bw;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
    }
}
