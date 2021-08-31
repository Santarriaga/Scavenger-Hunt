package com.grumpy.scavengerhunt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grumpy.scavengerhunt.Models.ImagePost;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {


    private ArrayList<ImagePost> mListOfPost = new ArrayList<>();
    private Fragment fragment;


    public ImagesAdapter(Fragment fragment, ArrayList<ImagePost> postList){
        this.fragment = fragment;
        this.mListOfPost = postList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_post_second_type, parent,false);

        return  new ImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ImagePost post = mListOfPost.get(position);
        holder.setmPostImage(post.getImage());

    }

    @Override
    public int getItemCount() {
        return mListOfPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mPostedPic;
        private View mView;

        public ViewHolder (@NonNull View itemView){
            super (itemView);

            mPostedPic = itemView.findViewById(R.id.imagePost);
            mView = itemView;
        }

        public void setmPostImage(String image){

            if(image != null){
                Glide.with(mView.getContext()).load(image).into(mPostedPic);
            }

        }


    }
}
