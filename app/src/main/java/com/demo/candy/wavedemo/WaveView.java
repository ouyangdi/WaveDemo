package com.demo.candy.wavedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ouyangdi on 2016/1/21.
 */
public class WaveView extends View {
    public static final String TAG="ouyangdi";

    private static final float DEFAULT_AMPLITUDE_RATIO = 0.05f;  //幅值
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f; //水位
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f; //波长
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;  //波动

    public static final int DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#28FFFFFF");
    public static final int DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#3CFFFFFF");
    public static final ShapeType DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE;

    public enum ShapeType {
        CIRCLE,
        SQUARE
    }

    //if true, the shader will display the wave
    private boolean mShowWave;
    //shader containing repeated waves
    private BitmapShader mWaveShader;
    //shader matrix
    private Matrix mShaderMatrix;
    //paint to draw wave
    private Paint mViewPaint;
    //paint to draw border
    private Paint mBorderPaint;

    private float mDefaultAmplitude;
    private float mDefaultWaterLevel;
    private float mDefaultWaveLength;
    private float mDefaultAngularFrequency;

    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    private float mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO;
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;

    private int mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
    private int mFrontWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
    private ShapeType mShapeType = DEFAULT_WAVE_SHAPE;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mShaderMatrix = new Matrix();
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);

        mShowWave = true;
    }

    public void setWaveShiftRatio(float waveShiftRatio) {
        if(mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }

    public void setShowWave(boolean showWave) {
        mShowWave = showWave;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // modify paint shader according to mShowWave state
        if(mShowWave && mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if(mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }

            mShaderMatrix.postTranslate(mWaveShiftRatio * getWidth(), 0);

            float borderWidth = mBorderPaint == null ? 0f :mBorderPaint.getStrokeWidth();
            switch (mShapeType) {
                case CIRCLE:
                    if(borderWidth > 0) {
                        canvas.drawCircle(getWidth()/2f, getHeight()/2f,
                                (getWidth()-borderWidth)/2f-1f,mBorderPaint);
                    }
                    float radius = getWidth() / 2f - borderWidth;
                    canvas.drawCircle(getWidth()/2f, getHeight()/2f, radius, mViewPaint);
                    break;
                case SQUARE:
                    if(borderWidth > 0) {
                        canvas.drawRect(borderWidth / 2f,
                                borderWidth / 2f,
                                getWidth() - borderWidth / 2f - 0.5f,
                                getHeight() - borderWidth / 2f - 0.5f,
                                mBorderPaint);
                    }
                    canvas.drawRect(borderWidth,
                            borderWidth,
                            getWidth() - borderWidth,
                            getHeight() - borderWidth,
                            mViewPaint);
                    break;
            }
        } else {
            mViewPaint.setShader(null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createShader();
    }

    /**
     * Create the shader with default waves which repeat horizontally, and clamp vertically
     */
    private void createShader() {
        mDefaultAngularFrequency = (float)(2.0 * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / getWidth());
        mDefaultAmplitude = getHeight()*DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = getHeight()*DEFAULT_WATER_LEVEL_RATIO;
        mDefaultWaveLength = getWidth();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint wavePaint = new Paint();
        wavePaint.setStrokeWidth(2);
        wavePaint.setAntiAlias(true);

        //Draw default waves into the bitmap
        //y=Asin(ωx+φ)+h
        int endX = getWidth() + 1;
        int endY = getHeight() + 1;

        float[] waveY = new float[endX];

        //draw behind wave
        wavePaint.setColor(mBehindWaveColor);
        for(int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * mDefaultAngularFrequency;
            float beginY = (float)(mDefaultWaterLevel+ mDefaultAmplitude * Math.sin(wx));
            canvas.drawLine(beginX, beginY, beginX, endY,wavePaint);

            waveY[beginX] = beginY;
        }

        //draw front wave
        wavePaint.setColor(mFrontWaveColor);
        int wave2Shift = (int)(mDefaultWaveLength / 4);
        for(int beginX = 0; beginX < endX; beginX++ ) {
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
        }

        //use the Bitmap to create the shader
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);
    }
}
