package com.grumpy.scavengerhunt;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.grumpy.scavengerhunt.Fragments.HomeFragment;
import com.grumpy.scavengerhunt.Fragments.ProfileEditFragment;
import com.grumpy.scavengerhunt.Fragments.ProfileFragment;
import com.grumpy.scavengerhunt.Fragments.SearchFragment;


// Adapter for view pager on main activity
// lets user navigate through tabs by swiping

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new ProfileFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}