package com.example.myapplication.Control_Interfaces;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public   class Livingroom extends Fragment {
    private Animation rotateAnimation;
    private ImageView picAir,picFan,picLight;
    private Switch sw_Air,sw_Fan,sw_Light;
    TextView Temp, Humid;
    private boolean stateAir,stateFan,stateLight;
    //String light,fan, air_conditioner;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_livingroom, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // đọc id cho các đối tượng từ xml
        anhxa();
        //thay doi database realtime

        final DatabaseReference light = database.getReference("Livingroom/Light");
        final DatabaseReference fan = database.getReference("Livingroom/Fan");
        final DatabaseReference air_conditioner = database.getReference("Livingroom/Air_conditioner");

        final DatabaseReference humidity = database.getReference("Livingroom/Humidity");
        final DatabaseReference temperature = database.getReference("Livingroom/Temperature");

        // Đọc nhiệt độ và độ ẩm từ Firebase

        temperature.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Temp.setText((snapshot.getValue()+" °C").toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        humidity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Humid.setText((snapshot.getValue()+" %").toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // đối với máy lạnh
        air_conditioner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateAir = true; //bat thiet bi máy lạnh
                    picAir.setImageResource(R.drawable.aircondition);
                    sw_Air.setChecked(true);
                    Toast.makeText(getActivity(),"Turn on Air Conditioner",Toast.LENGTH_SHORT).show();
                }else if(value.equals("Close")){
                    stateAir=false;//tat thiet bi máy lạnh
                    sw_Air.setChecked(false);
                    picAir.setImageResource(R.drawable.airconditionoff);
                    Toast.makeText(getActivity(),"Turn off Air Conditioner",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Đối với ĐÈN
        light.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value =snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateLight=true;
                    picLight.setImageResource(R.drawable.lampbulbon);
                    sw_Light.setChecked(true);
                    Toast.makeText(getActivity(),"Turn on Light",Toast.LENGTH_SHORT).show();
                }else if(value.equals("Close")){
                    stateLight = false;
                    sw_Light.setChecked(false);
                    picLight.setImageResource(R.drawable.lampbulboff);
                    Toast.makeText(getActivity(),"Turn off Light",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Đối với QUẠT
        fan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value =snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateFan=true;
                    rotateAnimation();
                    sw_Fan.setChecked(true);
                    Toast.makeText(getActivity(),"Turn on the Fan",Toast.LENGTH_SHORT).show();
                }else if(value.equals("Close")){
                    stateFan=false;
                    sw_Fan.setChecked(false);
                    picFan.clearAnimation();
                    Toast.makeText(getActivity(),"Turn off the Fan", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        sw_Air.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    air_conditioner.setValue("Open");
                }else{
                    air_conditioner.setValue("Close");
                }
            }
        });
        sw_Light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    light.setValue("Open");
                }else{
                    light.setValue("Close");
                }
            }
        });
        sw_Fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fan.setValue("Open");
                }else{
                    fan.setValue("Close");
                }
            }
        });


        picAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateAir =!stateAir;
                if(stateAir){
                    air_conditioner.setValue("Open");
                    picAir.setImageResource(R.drawable.aircondition);
                    sw_Air.setChecked(true);
                }else{
                    air_conditioner.setValue("Close");
                    picAir.setImageResource(R.drawable.airconditionoff);
                    sw_Air.setChecked(false);
                }
            }
        });
        picLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLight =!stateLight;
                if(stateLight){
                    light.setValue("Open");
                    picLight.setImageResource(R.drawable.lampbulbon);
                    sw_Light.setChecked(true);
                }else{
                    light.setValue("Close");
                    picLight.setImageResource(R.drawable.lampbulboff);
                    sw_Light.setChecked(false);
                }
            }
        });
        picFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateFan =!stateFan;
                if(stateFan){
                    fan.setValue("Open");
                    rotateAnimation();
                    sw_Fan.setChecked(true);
                }else{
                    fan.setValue("Close");
                    picFan.clearAnimation();
                    sw_Fan.setChecked(false);
                }
            }
        });

    }

    private void anhxa() {
        picAir = getActivity().findViewById(R.id.pic_air_off);
        picLight = getActivity().findViewById(R.id.pic_light_off);
        picFan = getActivity().findViewById(R.id.pic_fan_on);

        sw_Air = getActivity().findViewById(R.id.swAir);
        sw_Fan = getActivity().findViewById(R.id.swFan);
        sw_Light = getActivity().findViewById(R.id.swLight);

        Temp = getActivity().findViewById(R.id.temp);
        Humid = getActivity().findViewById(R.id.humid);

    }
    private void rotateAnimation() {
        rotateAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.rotationfan);
        picFan.startAnimation(rotateAnimation);
    }
}
