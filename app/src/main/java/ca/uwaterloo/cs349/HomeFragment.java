package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements MyFragment{

    private SharedViewModel mViewModel;
    private DrawingView drawingView;

    private ImageView imageView1, imageView2, imageView3;
    ArrayList<ImageView> imageViews = new ArrayList<>();
    private TextView textView1, textView2, textView3;
    ArrayList<TextView> textViews = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("Home", "Create view");
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        drawingView = root.findViewById(R.id.drawingView_home);
        drawingView.setParentFragment(this);

        //Initialize img and txt
        imageView1 = root.findViewById(R.id.thumbNail1);
        imageView2 = root.findViewById(R.id.thumbNail2);
        imageView3 = root.findViewById(R.id.thumbNail3);
        imageViews.add(imageView1);
        imageViews.add(imageView2);
        imageViews.add(imageView3);
        textView1 = root.findViewById(R.id.home_text1);
        textView2 = root.findViewById(R.id.home_text2);
        textView3 = root.findViewById(R.id.home_text3);
        textViews.add(textView1);
        textViews.add(textView2);
        textViews.add(textView3);


        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Home", "Destroy view");
    }

    /*
    This should actually function as matching the best three options but I'm too lazy at this moment
     */
    @Override
    public void fragmentOnMotionUp() {
        ArrayList<Gesture> bestMatches = new ArrayList<>();
        bestMatches.add(null);
        bestMatches.add(null);
        bestMatches.add(null);

        if (drawingView.path != null && !drawingView.path.isEmpty()){
            //But by click, only line to, no move to
            Gesture gesture = new Gesture(drawingView.path, "");
            if(!gesture.isEmpty()){
                gesture.standardize();

                bestMatches = mViewModel.bestMatches(gesture);
            }
        }


        setView(bestMatches);
    }

    private void setView(ArrayList<Gesture> gestures){
        for (int i = 0; i < 3; i++){
            Gesture gesture = gestures.get(i);

            if (gesture == null){
                imageViews.get(i).setBackground(null);
                imageViews.get(i).setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_help, null));
                textViews.get(i).setText("No Match");
            } else {
                Path path = gestures.get(i).getDisplayPath(100, 100);
                RectF bound = new RectF();
                path.computeBounds(bound, true);
                ShapeDrawable shapeDrawable = new ShapeDrawable(new PathShape(path, bound.width(), bound.height()));

                shapeDrawable.getPaint().setColor(Color.BLACK);
                shapeDrawable.getPaint().setStrokeWidth(5);
                shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                imageViews.get(i).setBackground(shapeDrawable);
                imageViews.get(i).setImageDrawable(null);
                textViews.get(i).setText("No." + (i+1) + ": " + gesture.name);
            }
        }
//        Path path = new Path();


    }
}