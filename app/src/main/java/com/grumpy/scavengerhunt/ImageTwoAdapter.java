package com.grumpy.scavengerhunt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grumpy.scavengerhunt.Models.ImagePost;

import java.util.ArrayList;

public class ImageTwoAdapter extends RecyclerView.Adapter<ImageTwoAdapter.ViewHolder> {

    private ArrayList<ImagePost> postList  = new ArrayList<>();

    public ImageTwoAdapter( ArrayList<ImagePost> postList){
        this.postList = postList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_post_third_type, parent,false);

        return  new ImageTwoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImagePost post = postList.get(position);
        holder.setImage(post.getImage());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImage;
        private View mView;


        public ViewHolder (@NonNull View itemView){
            super (itemView);

            mImage = itemView.findViewById(R.id.image);
            mView = itemView;
        }

        public void setImage(String image){

            if(image != null){
                Glide.with(mView.getContext()).load(image).into(mImage);
            }

        }
    }

}
