package com.grumpy.scavengerhunt.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grumpy.scavengerhunt.ImagePostAdapter;
import com.grumpy.scavengerhunt.ImagesAdapter;
import com.grumpy.scavengerhunt.Models.ImagePost;
import com.grumpy.scavengerhunt.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchBar;
    private ImagesAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private Query query;

    private ArrayList<ImagePost> items = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_search, container, false);

         recyclerView = view.findViewById(R.id.recView);
         searchBar = view.findViewById(R.id.searchBar);


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        adapter = new ImagesAdapter(this, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));


        //firebase retrieves of all posts
        if (mAuth.getCurrentUser() != null) {
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    Boolean isReached = !recyclerView.canScrollVertically(1);
//                    if (isReached)
//                        Toast.makeText(getContext(), "Reached bottom", Toast.LENGTH_SHORT).show();
//                }
//            });

            query = firestore.collection("Posts").orderBy("time" , Query.Direction.DESCENDING);
            listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange documentChange : value.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            ImagePost model = documentChange.getDocument().toObject(ImagePost.class);
                            items.add(model);
                            adapter.notifyDataSetChanged();
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listenerRegistration.remove();
                }
            });
        }

        //Todo add search feature

        return view;
    }
}