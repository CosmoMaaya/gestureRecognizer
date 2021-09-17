package ca.uwaterloo.cs349;


import android.graphics.*;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Gesture implements Parcelable, Serializable {

    String name;
    ArrayList<float[]> standardizedPoints;
    ArrayList<float[]> originalPoints;
    public Gesture(){
        standardizedPoints = new ArrayList<>();
        originalPoints = new ArrayList<>();
    }
    static final int sampleNumber = 512;

    public Gesture(Path path, String name){
        standardizedPoints = new ArrayList<>();
        originalPoints = new ArrayList<>();
        this.name = name;

        if (path == null) return;

        PathMeasure pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();
        float[] pos = {0,0};
        for(int i = 0; i < sampleNumber; i++){
            if(pathMeasure.getPosTan(length/sampleNumber * i, pos, null)){
                this.addPoint(pos);
            }
        }

    }

    public void addPoint(float[] pos){
        standardizedPoints.add(new float[]{pos[0], pos[1]});
        originalPoints.add(new float[]{pos[0], pos[1]});
    }

    public void standardize(){
        rotateToZero();
        scaleAndTranslate();
//        for(float[] p: standardizedPoints){
//            Log.d("Gesture", p[0] + " " + p[1]);
//        }
    }

    private void rotateToZero(){
        if (standardizedPoints.size() <= 0) return;

        float[] centroid = findCentroid(standardizedPoints);
        float[] startPoint = standardizedPoints.get(0);

        Matrix transform = new Matrix();
        double angle = Math.atan2(startPoint[1] - centroid[1], startPoint[0] - centroid[0]) * 180 / Math.PI;
        Log.d("SampleLine", "Angle: " + angle);

        //No big difference, ignored
        if (angle > -1 && angle < 1){
            return;
        }


        transform.setRotate((float) -angle, centroid[0], centroid[1]);

        for (float[] pos: standardizedPoints){
//            Log.d("SampleLine", "before rotate x = " + pos[0] + " y = " + pos[1]);
            transform.mapPoints(pos);
//            Log.d("SampleLine", "After rotate x = " + pos[0] + " y = " + pos[1]);
        }
    }

    private void scale(ArrayList<float[]> points, float width, float height, float x, float y){
        Path path = getPath(false, null);
        if (path == null) return;
        RectF bound = new RectF();
        path.computeBounds(bound, true);

        float widthScale = width/ bound.width();
        float heightScale = height/ bound.height();

        Matrix transform = new Matrix();
        transform.setScale(widthScale, heightScale, x, y);

        for (float[] pos: points){
            transform.mapPoints(pos);
        }
    }

    private void scaleAndTranslate(){

        Matrix transform = new Matrix();

        float[] centroid = findCentroid(standardizedPoints);
        transform.setTranslate(-centroid[0], -centroid[1]);
//        transform.postScale(widthScale, heightScale);

//        transform.setScale(widthScale, heightScale);
//        transform.preTranslate(-centroid[0], -centroid[1]);
        for (float[] pos: standardizedPoints){
            transform.mapPoints(pos);
        }

        scale(standardizedPoints, 400, 400, 0, 0);

//        centroid = findCentroid();
//        path = getPath();
//        path.computeBounds(bound, true);
//        Log.d("SampledLine", "Centroid = [" + centroid[0] + ", " + centroid[1] + "] ");
//        Log.d("SampledLine", "Bound = [" + bound.width() + ", " + bound.height() + "] ");
    }

    public float distance(Path path){
        Gesture operand = new Gesture(path, "");
        return distance(operand);
    }

    public float distance(Gesture operand){
        if (operand.standardizedPoints.size() != sampleNumber || standardizedPoints.size() != sampleNumber) {
            throw new IllegalArgumentException("Size not match");
        }

        double distance = 0;
        for (int i = 0; i < sampleNumber; i ++){
            distance += Math.sqrt(Math.pow(standardizedPoints.get(i)[0] - operand.standardizedPoints.get(i)[0], 2) + Math.pow(standardizedPoints.get(i)[1] - operand.standardizedPoints.get(i)[1], 2));
        }

        distance /= sampleNumber;
//        Log.d("Gesture", "This Size: " + this.points.size() + " Operand Size: " + operand.points.size());
//        Log.d("Gesture", "Distance: " + distance);
        return (float) distance;
    }

    public float[] findCentroid(ArrayList<float[]> points){
        if (points.size() <= 0) return null;

        float x = 0;
        float y = 0;

        for (float[] pos : points){
            x += pos[0];
            y += pos[1];
        }

        x /= points.size();
        y /= points.size();

        return new float[]{x, y};
    }

    public Path getPath(Boolean original, ArrayList<float[]> _points){
        ArrayList<float[]> points = standardizedPoints;
        if (original){
            points = originalPoints;
        }
        if (_points != null){
            points = _points;
        }

        if(points.size() <= 0) return null;

        Path path = new Path();
//        path.moveTo(findCentroid()[0], findCentroid()[1]);
        path.moveTo(points.get(0)[0], points.get(0)[1]);
        for (float[] pos: points){
            path.lineTo(pos[0], pos[1]);
        }

        return path;
    }

    public Path getDisplayPath(float width, float height){
        ArrayList<float[]> points = new ArrayList<>();

        for(float[] pos: originalPoints){
            points.add(new float[]{pos[0], pos[1]});
        }

        float[] centroid = findCentroid(points);
        scale(points, 150, 150, centroid[0], centroid[1]);

        Path path = getPath(true, points);
        RectF bound = new RectF();
        path.computeBounds(bound, true);

        Matrix matrix = new Matrix();
        matrix.setTranslate(-bound.left, -bound.top);

        for (float[] pos: points){
            matrix.mapPoints(pos);
        }


        return getPath(false, points);
    }

    //Parcelable
    public Gesture(Parcel source){
        name = source.readString();
        int size = source.readInt();
        standardizedPoints = new ArrayList<>();
        for (int i = 0; i < size; i++){
            float[] point = {0,0};
            source.readFloatArray(point);
            standardizedPoints.add(point);
        }
        originalPoints = new ArrayList<>();
        size = source.readInt();
        for (int i = 0; i < size; i++){
            float[] point = {0,0};
            source.readFloatArray(point);
            originalPoints.add(point);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(standardizedPoints.size());
        for(float[] p: standardizedPoints){
            dest.writeFloatArray(p);
        }
        dest.writeInt(originalPoints.size());
        for(float[] p: originalPoints){
            dest.writeFloatArray(p);
        }
    }

    public static final Creator<Gesture> CREATOR = new Creator<Gesture>() {
        @Override
        public Gesture createFromParcel(Parcel source) {
            return new Gesture(source);
        }

        @Override
        public Gesture[] newArray(int size) {
            return new Gesture[size];
        }
    };

    public boolean isEmpty(){
        return originalPoints.size() <= 0 || standardizedPoints.size() <= 0;
    }
}
