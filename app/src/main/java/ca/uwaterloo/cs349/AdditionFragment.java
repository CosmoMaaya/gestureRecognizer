package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class AdditionFragment extends Fragment implements MyFragment{

    private SharedViewModel mViewModel;
    private DrawingView drawingView;
    private String defaultText = "My great gesture";
    Button addButton, clearButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addition, container, false);
        drawingView = root.findViewById(R.id.drawingView_addition);
        drawingView.setPath(mViewModel.unFinishedAdditionPath);
        drawingView.setParentFragment(this);

        addButton = root.findViewById(R.id.button_add);
        clearButton = root.findViewById(R.id.button_clear);
        if (drawingView.path == null || drawingView.path.isEmpty()){
            addButton.setEnabled(false);
            clearButton.setEnabled(false);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClicked(v);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        return root;
    }

    public void clear(){
        drawingView.path.reset();
        drawingView.invalidate();
        addButton.setEnabled(false);
        clearButton.setEnabled(false);
    }


    public void onAddButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        if (drawingView.path == null) {
            builder.setTitle("Illegal");
            builder.setMessage("Please draw a gesture first");
        }

        //if length too short, cannot add the gesture
        PathMeasure pm = new PathMeasure(drawingView.path, true);
        if (pm.getLength() < 300) {
            Toast toast = Toast.makeText(requireActivity(), "Please draw a longer gesture", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        builder.setTitle("Gesture Name");

        // Set up the input
        final EditText input = new EditText(requireActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(defaultText);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String gestureName = input.getText().toString();
                if (gestureName.equals("")) gestureName = defaultText;
                mViewModel.addGesture(drawingView.path, gestureName);
                clear();
                Toast toast = Toast.makeText(requireActivity(), "Gesture Added: " + gestureName, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Add", "Destroy view");
        mViewModel.unFinishedAdditionPath = drawingView.getPath();
    }


    @Override
    public void fragmentOnMotionUp() {
        if (drawingView.path == null || drawingView.path.isEmpty()){
            addButton.setEnabled(false);
            clearButton.setEnabled(false);
        } else  {
            addButton.setEnabled(true);
            clearButton.setEnabled(true);
        }
    }
}