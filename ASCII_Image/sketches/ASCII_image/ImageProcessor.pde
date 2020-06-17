import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

class ImageProcessor {

    private final ASCIIDrawer drawer;
    PApplet p;
    PImage raw;
    boolean convolute = false;
    boolean threshold = false;
    boolean invert = false;
    boolean luma = false;
    boolean gray = false;
    boolean blur = false;
    int[] hist;
    PImage processed;
    private int currentIndexFilter = 0;
    private int numFilters = 6;
    private int w;
    private int h;

    public ImageProcessor(PApplet parent, ASCIIDrawer drawer) {
        this.p = parent;
        this.drawer = drawer;
    }


    public void load(PImage img, int w, int h) {
        this.raw = img;
        constructHistogram();
        this.w = w;
        this.h = h;
        process();
    }


    public PImage getProcessed() {
        return processed;
    }

    private void process() {

        //PImage filtered = adjusted(raw,w,h);
        PImage filtered = raw.copy();
        if (luma) {
            filtered = luma(filtered);
        }
        if (gray) {
            filtered = grayByAverage(filtered);
        }
        if (threshold) {
            filtered.filter(THRESHOLD, (float) 0.75);
        }

        if (invert) filtered = inverse(filtered);
        if (convolute) {
            float[][] matrix = {{-1, -1, -1},
                    {-1, 8, -1},
                    {-1, -1, -1}};
            filtered = convolute(filtered, matrix);
        }
        if (blur) {
            float[][] matrix = {{(1.0F / 3), (1.0F / 3), (1.0F / 3)},
                    {(1.0F / 3), (1.0F / 3), (1.0F / 3)},
                    {(1.0F / 3), (1.0F / 3), (1.0F / 3)}};
            filtered = convolute(filtered, matrix);
        }

        filtered = adjusted(filtered, w, h);


        processed = adjusted(filtered, w, h);
        drawer.createAsciiImage(processed);

    }

    private int mirror(int a, int N) {
        if (a < 0) return -a - 1;
        else if (a > N - 1) return N + N - 1 - a;
        else return a;
    }

    PImage luma(PImage img) {
        int w = img.width;
        int h = img.height;
        PImage new_img = createImage(w, h, RGB);
        img.loadPixels();
        int r, g, b;
        for (int i = 0; i < img.width * img.height; i++) {
            r = (int) red(img.pixels[i]);
            g = (int) green(img.pixels[i]);
            b = (int) blue(img.pixels[i]);
            int val = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
            new_img.pixels[i] = color(val);
        }
        return new_img;
    }

    PImage grayByAverage(PImage img) {
        int w = img.width;
        int h = img.height;
        PImage new_img = createImage(w, h, RGB);
        img.loadPixels();
        int r, g, b;
        for (int i = 0; i < img.width * img.height; i++) {
            r = (int) red(img.pixels[i]);
            g = (int) green(img.pixels[i]);
            b = (int) blue(img.pixels[i]);
            int val = (r + g + b) / 3;
            new_img.pixels[i] = color(val);
        }
        return new_img;
    }

    PImage inverse(PImage img) {
        int w = img.width;
        int h = img.height;
        PImage new_img = createImage(w, h, RGB);
        img.loadPixels();
        int r, g, b;
        for (int i = 0; i < img.width * img.height; i++) {
            r = 255 - (int) red(img.pixels[i]);
            g = 255 - (int) green(img.pixels[i]);
            b = 255 - (int) blue(img.pixels[i]);
            new_img.pixels[i] = color(r, g, b);
        }
        return new_img;
    }

    PImage convolute(PImage img, float[][] matrix) {


        int w = img.width;
        int h = img.height;
        PImage new_img = createImage(w, h, RGB);
        int kS = matrix.length;
        int k = (kS - 1) / 2;


        for (int cx = 0; cx < w; cx++) {
            for (int cy = 0; cy < h; cy++) {

                int R = 0;
                int G = 0;
                int B = 0;

                for (int j = -k; j <= k; j++) {

                    int sR = 0;
                    int sG = 0;
                    int sB = 0;

                    int ny = mirror(cy + j, h);

                    for (int i = -k; i <= k; i++) {
                        int nx = mirror(cx + i, w);
                        //sR+= (uint8_t) *(img + channels*( nx + ny*w ));
                        sR += red(img.pixels[nx + ny * w]) * matrix[i + k][j + k];
                        sG += green(img.pixels[nx + ny * w]) * matrix[i + k][j + k];
                        sB += blue(img.pixels[nx + ny * w]) * matrix[i + k][j + k];

                    }
                    R += sR;
                    G += sG;
                    B += sB;
                }
                R /= kS;
                G /= kS;
                B /= kS;
                new_img.pixels[cx + cy * w] = color(R, G, B);
            }
        }

        return new_img;
    }


    public PImage adjusted(PImage img, double w, double h) {

        float sw, sh;

        if ((double) img.width / (double) img.height > w / h) {
            sw = (float) w;
            sh = sw * img.height / img.width;
        } else {
            sh = (float) h;
            sw = sh * img.width / img.height;
        }

        PGraphics pg = createGraphics((int) w, (int) h);
        pg.beginDraw();

        pg.imageMode(CENTER);
        pg.image(img, (float) w / 2, (float) h / 2, sw, sh);
        pg.endDraw();

        return pg.get(0, 0, (int) w, (int) h);

    }


    public void constructHistogram() {
        hist = new int[256];
        for (int i = 0; i < raw.width; i++) {
            for (int j = 0; j < raw.height; j++) {
                int bright = (int) (brightness(raw.get(i, j)));
                hist[bright]++;
            }
        }
    }

    public void drawHistogram(float xo, float yo, float w, float h) {


        // Calculate the histogram


        int histMax = PApplet.max(hist);
        rectMode(CORNERS);
        noStroke();
        fill(255);
        // Draw half of the histogram (skip every second value)
        for (int i = 0; i < raw.width; i += 2) {
            // Map i (from 0..img.width) to a location in the histogram (0..255)
            int which = (int) (PApplet.map(i, 0, raw.width, 0, 255));
            // Convert the histogram value to a location between
            // the bottom and the top of the picture
            int y = (int) (PApplet.map(hist[which], 0, histMax, yo + h, yo));

            rect(xo + i * (w / raw.width), yo + h, xo + (i + 1) * (w / raw.width), y);
        }
    }

    public void updateParam(int i) {
        switch (i) {
            case 1:
                this.luma = !this.luma;
                break;
            case 2:
                this.convolute = !this.convolute;
                break;
            case 3:
                this.threshold = !this.threshold;
                break;
            case 5:
                this.invert = !this.invert;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
        process();

    }

    public void updateFilterIndex() {
        System.out.println(this.currentIndexFilter);
        switch (this.currentIndexFilter) {
            case 0:
                title = "Original";
                this.luma = false;
                this.gray = false;
                this.invert = false;
                this.convolute = false;
                this.blur = false;
                break;
            case 1:
            title = "Luma";
                this.luma = true;
                this.gray = false;
                this.invert = false;
                break;
            case 2:
            title = "Gray";
                this.gray = true;
                this.luma = false;
                this.invert = false;
                break;
            case 3:
            title = "Invert";
                this.invert = true;
                this.gray = false;
                this.convolute = false;
                break;
            case 4:
            title = "Edge detection";
                this.invert = false;
                this.convolute = true;
                this.blur = false;
                break;
            case 5:
            title = "Blur";
                this.blur = true;
                this.convolute = false;
                break;
            default:
            title = "Original";
                this.luma = false;
                this.gray = false;
                this.invert = false;
                this.convolute = false;
                this.blur = false;
        }
        process();
    }

    public void upFilter() {
        this.currentIndexFilter = (this.currentIndexFilter + 1) % numFilters;
        updateFilterIndex();
    }

    public void downFilter() {
        this.currentIndexFilter = currentIndexFilter - 1 >= 0 ? (currentIndexFilter - 1) % numFilters : numFilters - 1;
        updateFilterIndex();
    }

    public boolean getParam(int i) {


        switch (i) {
            case 2:
                return this.convolute;
            case 3:
                return this.threshold;
            case 5:
                return this.invert;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
    }
}
