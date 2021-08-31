package com.grumpy.scavengerhunt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grumpy.scavengerhunt.Models.Day;
import com.grumpy.scavengerhunt.Models.ImagePost;

import java.util.ArrayList;
import java.util.Calendar;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder>  {

    private Fragment fragment;
    private ArrayList<Day> mListDays;
    private Context context;


    public DayAdapter(Fragment fragment){
        this.fragment = fragment;
    }

    public DayAdapter(Fragment fragment, ArrayList<Day> list, Context context){
        this.fragment = fragment;
        this.mListDays = list;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.curentday_card, parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mCurrentDate.setText(mListDays.get(position).getCurrentDay());
        Calendar c = Calendar.getInstance();
        String dt = Integer.toString(c.get(Calendar.DATE));

        // TODO: 8/17/2021 fix highlighted date and add daily prompts 
        if( mListDays.get(position).getCurrentDay().equals(dt)){
            holder.parent.setStrokeColor(context.getColor(R.color.custom_pink));
//            holder.mLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.ic_background));
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, mListDays.get(position).getCurrentDay() + " was selected", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return mListDays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mCurrentDate;
        ConstraintLayout mLayout;
        MaterialCardView parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mCurrentDate = itemView.findViewById(R.id.text_date);
            mLayout = itemView.findViewById(R.id.layoutBG);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
