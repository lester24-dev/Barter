package com.example.barter.home.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.home.ui.categories.CategoryActivity;


public class CategoryFragment extends Fragment {


    View root;
   CardView male_clothes, female_clothes, gadget, appliances,unisex_clothes, unisex_acc, sport_a,
           male_as, female_shs, sport_acs, music, kid, furniture, baby, auto_part,muvie, healbe, garden, pet,
           game, vehicle,miscellaneous, food;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPref sharedPref = new SharedPref(getContext());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.ThemeDark);
        } else {
            getActivity().setTheme(R.style.ThemeLight);
        }
        root =  inflater.inflate(R.layout.fragment_category, container, false);

        male_clothes = root.findViewById(R.id.male_c);
        female_clothes = root.findViewById(R.id.female_c);
        gadget = root.findViewById(R.id.gadget);
        appliances = root.findViewById(R.id.appliance);
        unisex_acc = root.findViewById(R.id.unix_c);
        unisex_clothes = root.findViewById(R.id.unix_a);
        sport_a = root.findViewById(R.id.sport_a);
        male_as = root.findViewById(R.id.male_as);
        female_shs = root.findViewById(R.id.female_shs);
        sport_acs = root.findViewById(R.id.sport_acs);
        music = root.findViewById(R.id.music);
        kid = root.findViewById(R.id.kid);
        furniture = root.findViewById(R.id.furniture);
        baby = root.findViewById(R.id.baby);
        auto_part = root.findViewById(R.id.auto_part);
        muvie = root.findViewById(R.id.muvie);
        healbe = root.findViewById(R.id.healbe);
        garden = root.findViewById(R.id.garden);
        pet = root.findViewById(R.id.pet);
        game = root.findViewById(R.id.game);
        vehicle = root.findViewById(R.id.vehicle);
        miscellaneous = root.findViewById(R.id.miscellaneous);
        food = root.findViewById(R.id.food);


        male_clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Male Clothes");
                startActivity(intent);
            }
        });

        female_clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Women Clothes");
                startActivity(intent);
            }
        });

        male_as.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Male Accessories And Shoes");
                startActivity(intent);
            }
        });

        female_shs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Female Accessories And Shoes");
                startActivity(intent);
            }
        });

        gadget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Gadget Accessories");
                startActivity(intent);
            }
        });


        appliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Appliances");
                startActivity(intent);
            }
        });

        unisex_clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Unisex Accessories And Shoes");
                startActivity(intent);
            }
        });

        unisex_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Unisex Clothes");
                startActivity(intent);
            }
        });

        sport_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Sport Accessories");
                startActivity(intent);
            }
        });

        sport_acs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Sport Clothes And Shoes");
                startActivity(intent);
            }
        });

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Music and Intruments");
                startActivity(intent);
            }
        });

        kid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Kids Fashion and Accessories");
                startActivity(intent);
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Furniture");
                startActivity(intent);
            }
        });

        baby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Baby");
                startActivity(intent);
            }
        });

        auto_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Auto Parts");
                startActivity(intent);
            }
        });

        muvie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Books, Movies & Music");
                startActivity(intent);
            }
        });

        healbe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Healthy and Beauty");
                startActivity(intent);
            }
        });

        garden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Patio and Garden");
                startActivity(intent);
            }
        });

        pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Pet Supplies");
                startActivity(intent);
            }
        });

        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Toy and Games");
                startActivity(intent);
            }
        });

        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Vehicles");
                startActivity(intent);
            }
        });

        miscellaneous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Miscellaneous");
                startActivity(intent);
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("product_category","Foods");
                startActivity(intent);
            }
        });


        return root;
    }
}