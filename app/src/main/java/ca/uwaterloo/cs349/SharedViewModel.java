package ca.uwaterloo.cs349;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.*;
import java.util.ArrayList;

public class SharedViewModel extends ViewModel implements Serializable {

    private MutableLiveData<String> mText;

    Path unFinishedAdditionPath, recognitionPath;
    RectF boundDebugger;
    PathMeasure additionSample, recognitionSample;
    ArrayList<Gesture> storedGestures;
    File fileDir;

    public SharedViewModel() {
        storedGestures = new ArrayList<>();
        mText = new MutableLiveData<>();
        mText.setValue("This is shared model");

    }

    public LiveData<String> getText() {
        return mText;
    }

    public boolean addGesture(Path path, String name){
        if(path == null || path.isEmpty()) return false;
        Log.d("VM", "processLine");

        Gesture gesture = new Gesture(path, name);
        gesture.standardize();

        storedGestures.add(gesture);
        store();

        return true;
    }

    public void store(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(fileDir, MainActivity.filename);
                try (ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(file))) {
                    Log.d("Main", "Writing");
                    fos.writeObject(storedGestures);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public ArrayList<Gesture> bestMatches(Gesture gesture){
        //Best three, manually solve!!
        Gesture[] bestThree = {null,null,null};
        float[] bestThreeValue = {Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY};
        for(Gesture iterate: storedGestures){
            float curValue = iterate.distance(gesture);
            Log.d("Model", iterate.name + ": " + curValue);

            if (curValue <= bestThreeValue[0]){
                //Move everything
                bestThreeValue[2] = bestThreeValue[1];
                bestThree[2] = bestThree[1];
                bestThreeValue[1] = bestThreeValue[0];
                bestThree[1] = bestThree[0];

                bestThreeValue[0] = curValue;
                bestThree[0] = iterate;
            } else if (curValue <= bestThreeValue[1]){
                bestThreeValue[2] = bestThreeValue[1];
                bestThree[2] = bestThree[1];

                bestThreeValue[1] = curValue;
                bestThree[1] = iterate;
            } else if (curValue <= bestThreeValue[2]) {
                bestThreeValue[2] = curValue;
                bestThree[2] = iterate;
            }
        }

        ArrayList<Gesture> ans = new ArrayList<>();
        for (Gesture iterate: bestThree){
                ans.add(iterate);
        }

        return ans;
    }

    public void setFileDir(File file){fileDir = file;}

}