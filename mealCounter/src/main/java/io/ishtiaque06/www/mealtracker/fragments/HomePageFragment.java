package io.ishtiaque06.www.mealtracker.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import io.ishtiaque06.www.mealtracker.R;
import io.ishtiaque06.www.mealtracker.SharedViewModel;


public class HomePageFragment extends Fragment {
    private String TAG = "HomePageFragment";
    private SharedViewModel mViewModel;
    private Button mSwipeButton;
    private Button mUpdateButton;
    private TextView mMealCount;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);
        mSwipeButton = v.findViewById(R.id.swipe_meal);
        mUpdateButton = v.findViewById(R.id.update_meals_button);
        mMealCount = v.findViewById(R.id.meal_count);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        //Sets up an observer that observes for changes in mealCount for the logged in user.
        ValueEventListener mealListener = mViewModel.getDataChangeListener();
        mViewModel.getMealCountfromDatabase()
                .addValueEventListener(mealListener);
        final Observer<Object> mealCountObserver  = new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object o) {
                mMealCount.setText(String.valueOf(o));
            }
        };
        mViewModel.getCurrentMealCount().observe(getViewLifecycleOwner(), mealCountObserver);

        // OnClickListener on mSwipeButton. Swipes a meal off and deducts it from Firebase.
        mSwipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Long) mViewModel.getCurrentMealCount().getValue() == 0) {
                    Toast.makeText(getActivity(), "You have no swipes left!",
                            Toast.LENGTH_SHORT).show();
                }
                else {

                    mViewModel.swipeMeal();
                }
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.updateMealsFragment);
            }
        });
    }
}