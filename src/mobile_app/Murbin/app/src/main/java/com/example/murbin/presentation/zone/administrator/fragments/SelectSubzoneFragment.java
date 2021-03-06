package com.example.murbin.presentation.zone.administrator.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.murbin.App;
import com.example.murbin.R;
import com.example.murbin.data.SubzonesDatabaseCrud;
import com.example.murbin.data.adapters.SelectSubzonesAdapter;
import com.example.murbin.models.Subzone;
import com.example.murbin.presentation.global.fragments.SubzoneCheckboxesDialogFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SelectSubzoneFragment extends Fragment {
    public SelectSubzonesAdapter subzonesListAdapter;

    private RecyclerView recyclerView;
    private SubzonesDatabaseCrud subzonesDatabaseCrud;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subzonesDatabaseCrud = new SubzonesDatabaseCrud();
        setHasOptionsMenu(true); // For onOptionsItemSelected to work
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.global_recycler_layout, container, false);
        initializeLayoutElements(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        subzonesListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        subzonesListAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subzonesListAdapter.stopListening();
    }

    /**
     * Method to start the layout elements
     *
     * @param view View
     */
    private void initializeLayoutElements(View view) {
        FirestoreRecyclerOptions<Subzone> options = new FirestoreRecyclerOptions.Builder<Subzone>()
                .setQuery(subzonesDatabaseCrud.getSubzones(), Subzone.class).build();

        subzonesListAdapter = new SelectSubzonesAdapter(options, getContext());
        subzonesListAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                String id = subzonesListAdapter.getId(position);
                Log.d(App.DEFAULT_TAG, "id:" + id);
                SubzoneCheckboxesDialogFragment.listSubzones.add(id);
                Log.d(App.DEFAULT_TAG, "listSubzones:" + SubzoneCheckboxesDialogFragment.listSubzones.toString());
            }
        });

        recyclerView = view.findViewById(R.id.global_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(subzonesListAdapter);

        subzonesListAdapter.startListening();
    }
}
