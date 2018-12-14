package io.ishtiaque06.www.mealtracker.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ishtiaque06.www.mealtracker.R;


public class updateMealsFragment extends Fragment {

    public updateMealsFragment() {
        // Required empty public constructor
    }

    public static updateMealsFragment newInstance() {
        return new updateMealsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_meals, container, false);
    }
}
