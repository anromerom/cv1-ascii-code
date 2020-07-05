package com.supermegadinamita;

import com.jogamp.opengl.*;

import nub.core.Graph;
import nub.core.Node;
import nub.core.constraint.AxisPlaneConstraint;
import nub.core.constraint.LocalConstraint;
import nub.primitives.Matrix;
import nub.primitives.Quaternion;
import nub.primitives.Vector;
import nub.processing.Scene;
import processing.core.*;
import processing.event.MouseEvent;
import processing.opengl.PJOGL;
import processing.opengl.PShader;

import static com.jogamp.opengl.GL2GL3.GL_CLIP_DISTANCE0;



public class Main extends PApplet {

    Scene scene;
    String renderer = P3D;
    int w = 800;
    int h = 500;

    PGraphics refrGraphics, reflGraphics, waterGraphics;
    PShader clipShader, waterShader;

    Node waterNode, defaultNode, skyNode, terrainNode;

    PJOGL  pgl;
    GL2GL3 gl;

    boolean constrained = false;
    PImage dudv;

    public void settings(){
        size(w, h, renderer);
    }


    public void setup(){

        rectMode(CENTER);

        hint(ENABLE_OPENGL_ERRORS);
        pgl = (PJOGL) beginPGL();
        gl = pgl.gl.getGL2GL3();
        gl.glEnable(GL_CLIP_DISTANCE0);
        PShape moon = loadShape("models/moon/moon.obj");

        PShape mountain = terrainShape(1000);





        scene = new Scene(this, renderer, w, h);
        defaultNode = new Node(); defaultNode.setPosition(0,0);
        skyNode = new Node(defaultNode , skyShape(800));  //     );//
        Node moonNode = new Node(defaultNode, moon);



        terrainNode = new Node(defaultNode, mountain);
        waterNode = new Node(waterShape());


        skyNode.setPosition(new Vector(0,0,0)); skyNode.setPickingThreshold(0);
        moonNode.setPosition(new Vector(0,100,0)); moonNode.setPickingThreshold(0);
        waterNode.setPosition(new Vector(0,0,0)); waterNode.setPickingThreshold(0);



        moonNode.scale(0.4f);

        terrainNode.setPosition(new Vector(0,-100,0)); terrainNode.setPickingThreshold(0); //terrainNode.setOrientation(moonNode);



        scene.setRadius(900);
        scene.rotateEye(PI, 0 , 0);
        scene.eye().setPosition(new Vector(0,15,0));
        scene.eye().setOrientation(new Quaternion(0,0,1,0));


        if(constrained){
            AxisPlaneConstraint constraint = new LocalConstraint();
            constraint.setRotationConstraintType(AxisPlaneConstraint.Type.AXIS);
            constraint.setRotationConstraintDirection(new Vector(0,1,0));
            scene.eye().setConstraint(constraint);
        }


        //buffers

        clipShader = loadShader("shaders/clipFrag.glsl", "shaders/clipVert.glsl");
        waterShader = loadShader("shaders/waterFrag.glsl", "shaders/waterVert.glsl");

        dudv = loadImage("shaders/dudv.png");
        waterShader.set("dudv", dudv);


        refrGraphics = createGraphics(w/2, h/2, P3D);
        refrGraphics.shader(clipShader);

        reflGraphics = createGraphics(w/2, h/2, P3D);
        reflGraphics.shader(clipShader);

        waterGraphics = createGraphics(w/2, h/2, P3D);
        waterGraphics.shader(waterShader);


        frameRate(10);
    }

    float offset = 0;

    public void draw(){

        scene.beginDraw();
        scene.context().background(255);
        scene.render(defaultNode);

        scene.display();

        PImage refractionTexture = refractionTexture(scene);
        PImage reflectionTexture = reflectionTexture(scene);

        waterShader.set("refractionTexture", refractionTexture);
        waterShader.set("reflectionTexture", reflectionTexture);
        waterShader.set("offset", (offset+=0.05)% 1);
        waterShader.set("eyePosition", scene.eye().position().x(), scene.eye().position().y(), scene.eye().position().z());

        waterGraphics.beginDraw();
        waterGraphics.clear();
        waterGraphics.endDraw();
        Scene.render(waterGraphics, Graph.Type.PERSPECTIVE, waterNode, scene.eye(), scene.zNear(), scene.zFar());
        image(waterGraphics.get(), 0,0);

        // 2. Display back buffer
        //scene.displayBackBuffer(0, h / 2);
        //println(frameRate);

        //clipShader.set("plane", new float[]{0, 1, 0, 100});


        image(reflectionTexture(scene),0, 0 , w/4f, h/4f);
        image(refractionTexture(scene),3f*w/4f, 0 , w/4f, h/4f);


        scene.endDraw();
    }



    PImage reflectionTexture(Scene scene){

        reflGraphics.beginDraw();
        reflGraphics.clear();

        //Vector wm = n.worldLocation(n.position());

        Matrix wm = defaultNode.worldMatrix();


        //Por loop
        PMatrix3D aux = new PMatrix3D(wm.m00(),wm.m01(),wm.m02(),wm.m03(),wm.m10(),wm.m11(),wm.m12(),wm.m13(),wm.m20(),wm.m21(),wm.m22(),wm.m23(),wm.m30(),wm.m31(),wm.m32(),wm.m33());

        clipShader.set("worldMatrix",aux);
        clipShader.set("plane", 0,1, 0, -waterNode.position().y());


        float distance = -2 * (scene.eye().position().y() - waterNode.position().y());

        Vector eyePosition = scene.eye().position();

        scene.eye().setPosition(eyePosition.x() , eyePosition.y() - distance, eyePosition.z());
        Quaternion or = scene.eye().orientation();
        scene.eye().setOrientation(or.x(), -or.y(), or.z(), or.w());

        Scene.render(reflGraphics, Graph.Type.PERSPECTIVE, defaultNode, scene.eye() , scene.zNear(), scene.zFar());

        scene.eye().setOrientation(or);
        scene.eye().setPosition(eyePosition);
        //scene.render(n, );


        reflGraphics.endDraw();

        PGraphics pg = createGraphics(w, h);
        pg.beginDraw();
        pg.translate(0, h);
        pg.scale(1,-1);
        pg.image( reflGraphics.get(),0, 0, w, h);
        pg.endDraw();

        return pg.get();
    }

    PImage refractionTexture(Scene scene){

        refrGraphics.beginDraw();
        refrGraphics.clear();
        //Vector wm = n.worldLocation(n.position());

        Matrix wm = defaultNode.worldMatrix();


        //Por loop
        PMatrix3D aux = new PMatrix3D(wm.m00(),wm.m01(),wm.m02(),wm.m03(),wm.m10(),wm.m11(),wm.m12(),wm.m13(),wm.m20(),wm.m21(),wm.m22(),wm.m23(),wm.m30(),wm.m31(),wm.m32(),wm.m33());

        clipShader.set("worldMatrix",aux);
        clipShader.set("plane", 0,-1, 0, waterNode.position().y());


        Scene.render(refrGraphics, Graph.Type.PERSPECTIVE, defaultNode, scene.eye() , scene.zNear(), scene.zFar());


        refrGraphics.endDraw();

        return refrGraphics.get();
    }



    public void mouseDragged() {
        if (mouseButton == LEFT)
            scene.rotateEye( 0, 0.5f* (pmouseX - mouseX)/w, 0);
        else if (mouseButton == RIGHT)
            scene.rotateEye( 0.5f* (pmouseY - mouseY)/h, 0, 0);
    }

    public void mouseWheel(MouseEvent event) {
        scene.moveForward(event.getCount() * 2);
    }



    public void mouseClicked(){
        if (mouseButton == LEFT)
        {
            //scene.alignEye();
        }
        else if ( mouseButton == RIGHT)
        {
            scene.moveForward(1);
        }
    }


    PShape skyShape(float d){

        int i = 1;
        //Sky boxes: https://imgur.com/a/WSGJ5
        PImage skyImg = loadImage("data/sky_field_" + i +  ".jpg");
        //skyImg = loadImage("data/sky.png");
        PImage img = skyImg.get(0, skyImg.height/3, skyImg.width, skyImg.height/3);

        //img = loadImage("data/sky.png");


        noStroke();
        PShape sky = createShape(GROUP);


        // .vertex( -d,  d,  d,              //A
        // .vertex( -d,  d, -d,              //B
        // .vertex(  d,  d, -d,              //C
        // .vertex(  d,  d,  d,              //D
        // .vertex(  d, -d,  d,              //E
        // .vertex(  d, -d, -d,              //F
        // .vertex( -d, -d, -d,              //G
        // .vertex( -d, -d,  d,              //H




        //.vertex(  0, 0);
        //.vertex(  img.width, 0);
        //.vertex(  img.width, img.height);
        //.vertex(  0, img.height);

//------------------------- TOP ------------------------------

        PShape top = createShape();
        top.beginShape();

        img = skyImg.get(skyImg.width/4 + 1, 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        top.texture(img);
        top.vertex( -d,  d,  d, 0, 0);
        top.vertex(  d,  d,  d, img.width, 0);
        top.vertex(  d,  d, -d,  img.width, img.height);
        top.vertex( -d,  d, -d, 0, img.height);
        top.endShape(CLOSE);
        sky.addChild(top);

//------------------------- FRONT ------------------------------

        PShape front = createShape();
        front.beginShape();

        img = skyImg.get(skyImg.width/4 + 1 , skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        front.texture(img);
        front.vertex( -d,  d, -d,  0, 0);
        front.vertex(  d,  d, -d,  img.width, 0);
        front.vertex(  d, -d, -d,  img.width, img.height);
        front.vertex( -d, -d, -d,  0, img.height);
        front.endShape(CLOSE);
        sky.addChild(front);

        //------------------------- LEFT ------------------------------

        PShape left = createShape();
        left.beginShape();

        img = skyImg.get(1 , skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        left.texture(img);
        left.vertex( -d,  d,  d,  0, 0);
        left.vertex( -d,  d, -d,  img.width, 0);
        left.vertex( -d, -d, -d,  img.width, img.height);
        left.vertex( -d, -d,  d,  0, img.height);
        left.endShape(CLOSE);
        sky.addChild(left);


        //------------------------- RIGHT ------------------------------

        PShape right = createShape();
        right.beginShape();

        img = skyImg.get(2 * skyImg.width/4 , skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        right.texture(img);

        right.vertex(  d,  d, -d,   0, 0);
        right.vertex(  d,  d,  d,  img.width, 0);
        right.vertex(  d, -d,  d,  img.width, img.height);
        right.vertex(  d, -d, -d,  0, img.height);
        right.endShape(CLOSE);
        sky.addChild(right);



        //------------------------- BACK ------------------------------

        PShape back = createShape();
        back.beginShape();

        img = skyImg.get(3 * skyImg.width/4 , skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        back.texture(img);

        back.vertex(  d,  d,  d,   0, 0);
        back.vertex( -d,  d,  d,  img.width, 0);
        back.vertex( -d, -d,  d,  img.width, img.height);
        back.vertex(  d, -d,  d,  0, img.height);
        back.endShape(CLOSE);
        sky.addChild(back);



        //------------------------- BOTTOM ------------------------------

        PShape bottom = createShape();
        bottom.beginShape();

        img = skyImg.get(skyImg.width/4 , 2 * skyImg.height/3 + 1, skyImg.width/4 - 2, skyImg.height/3 - 2);
        bottom.texture(img);

        bottom.vertex(  -d, -d, -d,   0, 0);
        bottom.vertex(   d, -d, -d,  img.width, 0);
        bottom.vertex(   d, -d,  d,  img.width, img.height);
        bottom.vertex(  -d, -d,  d,  0, img.height);
        bottom.endShape(CLOSE);
        sky.addChild(bottom);


        // .vertex( -d,  d,  d,              //A
        // .vertex( -d,  d, -d,              //B
        // .vertex(  d,  d, -d,              //C
        // .vertex(  d,  d,  d,              //D
        // .vertex(  d, -d,  d,              //E
        // .vertex(  d, -d, -d,              //F
        // .vertex( -d, -d, -d,              //G
        // .vertex( -d, -d,  d,              //H
        return sky;
    }

    PShape waterShape(){

        PShape waterQuad = createShape();



        int d = 1000;
        float h = 0;
        waterQuad.beginShape();
        waterQuad.fill( 0,0,125 , 100);
        waterQuad.vertex( -d,  h,  d);
        waterQuad.vertex(  d,  h,  d);
        waterQuad.vertex(  d,  h, -d);
        waterQuad.vertex( -d,  h, -d);
        waterQuad.endShape(CLOSE);

        return waterQuad;

    }


    PShape terrainShape(int wide){

        int d = 50;
        int levels = 2*d;

        float scale = 0.09f;

        float size = wide/d;
        pushMatrix();
        PImage terrainTexture = loadImage("models/terrain/ground_texture.jpg");

        PShape terrain = createShape(GROUP);

        for (int l = 0; l < levels; l++){
            for (int i = max(0, l - d); i< min(l, d); i++){

                int j = l - i - 1;

                //Back triangle

                if (i - 1 >= 0 && j - 1 >= 0){
                    PShape triangle = createShape();

                    triangle.beginShape();
                    triangle.noStroke();
                    triangle.texture(terrainTexture);

                    triangle.vertex(i *size - wide/2,     map(noise((i)*scale,   (j)*scale), -1, 1, -0.2f*wide, 0.2f*wide), j*size- wide/2  , map(i  , 0, d, 0, terrainTexture.width),     map(j  , 0, d, 0, terrainTexture.width));
                    triangle.vertex((i-1)*size - wide/2,  map(noise((i-1)*scale, (j)*scale), -1, 1, -0.2f*wide, 0.2f*wide), j*size - wide/2  , map(i-1, 0, d, 0, terrainTexture.width),     map(j  , 0, d, 0, terrainTexture.width));
                    triangle.vertex(i*size - wide/2,  map(noise((i)*scale, (j-1)*scale), -1, 1, -0.2f*wide, 0.2f*wide), (j-1)*size - wide/2  , map(i  , 0, d, 0, terrainTexture.width),     map(j-1, 0, d, 0, terrainTexture.width));

                    triangle.endShape(CLOSE);

                    terrain.addChild(triangle);
                }


                //Front triangle
                if( i + 1 < d && j + 1 < d){
                    PShape triangle = createShape();

                    triangle.beginShape();
                    triangle.noStroke();
                    triangle.texture(terrainTexture);

                    triangle.vertex(i*size -wide/2,    map(noise((i)  *scale, (j)    *scale), -1, 1, -0.2f*wide, 0.2f*wide), j*size  -wide/2    ,map(i  , 0, d, 0, terrainTexture.width), map(j,   0, d, 0, terrainTexture.width));
                    triangle.vertex((i+1)*size -wide/2,  map(noise((i+1)*scale, (j)  *scale), -1, 1, -0.2f*wide, 0.2f*wide), j*size -wide/2  ,map(i+1, 0, d, 0, terrainTexture.width), map(j,   0, d, 0, terrainTexture.width));
                    triangle.vertex(i*size -wide/2,  map(noise((i)  *scale, (j+1)*scale), -1, 1, -0.2f*wide, 0.2f*wide), (j+1)*size -wide/2  ,map(i  , 0, d, 0, terrainTexture.width), map(j+1, 0, d, 0, terrainTexture.width));

                    triangle.endShape(CLOSE);
                    terrain.addChild(triangle);
                }
            }
        }
        popMatrix();
        return terrain;

    }




    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "com.supermegadinamita.Main" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
