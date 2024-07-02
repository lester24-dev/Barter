package com.example.barter.darkmode;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences mySharedPref;
    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("file name", Context.MODE_PRIVATE);
    }
    public void  setNightModeState(Boolean state){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("Night Mode",state);
        editor.commit();
    }

    public Boolean loadNightModeState(){
        Boolean state = mySharedPref.getBoolean("Night Mode", false);
        return state;
    }
}
