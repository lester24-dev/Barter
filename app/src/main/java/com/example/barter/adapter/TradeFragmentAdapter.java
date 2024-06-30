package com.example.barter.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.barter.trade.TradeOrderComFragment;
import com.example.barter.trade.TradeOrderFragment;
import com.example.barter.trade.TradeRefFragment;
import com.example.barter.trade.TradeReqComFragment;
import com.example.barter.trade.TradeReqsFragment;

public class TradeFragmentAdapter extends FragmentStateAdapter {
    public TradeFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public TradeFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public TradeFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new TradeReqsFragment();
            case 1:
                return new TradeReqComFragment();
            case 2:
                return new TradeOrderFragment();
            case 3:
                return new TradeOrderComFragment();
            default:
                return new TradeRefFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
