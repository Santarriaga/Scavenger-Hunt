package com.grumpy.scavengerhunt.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grumpy.scavengerhunt.Activities.AddPostActivity;
import com.grumpy.scavengerhunt.Activities.LoginActivity;
import com.grumpy.scavengerhunt.Activities.ProfileSetUpActivity;
import com.grumpy.scavengerhunt.ImageTwoAdapter;
import com.grumpy.scavengerhunt.ImagesAdapter;
import com.grumpy.scavengerhunt.Models.ImagePost;
import com.grumpy.scavengerhunt.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment  {

    private CircleImageView circleImageView;
    private TextView mUserName;
    private Button editProfileBtn;
    private RecyclerView recyclerView;
    private ImageTwoAdapter adapter;
    private Button settingsBtn;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private StorageReference storage;
    private String Uid;
    private Uri mImageUri;
    private ListenerRegistration listenerRegistration;
    private Query query;

    private ArrayList<ImagePost> listOfPost = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        circleImageView = view.findViewById(R.id.profilePic);
        mUserName =  view.findViewById(R.id.txtUserName);
        editProfileBtn = view.findViewById(R.id.editBtn);
        recyclerView = view.findViewById(R.id.rv);
        settingsBtn = view.findViewById(R.id.settings);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        Uid = auth.getCurrentUser().getUid();


        //sets up recyclerView
        adapter = new ImageTwoAdapter( listOfPost);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));


        //retrieve Username and profile picture
        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String profile = task.getResult().getString("image");

                        mImageUri = Uri.parse(profile);
                        mUserName.setText(name);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.ic_profile);

                        Glide.with(getContext()).load(profile).into(circleImageView);
                    }
                }
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getActivity();
                PopupMenu popUp = new PopupMenu(context, view);
                popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.logOut:
                                auth.signOut();
                                startActivity(new Intent(getActivity() , LoginActivity.class));
                                return true;

                            case R.id.about:
                                Toast.makeText(context, "About Selected", Toast.LENGTH_SHORT).show();
                                return true;

                            default:
                                return false;
                        }

                    }
                });
                popUp.inflate(R.menu.popup_menu);
                popUp.show();
            }
        });


        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProfileSetUpActivity.class));
            }
        });


        //collects all post from user
        query = firestore.collection("Posts").whereEqualTo("user",Uid);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        ImagePost model = documentChange.getDocument().toObject(ImagePost.class);
                        listOfPost.add(model);
                        adapter.notifyDataSetChanged();
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });

        return view;
    }


}