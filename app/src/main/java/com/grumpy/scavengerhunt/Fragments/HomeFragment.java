package com.grumpy.scavengerhunt.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grumpy.scavengerhunt.Activities.AddPostActivity;
import com.grumpy.scavengerhunt.DayAdapter;
import com.grumpy.scavengerhunt.ImagePostAdapter;
import com.grumpy.scavengerhunt.Models.Day;
import com.grumpy.scavengerhunt.Models.ImagePost;
import com.grumpy.scavengerhunt.R;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImagePostAdapter adapter;
    private FloatingActionButton fab;
    private TextView year;
    private TextView month;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String currentUserId;
    private Query query;
    private ListenerRegistration listenerRegistration;

    private ArrayList<ImagePost> items = new ArrayList<>();


    private RecyclerView recViewDay;
    private DayAdapter dayAdapter;
    ArrayList<Day> days = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler);
        fab = view.findViewById(R.id.fab);
        year = view.findViewById(R.id.yearText);
        month = view.findViewById(R.id.monthText);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        adapter = new ImagePostAdapter(this, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        recViewDay = view.findViewById(R.id.listDays);

        //sets up recycler view for dates
        dayAdapter = new DayAdapter(this, days, getActivity());
        recViewDay.setAdapter(dayAdapter);
        recViewDay.setLayoutManager(new LinearLayoutManager(getActivity(), recyclerView.HORIZONTAL,false));


        //set year and month
        Calendar c = Calendar.getInstance();
        String[]monthName={"January","February","March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
        Integer[] d = {31, 28, 31,30, 31, 30, 31, 31,30, 31, 30, 31};
        String monthStr = monthName[c.get(Calendar.MONTH)];
        Integer currentYear = c.get(Calendar.YEAR);
        Integer dt = c.get(Calendar.DATE);

        for(int i=0; i < d[c.get(Calendar.MONTH)]; i++){
            Day day = new Day(Integer.toString(i+1));
            days.add(i, day);
        }
        dayAdapter.notifyDataSetChanged();
        recViewDay.getLayoutManager().scrollToPosition(dt - 3);

        //display current month and year
        month.setText(monthStr);
        year.setText(Integer.toString(currentYear));



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddPostActivity.class));
            }
        });



        //review
        if (mAuth.getCurrentUser() != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isReached = !recyclerView.canScrollVertically(1);
                    if (isReached)
                        Toast.makeText(getContext(), "Reached bottom", Toast.LENGTH_SHORT).show();
                }
            });

            query = firestore.collection("Posts").orderBy("time" , Query.Direction.DESCENDING);
            listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange documentChange : value.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            String postId =  documentChange.getDocument().getId();
                            String postuserId = documentChange.getDocument().getString("user");
                            ImagePost model = documentChange.getDocument().toObject(ImagePost.class).withId(postId);

                            firestore.collection("Users").document(postuserId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                            Users users = task.getResult().toObject(Users.class);
//                                            usersList.add(users);
                                            items.add(model);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listenerRegistration.remove();
                }
            });
        }

        return view;
    }



}