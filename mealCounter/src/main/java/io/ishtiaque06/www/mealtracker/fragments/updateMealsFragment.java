package io.ishtiaque06.www.mealtracker.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.Navigation;
import io.ishtiaque06.www.mealtracker.R;
import io.ishtiaque06.www.mealtracker.SharedViewModel;


public class updateMealsFragment extends Fragment {
    private Button mUpdateButton;
    private EditText mNewCount;
    private String TAG = "updateMealsFragment";
    private SharedViewModel mViewModel;

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
        View v = inflater.inflate(R.layout.fragment_update_meals, container, false);
        mViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        // Save button and EditText references
        mUpdateButton = v.findViewById(R.id.update_meals_button);
        mNewCount = v.findViewById(R.id.new_meal_count);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countData = mNewCount.getText().toString();
                Log.d(TAG, countData);
                try {
                    long dataToUpdate = Long.parseLong(countData);
                    if (dataToUpdate > 0) {
                        mViewModel.updateMealCount(dataToUpdate);
                        getActivity().onBackPressed();
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enter a valid meal count.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    Toast.makeText(getActivity(), "Please enter a valid meal count.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
