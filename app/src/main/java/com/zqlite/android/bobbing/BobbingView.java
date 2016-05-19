package com.zqlite.android.bobbing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author qinglian.zhang
 */
public class BobbingView extends View {

    Paint paint = new TextPaint();

    final int textSizeDP = 20;

    final float textSize = DipToPixels(getContext(), textSizeDP);

    String[] texts;

    public BobbingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        //paint.setShadowLayer(5,2,3,Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (texts != null && texts.length > 0) {
            drawBobbingText(texts, canvas);
        }
    }

    private void drawBobbingText(String[] textArray, Canvas canvas) {

        float canvasW = getWidth();
        float canvasH = getHeight();

        float[] bobbings = new float[textArray.length];
        float bobbing = canvasW/(textArray.length + 1);
        for(int i = 0;i<textArray.length;i++){
            bobbings[i] = bobbing * (i + 1);
        }

        for (int index = 0; index < textArray.length; index++) {
            float textW = paint.measureText(textArray[index]);
            int tmpDP = textSizeDP;
            float tmpSize = textSize;
            if(textW > canvasH){
                do{
                    tmpSize = DipToPixels(getContext(),--tmpDP);
                    paint.setTextSize(tmpSize);
                    textW = paint.measureText(textArray[index]);
                }while(textW > canvasH);
            }
            canvas.save();
            if (index % 2 == 0) {
                canvas.rotate(270,0,0);
                canvas.translate(-canvasH, 0);
                canvas.drawText(textArray[index], (canvasH-textW)/2, bobbings[index]-tmpSize/2, paint);
            }
            if (index % 2 != 0) {
                canvas.rotate(90, 0, 0);
                canvas.drawText(textArray[index], (canvasH-textW)/2, -(bobbings[index]-tmpSize), paint);
            }

            canvas.restore();
            paint.setTextSize(textSize);
        }

    }

    public void setBobbingText(String ...texts) {
        this.texts = texts;
        invalidate();
    }

    public static int DipToPixels(Context context, int dip) {
        final float SCALE = context.getResources().getDisplayMetrics().density;
        float valueDips = dip;
        int valuePixels = (int) (valueDips * SCALE + 0.5f);
        return valuePixels;
    }

}
