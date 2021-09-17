package ca.uwaterloo.cs349;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private SharedViewModel mViewModel;
    public final static String filename = "saveFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_library, R.id.navigation_addition)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        mViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mViewModel.setFileDir(this.getFilesDir());

        File file = new File(this.getFilesDir(), filename);

        if(file.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                mViewModel.storedGestures = (ArrayList<Gesture>) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("Main", "onStop");
        File file = new File(this.getFilesDir(), filename);
        try (ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(file))) {
            Log.d("Main", "Writing");
            fos.writeObject(mViewModel.storedGestures);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}