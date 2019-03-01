package com.linwei.electronicdream.onehandzoomimageview;

import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Toast;

public class OneFingerZoomImageView extends AppCompatImageView {

    private static final int DOUBLE_TAPPING_DELTA = ViewConfiguration.getTapTimeout() + 100;
    private static final int DP_PER_1X = 1000   ;

    private Matrix matrix = new Matrix();
    private Matrix currentMatrix = new Matrix();

    private long startTime = 0;
    private boolean isDoubleTapping = false;
    private PointF startPoint = new PointF();
    private float scale;

    public OneFingerZoomImageView(Context context) {
        super(context);
    }

    public OneFingerZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OneFingerZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setScaleType(ScaleType.MATRIX);
        Log.e("zoooming", "event:"+event);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            currentMatrix = this.getImageMatrix();
            startPoint.x = event.getX();
            startPoint.y = event.getY();

        }

        //拖动
        if(event.getAction() == MotionEvent.ACTION_MOVE && !isDoubleTapping){
            matrix.set(currentMatrix);
            matrix.postTranslate((event.getX()-startPoint.x)/10,(event.getY()-startPoint.y)/10 );
            this.setImageMatrix(matrix);
            return false;
        }

        if (event.getPointerCount() != 1) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isDoubleTapping) {
                isDoubleTapping = false;
//                listener.onZoomEnded(scale, 0);
                return true;
            } else {
                startTime = currentTime;
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN && !isDoubleTapping
                && currentTime - startTime < DOUBLE_TAPPING_DELTA) {
            isDoubleTapping = true;
            startPoint.x = event.getX();
            startPoint.y = event.getY();

            Toast.makeText(getContext(), "moving", Toast.LENGTH_SHORT).show();

//            listener.onGestureInit(startX, startY, startX, startY);
//            listener.onZoomStarted(new PointF(startX, startY));

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isDoubleTapping) {
                float delta = px2dp((int) (startPoint.y - event.getY()));
                float scaleDelta = delta / DP_PER_1X;
                scale = 1 - scaleDelta;
                Toast.makeText(getContext(), "zooming", Toast.LENGTH_SHORT).show();
                onZooming(scale);
                return true;
            } else {

                return true;
            }
        }
        return true;
    }

    private void onZooming(double scale){
        Log.e("zoooming", "scale:"+scale);
        matrix.set(currentMatrix);
        matrix.postScale((float) scale, (float) scale);
        this.setImageMatrix(matrix);
    }



    private int px2dp (int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
