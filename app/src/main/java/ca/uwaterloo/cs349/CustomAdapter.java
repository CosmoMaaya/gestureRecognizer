package ca.uwaterloo.cs349;


import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<Gesture> storedGestures;

    private View.OnClickListener editListener;
    private final LibraryFragment mFragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageButton delButton, editButton;
        private final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
            delButton = v.findViewById(R.id.buttonDel);
            editButton = v.findViewById(R.id.buttonEdit);
            imageView = v.findViewById(R.id.imageView_pathThumbnail);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageButton getDelButton(){
            return delButton;
        }

        public ImageButton getEditButton(){
            return editButton;
        }

        public ImageView getImageView(){return imageView;};
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    public CustomAdapter(LibraryFragment fragment, ArrayList<Gesture> dataSet) {
        storedGestures = dataSet;
        mFragment = fragment;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d("Adapter", "onCreateViewHolder");
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.gesture_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextView().setText(storedGestures.get(position).name);
        viewHolder.getTextView().setTextSize(25);

        ImageButton deleteBtn = viewHolder.getDelButton();
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storedGestures.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, storedGestures.size());
            }
        });

        ImageButton editBtn = viewHolder.getEditButton();
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toEditActivity(storedGestures.get(position), position);
            }
        });

        ImageView imageView = viewHolder.getImageView();
        Path path = storedGestures.get(position).getDisplayPath(150, 150);
//        Path path = new Path();
        RectF bound = new RectF();
        path.computeBounds(bound, true);

        Matrix matrix = new Matrix();
        matrix.setTranslate(50,50);
        path.transform(matrix);

        ShapeDrawable shapeDrawable = new ShapeDrawable(new PathShape(path,bound.width()+100,bound.height()+100));
        shapeDrawable.getPaint().setColor(Color.BLACK);
        shapeDrawable.getPaint().setStrokeWidth(10);
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        imageView.setBackground(shapeDrawable);
        imageView.setImageDrawable(shapeDrawable);
    }

    private void toEditActivity(Gesture gestureToEdit, int position) {
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("gesture", gestureToEdit);
        mBundle.putInt("position", position);

        if (mFragment != null){
            mFragment.toEditActivity(mBundle);
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return storedGestures.size();
    }

    public void setEditListener(View.OnClickListener listener){
        editListener = listener;
    }
}

