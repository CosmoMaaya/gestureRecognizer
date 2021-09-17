package ca.uwaterloo.cs349;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class LibraryFragment extends Fragment {

    private SharedViewModel mViewModel;

    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Gesture> mDataset;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("Library", "onCreate");
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        mDataset = mViewModel.storedGestures;

        mRecyclerView = root.findViewById(R.id.recyclerView_library);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CustomAdapter(this, mDataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Library", "Destroy view");
//        mViewModel.unFinishedAdditionPath = drawingView.getPath();
    }


    public void toEditActivity(Bundle mBundle) {
        Intent intent = new Intent(requireActivity(), EditActivity.class);
        intent.putExtras(mBundle);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("Library", "onPause");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("Library", "onResume");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Library", "onActivityResult");
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            Gesture gesture = bundle.getParcelable("gesture");
            int position = bundle.getInt("position");
            mDataset.set(position, gesture);
            mAdapter.notifyItemChanged(position);
            mViewModel.store();
        }
    }
}