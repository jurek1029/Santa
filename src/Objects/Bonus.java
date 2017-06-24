package Objects;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.Random;
import santa.v1.Engine;
import santa.v1.GLES2Renderer;
import santa.v1.SantaActivity;



public class Bonus extends Object {

    Type type;
    private float vel;
    enum Type {life, slow, wrapOne, wrapAll};

    private static int minPresentsForBonus = Engine.bonusMinPresentsForBonus;
    private static int presentWrapped = 0;
    private static int counter = 0;
    long timeCounter;
    float a,b;
    double theta = 0.0;



    private Bonus (float x, float y, Type type)
    {
        super();
        this.x = x;
        this.y = y;
        this.type = type;
        Random rnd = new Random();
        vel = Engine.bonusVelocity + rnd.nextFloat()/8;

        a= rnd.nextFloat()/10;
        b=rnd.nextFloat()*10;


        switch (type)
        {
            case life:
                this.texture = new float[] {
                    0.0f, 0.0f,
                    .5f, 0.0f,
                    .5f, .5f,
                    0.0f, .5f
            };
            break;

            case slow:
                this.texture = new float[] {
                        0.5f, 0.0f,
                        1.0f, 0.0f,
                        1.0f, .5f,
                        0.5f, .5f
                };

             break;
        }

        allocate();
        if(SantaActivity.supportsEs2)
            allocateGLES2();
    }


    public void update(long elapsedTime)
    {
        float delta = (float)(elapsedTime)/1000;
        timeCounter+=elapsedTime;

        this.y += vel*delta;
        this.x = a*((float)Math.sin(b*theta));
        theta+=0.01;
        if (theta>=Math.PI) theta = 0;
        if ( this.y>=1.0f)
            Engine.bonus = null;

    }

    public static void createBonus (Present p)
    {
        presentWrapped++;
        if (presentWrapped<minPresentsForBonus) return;

        Random rnd = new Random();
        if (rnd.nextFloat()>0.25) return;

        counter++;
        if (counter>2) minPresentsForBonus--;
      //  minPresentsForBonus--;
        presentWrapped = 0;
        Engine.bonusLastPresent = p;
        Engine.bonusToCreate = true;
    }

    public static void makeBonus()
    {
        Random rand = new Random();
        int i = rand.nextInt(2); //poki co tylko bonus 0 i 1 sa
        Present p = Engine.bonusLastPresent;
       // Engine.bonus = new Bonus(p.x+(p.width-Engine.bonusWidth)/2, p.y+p.height/2,Type.values()[i]);
        Engine.bonus = new Bonus(p.x+(p.width-Engine.bonusWidth)/2,-Engine.bonusWidth,Type.values()[i]);
        Engine.bonusToCreate = false;
        Engine.bonusLastPresent = null;
    }

    public boolean isTouched(float xp, float yp)
    {
        if (Math.pow(xp-x,2)+Math.pow(yp-y,2)<=Math.pow(Engine.bonusWidth,2)) return true;
        else return false;
    }

    public void upgradePlayer()
    {
        switch (type) {
            case life:
                if (Engine.health<3) Engine.health++;
             break;

            case slow:
                Engine.slowByBonus = true;
                if (Engine.slowStartTime<0) Engine.slowStartTime=System.currentTimeMillis();
             break;

            case wrapOne:
                Random rand = new Random();
                int size = Engine.vPresents.size();
                Present p = Engine.vPresents.elementAt(rand.nextInt(size));
                Engine.pf.wrapPresent(p);

            break;

            case wrapAll:

                for(Present pr:Engine.vPresents)
                {
                    Engine.pf.wrapPresent(pr);
                }

             break;

            default:

        }

        Engine.bonus=null;

    }

    public static void backToBeginning()
    {
        minPresentsForBonus = Engine.bonusMinPresentsForBonus;
        presentWrapped = 0;
        counter = 0;

    }

}
