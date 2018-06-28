package com.example.minyoung.finding_dog.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.minyoung.finding_dog.R;

/**
 * Created by minyoung on 2018-06-28.
 */

public class register_fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    Context mContext;
    EditText editTextSpecies;
    EditText editTextLocation;
    EditText editTextFeature;
    Button buttonSaveDog;
    Button buttonChoose;
    Button buttonUpload;

    public register_fragment(){
        // Required empty public constructor
    }

    public static register_fragment newInstance(String param1, String param2) {
        register_fragment fragment = new register_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // EditText 생성
        editTextSpecies = view.findViewById(R.id.editTextSpecies);
        editTextLocation = view.findViewById(R.id.editTextLocation);;
        editTextFeature = view.findViewById(R.id.editTextFeature);;

        //Button 생성
        buttonSaveDog = view.findViewById(R.id.buttonSaveDog);
        buttonChoose = view.findViewById(R.id.buttonChoose);
        buttonUpload = view.findViewById(R.id.buttonUpload);

        //Button 클릭 기능 생성
        buttonSaveDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String species = editTextSpecies.getText().toString();
                String location = editTextLocation.getText().toString();
                String feature = editTextFeature.getText().toString();
                Dog dog = new Dog(species, location, feature);

            }
        });
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }



}
