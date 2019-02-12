package com.sherlockshi.pushrefreshpractise.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.sherlockshi.pushrefreshpractise.R;

import java.util.ArrayList;

/**
 * Created by Jack on 2015/10/16.
 */
public class BallPulseIndicator extends Indicator {
    private int[] colorArray;
    float circleSpacing = 4;
    float radius;
    float x;
    float y;

    public BallPulseIndicator(){

    }

    public BallPulseIndicator(Context context) {
        colorArray = context.getResources().getIntArray(R.array.indicatorColorArr);
    }

    public static final float SCALE = 1.0f;

    //scale x ,y
    private float[] scaleFloats = new float[]{SCALE,
            SCALE,
            SCALE};


    @Override
    public void draw(Canvas canvas, Paint paint) {
//        float circleSpacing = 4;
        if (radius == 0) {
            radius = (Math.min(getWidth(), getHeight()) - circleSpacing * 2) / 6;
        }

        if (x == 0) {
            x = getWidth() / 2 - (radius * 2 + circleSpacing);
        }

        if (y == 0) {
            y = getHeight() / 2;
        }

        for (int i = 0; i < colorArray.length; i++) {
            paint.setColor(colorArray[i]);

            canvas.save();
            float translateX = x + (radius * 2) * i + circleSpacing * i;
            canvas.translate(translateX, y);
            canvas.scale(scaleFloats[i], scaleFloats[i]);
            canvas.drawCircle(0, 0, radius, paint);
            canvas.restore();
        }
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators = new ArrayList<>(colorArray.length);
        int[] delays = new int[]{0, 120, 240};
        for (int i = 0; i < colorArray.length; i++) {
            final int index = i;

            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.3f, 1);

            scaleAnim.setDuration(750);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);

            addUpdateListener(scaleAnim, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });


            animators.add(scaleAnim);
        }

        return animators;
    }


}
