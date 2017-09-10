package com.nevaryyy.timerborderviewdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.concurrent.TimeUnit;

/**
 * Created by nevaryyy on 2017/9/10.
 */

public class TimerBorderView extends View {

    private static final String TAG = "TimerBorderView";

    private static final int DEFAULT_BORDER_WIDTH = 20;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private static final long MIN_TICK_INTERVAL = 500;

    private int borderWidth;
    private int borderColor;
    private Position startPos;
    private boolean clockwise;

    private Paint borderPaint;
    private Point[] points;

    private double ratio;

    private ValueAnimator valueAnimator;
    private TimerListener timerListener;

    public TimerBorderView(Context context) {
        this(context, null);
    }

    public TimerBorderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerBorderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ratio = 1.;

        points = new Point[4];
        for (int i = 0; i < 4; i ++) {
            points[i] = new Point(0, 0);
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerBorderView, defStyleAttr, 0);

        borderWidth = a.getDimensionPixelSize(R.styleable.TimerBorderView_borderWidth, DEFAULT_BORDER_WIDTH);
        borderColor = a.getColor(R.styleable.TimerBorderView_borderColor, DEFAULT_BORDER_COLOR);
        startPos = Position.parseByIntValue(a.getInteger(R.styleable.TimerBorderView_startPos, Position.TOP_RIGHT.value));
        clockwise = a.getBoolean(R.styleable.TimerBorderView_clockwise, true);

        a.recycle();

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);
    }

    public void show() {
        borderPaint.setAlpha(255);
        invalidate();
    }

    public void hide() {
        borderPaint.setAlpha(0);
        invalidate();
    }

    public synchronized void startCountdown(long time, TimeUnit timeUnit, TimerListener timerListener) {
        this.timerListener = timerListener;

        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(1, 0);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ratio = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (TimerBorderView.this.timerListener != null) {
                        TimerBorderView.this.timerListener.onStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (TimerBorderView.this.timerListener != null) {
                        TimerBorderView.this.timerListener.onFinish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (TimerBorderView.this.timerListener != null) {
                        TimerBorderView.this.timerListener.onCancel();
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    if (TimerBorderView.this.timerListener != null) {
                        TimerBorderView.this.timerListener.onRepeat();
                    }
                }
            });
        }
        valueAnimator.setDuration(timeUnit.toMillis(time));
        valueAnimator.start();
    }

    public void pause() {
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            return;
        }
        valueAnimator.pause();
        if (timerListener != null) {
            timerListener.onPause();
        }
    }

    public void resume() {
        if (valueAnimator == null || !valueAnimator.isPaused()) {
            return;
        }
        valueAnimator.resume();
        if (timerListener != null) {
            timerListener.onResume();
        }
    }

    public void cancel() {
        if (valueAnimator == null) {
            return;
        }
        valueAnimator.cancel();
    }

    public void clear() {
        cancel();

        ratio = 0;
        invalidate();
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setStartPos(Position startPos) {
        this.startPos = startPos;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    private void refreshPoints(int width, int height) {
        Position currentPos = startPos;

        for (int i = 0; i < 4; i ++) {
            points[i].x = currentPos.getWidthRatio() * width;
            points[i].y = currentPos.getHeightRatio() * height;
            currentPos = currentPos.nextPos(clockwise);
        }
    }

    private double calcDistance(Point point1, Point point2) {
        return calcDistance(point1.x, point1.y, point2.x, point2.y);
    }

    private double calcDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        double length = (width + height) * 2 * ratio;

        refreshPoints(width, height);

        for (int i = 0; i < 4 && length > 0; i ++) {
            Point point1 = points[i];
            Point point2 = points[(i + 1) % 4];
            double distance = calcDistance(point1, point2);
            double ratio = Math.min(length / distance, 1.);
            double endX = point1.x + (point2.x - point1.x) * ratio;
            double endY = point1.y + (point2.y - point1.y) * ratio;

            canvas.drawLine(point1.x, point1.y, (float) endX, (float) endY, borderPaint);
            length -= calcDistance(point1.x, point1.y, endX, endY);
        }
    }

    public interface TimerListener {
        void onStart();
        void onPause();
        void onResume();
        void onCancel();
        void onFinish();
        void onRepeat();
    }

    public static abstract class TimerAdapter implements TimerListener {
        @Override
        public void onStart() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onRepeat() {

        }
    }

    public enum Position {
        TOP_LEFT(0), TOP_RIGHT(1), BOTTOM_LEFT(2), BOTTOM_RIGHT(3);

        private int value;

        Position(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Position parseByIntValue(int value) {
            Position[] prioritiesArray = Position.values();
            Position result = null;
            for (Position type : prioritiesArray) {
                if (type.getValue() == value) {
                    result = type;
                    break;
                }
            }
            return result;
        }

        public Position nextPos(boolean clockwise) {
            switch (this) {
                case TOP_LEFT:
                    return clockwise ? BOTTOM_LEFT : TOP_RIGHT;
                case TOP_RIGHT:
                    return clockwise ? TOP_LEFT : BOTTOM_RIGHT;
                case BOTTOM_RIGHT:
                    return clockwise ? TOP_RIGHT : BOTTOM_LEFT;
                case BOTTOM_LEFT:
                    return clockwise ? BOTTOM_RIGHT : TOP_LEFT;
            }
            throw new IllegalStateException("Current position is invalid.");
        }

        public int getWidthRatio() {
            return (this == TOP_LEFT || this == BOTTOM_LEFT) ? 0 : 1;
        }

        public int getHeightRatio() {
            return (this == TOP_LEFT || this == TOP_RIGHT) ? 0 : 1;
        }
    }
}
