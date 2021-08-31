package com.grumpy.scavengerhunt;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.grumpy.scavengerhunt.Models.ImagePost;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagePostAdapter extends RecyclerView.Adapter<ImagePostAdapter.ViewHolder> {

    private static final String TAG = "ItemAdapter";

//    private Context context;
    private Fragment fragment;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private ArrayList<ImagePost> mListOfPost = new ArrayList<>();


    public ImagePostAdapter(Fragment fragment){
        this.fragment = fragment;
    }

    public ImagePostAdapter(Fragment fragment, ArrayList<ImagePost> postList){
        this.fragment = fragment;
        this.mListOfPost = postList;
    }



    @NonNull
    @Override
    public ImagePostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_post, parent,false);

        //firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePostAdapter.ViewHolder holder, int position) {
        //change tababase code out of main thread

        ImagePost post = mListOfPost.get(position);
        holder.setmPostImage(post.getImage());
        holder.setmCaption(post.getCaption());


        long milliseconds = post.getTime().getTime();
        String date = DateFormat.format("MM/dd/yyyy" , new Date(milliseconds)).toString();
        holder.setmDate(date);

        String blogPostId = post.PostId;


        String userId = post.getUser();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String username = task.getResult().getString("name");
                    String image = task.getResult().getString("image");


                    holder.setmProfilePic(image);
                    holder.setmUserName(username);
                }
                else{
                    Toast.makeText(holder.mView.getContext() , task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //like color change
        holder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Posts/" + blogPostId + "/Likes").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Map<String , Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp" , FieldValue.serverTimestamp());
                            firestore.collection("Posts/" + blogPostId + "/Likes").document(userId).set(likesMap);
                        }else{
                            firestore.collection("Posts/" + blogPostId + "/Likes").document(userId).delete();
                        }
                    }
                });
            }
        });
        firestore.collection("Posts/" + blogPostId + "/Likes").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (value.exists()) {

                        holder.mLikeBtn.setImageDrawable(holder.mView.getContext().getDrawable(R.drawable.ic_after_like));
                    } else {
                        holder.mLikeBtn.setImageDrawable(holder.mView.getContext().getDrawable(R.drawable.ic_before_like));
                    }
                }
            }
        });

        //like count
        firestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (!value.isEmpty()) {
                        int count = value.size();
                        holder.setmLikeCount(count);
                    } else {
                        holder.setmLikeCount(0);
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mListOfPost.size();
    }

    public void setmListOfPost(ArrayList<ImagePost> mListOfPost){
        this.mListOfPost = mListOfPost;
        notifyDataSetChanged();
    }

    public void clearAdapter(){
        this.mListOfPost.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
         TextView mUsername;
         TextView mLikeCount;
         TextView mDate;
         ImageView mProfilePic;
         ImageView mPostedPic;
         ImageView mLikeBtn;
         ImageView circle;
         TextView mCaption;

        private View mView;

        public ViewHolder (@NonNull View itemView){
            super (itemView);

            mLikeBtn = itemView.findViewById(R.id.like_pic);
            circle = itemView.findViewById(R.id.circle);
            mView = itemView;
        }

        public void setmPostImage(String image){
            mPostedPic = itemView.findViewById(R.id.pic);
            Glide.with(mView.getContext()).load(image).into(mPostedPic);
        }

        public void setmProfilePic(String profile){
            mProfilePic = itemView.findViewById(R.id.user_icon);
            Glide.with(mView.getContext()).load(profile).into(mProfilePic);
        }

        public void setmUserName(String userName){
            mUsername = itemView.findViewById(R.id.user_name);
            mUsername.setText(userName);
        }

        public void setmDate(String date){
            mDate = mView.findViewById(R.id.text_date);
            mDate.setText(date);
        }

        public void setmCaption(String caption){
            mCaption = mView.findViewById(R.id.caption);
            mCaption.setText(caption);
            mCaption.setVisibility(View.INVISIBLE);
        }

        public void setmLikeCount(int count){
            mLikeCount = mView.findViewById(R.id.likes);
            if(count > 0 ){
                mLikeCount.setText(count + " ");
            }
            else {
                mLikeCount.setVisibility(View.INVISIBLE);
            }

        }

    }

}
