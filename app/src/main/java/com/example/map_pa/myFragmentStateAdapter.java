package com.example.map_pa;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class myFragmentStateAdapter extends FragmentStateAdapter{

    String user;

    public myFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, String username) {
        super(fragmentActivity);
        user = username;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString("username", user);
                PersonalFragment personalFragment = new PersonalFragment();
                personalFragment.setArguments(bundle);
                return personalFragment;
            case 1:
                Bundle bundle2 = new Bundle();
                bundle2.putString("username", user);
                PublicFragment publicFragment = new PublicFragment();
                publicFragment.setArguments(bundle2);
                return publicFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}