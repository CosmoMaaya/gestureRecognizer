package ca.uwaterloo.cs349;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("AppCompatCustomView")
public class DrawingView extends View {

    final String LOGNAME = "DrawingView";

    // drawing
    Path path = null;
    RectF debugHelper = null;
    Paint paintbrush = new Paint(Color.BLUE);
    MyFragment parentFragment;
//    PathMeasure sample;

    // constructor
    public DrawingView(Context context) {
        super(context);
        paintbrush.setStyle(Paint.Style.STROKE);
        paintbrush.setStrokeWidth(15);
        setWillNotDraw(false);
    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        paintbrush.setStyle(Paint.Style.STROKE);
        paintbrush.setStrokeWidth(15);
        setWillNotDraw(false);
    }

    // we save a lot of points because they need to be processed
    // during touch events e.g. ACTION_MOVE
    float x1, y1;
    int p1_id, p1_index;

    // store cumulative transformations
    // the inverse matrix is used to align points with the transformations - see below
    Matrix matrix = new Matrix();
    Matrix inverse = new Matrix();

    // capture touch events (down/move/up) to create a path/stroke that we draw later
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
//        Log.d("Draw", "Touched: " + event.getPointerCount());
        // 1 point is drawing or erasing
        if (event.getPointerCount() == 1) {
            p1_id = event.getPointerId(0);
            p1_index = event.findPointerIndex(p1_id);

            // invert using the current matrix to account for pan/scale
            // inverts in-place and returns boolean
            x1 = event.getX(p1_index);
            y1 = event.getY(p1_index);

//            Log.d("Draw", "Touched: " + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
//                    Log.d(LOGNAME, "Action down");
                    if(!(parentFragment instanceof HomeFragment) && path != null && !path.isEmpty()) return false;
                    path = new Path();
                    path.moveTo(x1, y1);

                    break;
                case MotionEvent.ACTION_MOVE:
//                    Log.d(LOGNAME, "Action move");
                    path.lineTo(x1, y1);
                    break;
                case MotionEvent.ACTION_UP:
//                    Log.d(LOGNAME, "Action up");
                    break;
            }
        }
//        sample = new PathMeasure(path, false);
        invalidate();

        if (event.getAction() == MotionEvent.ACTION_UP){
            if (parentFragment != null){
                parentFragment.fragmentOnMotionUp();
            }
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw lines over it
        if(path != null){
            canvas.drawPath(path, paintbrush);
        }

        if(debugHelper != null){
            canvas.drawRect(debugHelper, paintbrush);
        }

    }

    public Path getPath(){
        return path;
    }
    public void setPath(Path path){
        this.path = path;
    }
    public void setParentFragment(MyFragment fragment){
        this.parentFragment = fragment;
    }
}
