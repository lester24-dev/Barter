package com.example.barter.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.barter.home.ui.home.CategoryFragment;
import com.example.barter.home.ui.home.MatchFragment;
import com.example.barter.home.ui.home.ProductFragment;

public class HomeFragmentAdapter extends FragmentStateAdapter {
    public HomeFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public HomeFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public HomeFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ProductFragment();
            default:
                return new CategoryFragment();
               // return new MatchFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
