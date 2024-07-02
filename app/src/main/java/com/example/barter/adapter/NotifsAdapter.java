package com.example.barter.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.barter.notification.BlackNotifFragment;
import com.example.barter.notification.TradeNotifFragment;
import com.example.barter.trade.TradeOrderComFragment;
import com.example.barter.trade.TradeOrderFragment;
import com.example.barter.trade.TradeRefFragment;
import com.example.barter.trade.TradeReqComFragment;
import com.example.barter.trade.TradeReqsFragment;

public class NotifsAdapter extends FragmentStateAdapter {
    public NotifsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public NotifsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public NotifsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new TradeNotifFragment();
            default:
                return new BlackNotifFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
