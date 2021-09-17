package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class EditActivity extends AppCompatActivity implements MyFragment{

    private SharedViewModel mViewModel;
    private DrawingView drawingView;
    Button updateButton, clearButton;
    String name;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        Intent intent = this.getIntent();
        Bundle mBundle = intent.getExtras();
        Gesture gesture = mBundle.getParcelable("gesture");
        name = gesture.name;
        setTitle("Editing Gesture: " + name);
        position = mBundle.getInt("position");

        drawingView = findViewById(R.id.drawingView_addition_edit);
        drawingView.setPath(gesture.getPath(true, null));
//        drawingView.setPath(gesture.getDisplayPath(150,150));
        drawingView.setParentFragment(this);

        updateButton = findViewById(R.id.button_update_edit);
        clearButton = findViewById(R.id.button_clear_edit);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateButtonClicked();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.path.reset();
                drawingView.invalidate();
                updateButton.setEnabled(false);
                clearButton.setEnabled(false);
            }
        });
    }

    public void onUpdateButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (drawingView.path == null){
            builder.setTitle("Illegal");
            builder.setMessage("Please draw a gesture first");
        }

        //if length too short, cannot add the gesture
        PathMeasure pm = new PathMeasure(drawingView.path, true);
        if (pm.getLength() < 300){
            Toast toast = Toast.makeText(this,"Please draw a longer gesture", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        builder.setTitle("Gesture Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(name);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String _name = input.getText().toString();
                if (!name.equals("")) name = _name;

                Gesture gesture = new Gesture(drawingView.path, name);
                gesture.standardize();

                Intent intent = new Intent();
                intent = intent.setClass(EditActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("gesture", gesture);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                Toast toast = Toast.makeText(EditActivity.this, "Gesture updated: " + gesture.name, Toast.LENGTH_SHORT);
                toast.show();
                EditActivity.this.setResult(RESULT_OK, intent);   //RESULT_OK是返回状态码
                EditActivity.this.finish(); //会触发onDestroy();
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
    public void fragmentOnMotionUp() {
        if (drawingView.path == null || drawingView.path.isEmpty()){
            updateButton.setEnabled(false);
            clearButton.setEnabled(false);
        } else  {
            updateButton.setEnabled(true);
            clearButton.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.d("EditAc", "onBackPressed");
        Intent intent = new Intent(this, MainActivity.class);

        Bundle bundle = new Bundle();
        EditActivity.this.setResult(RESULT_OK, intent);
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("EditAc", "onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("EditAc", "onDestroy");
        Intent intent = new Intent(this, MainActivity.class);

    }
}
