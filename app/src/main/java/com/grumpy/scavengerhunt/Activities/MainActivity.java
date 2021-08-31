package com.grumpy.scavengerhunt.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grumpy.scavengerhunt.R;
import com.grumpy.scavengerhunt.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String currentUSerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove status bar
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initializeTabs();

    }

    private void initializeTabs(){
        // links tabs to viewpager and initializes them
       new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
//                        tab.setText(R.string.label_1);
                        tab.setIcon(R.drawable.ic_baseline_home_24);
                        break;
                    case 1:
//                        tab.setText(R.string.label_2);
                        tab.setIcon(R.drawable.ic_baseline_search_24);
                        break;
//                    case 2:
//                        tab.setText(R.string.label_3);
//                        tab.setIcon(R.drawable.ic_social_activity);
//                        break;
                    case 2:
//                        tab.setText(R.string.label_4);
                        tab.setIcon(R.drawable.ic_profile);
                        break;
                }
            }

        }).attach();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            startActivity(new Intent(MainActivity.this , LoginActivity.class));
            finish();
        }else{

            currentUSerId = mAuth.getCurrentUser().getUid();
            firestore.collection("Users").document(currentUSerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){

                            //navigate to profile setup
//                            viewPager.setCurrentItem(2);
                            startActivity(new Intent(MainActivity.this,ProfileSetUpActivity.class));
                            finish();
                        }
                    }

                }
            });
        }
    }
}