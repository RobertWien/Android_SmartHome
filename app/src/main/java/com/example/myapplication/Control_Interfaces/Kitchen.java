package com.example.myapplication.Control_Interfaces;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public   class Kitchen extends Fragment {
    private ImageView picLight_kit,picAlarm,picLine;
    private Switch sw_Line,sw_Alarm,sw_Light_kit;
    private boolean stateAlarm,stateLight_kit;
    private int stateLine;
    private TextView txtLabelLine, txtStatusLine;
    boolean check_weather_status;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_kitchen, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // đọc id cho các đối tượng từ xml
        anhxa();
        //thay doi database realtime

        final DatabaseReference light_kit = database.getReference("Kitchen/Light");
        final DatabaseReference alarm = database.getReference("Kitchen/Alarm");
        final DatabaseReference clothes_line = database.getReference("Kitchen/Clothes_line");

        // đối với dây phơi quần áo
        clothes_line.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value.equals("Out")){
                    stateLine = 0; //Kéo quần áo ra phơi
                    picLine.setImageResource(R.drawable.clothes_out);
                    sw_Line.setChecked(true);
                    txtLabelLine.setTextColor(Color.parseColor("#0000FF"));
                    txtStatusLine.setText("Pull out Clothes Line outdoors ❗️");
                    txtStatusLine.setTextColor(Color.parseColor("#0000FF"));
                    Toast.makeText(getActivity(),"Pull out Clothes Line outdoors !",Toast.LENGTH_SHORT).show();
                }
                if (value.equals("Rain_In")){
                    stateLine = 2;//Kéo quần áo vào nhà khi trời mưa
                    sw_Line.setChecked(false);
                    picLine.setImageResource(R.drawable.clothes_in_rain);
                    Toast.makeText(getActivity(), "Pull in Clothes Line because of the rain !!!", Toast.LENGTH_SHORT).show();
                    txtLabelLine.setTextColor(Color.parseColor("#E91E63"));
                    txtStatusLine.setText("Pull in Clothes Line because of the rain ❗️");
                    txtStatusLine.setTextColor(Color.parseColor("#E91E63"));

                    // thông báo kéo dây phơi quần áo vào khi trời mưa
                    new AlertDialog.Builder(getActivity())
                            .setTitle("⚠️WARNING")
                            .setMessage("Pull in Clothes Line automatically because of the rain ❗️❗️❗️️️")
                            .setCancelable(false)
                            .setPositiveButton("\uD83D\uDC4D OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                }
                if (value.equals("In")){
                    stateLine = 1;//Kéo quần áo vào nhà thủ công
                    sw_Line.setChecked(false);
                    picLine.setImageResource(R.drawable.clothes_in_manual);
                    txtLabelLine.setTextColor(Color.parseColor("#FF9933"));
                    txtStatusLine.setText("Pull in Clothes Line manually ❗️");
                    txtStatusLine.setTextColor(Color.parseColor("#FF9933"));
                    Toast.makeText(getActivity(), "Pull in Clothes Line manually ❗️", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Đối với ĐÈN
        light_kit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value =snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateLight_kit =true;
                    picLight_kit.setImageResource(R.drawable.lampbulbon);
                    sw_Light_kit.setChecked(true);
                    Toast.makeText(getActivity(),"Turn on Light",Toast.LENGTH_SHORT).show();
                }else if(value.equals("Close")){
                    stateLight_kit = false;
                    sw_Light_kit.setChecked(false);
                    picLight_kit.setImageResource(R.drawable.lampbulboff);
                    Toast.makeText(getActivity(),"Turn off Light",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Đối với Báo thức
        alarm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value =snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateAlarm = true;
                    picAlarm.setImageResource(R.drawable.alarm_on);
                    sw_Alarm.setChecked(true);
                    Toast.makeText(getActivity(),"Turn on the Alarm",Toast.LENGTH_SHORT).show();
                }else if(value.equals("Close")){
                    stateAlarm = false;
                    picAlarm.setImageResource(R.drawable.alarm_off);
                    sw_Alarm.setChecked(false);
                    Toast.makeText(getActivity(),"Turn off the Alarm", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sw_Line.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (stateLine == 1) {
                        clothes_line.setValue("Out");
                    }
                    if (stateLine == 2) {
                        sw_Line.setChecked(false);
                        new AlertDialog.Builder(getActivity())
                                .setTitle("⚠️WARNING")
                                .setMessage("It's raining, Please don't pull out your clothes line ❗️❗️❗️")
                                .setCancelable(false)
                                .setPositiveButton("\uD83D\uDC4D OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create().show();
                    }
                }else if (stateLine != 2)
                    clothes_line.setValue("In");
            }

        });


        sw_Light_kit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    light_kit.setValue("Open");
                }else{
                    light_kit.setValue("Close");
                }
            }
        });
        sw_Alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    alarm.setValue("Open");
                }else{
                    alarm.setValue("Close");
                }
            }
        });


        picLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateLine == 1) {
                    clothes_line.setValue("Out");
                    picLine.setImageResource(R.drawable.clothes_out);
                    sw_Line.setChecked(true);
                }

                if (stateLine == 0) {
                    clothes_line.setValue("In");
                    picLine.setImageResource(R.drawable.clothes_in_manual);
                    sw_Line.setChecked(true);
                    stateLine = 1;
                }

                if (stateLine == 2){
                    new AlertDialog.Builder(getActivity())
                            .setTitle("⚠️WARNING")
                            .setMessage("It's raining, Please don't pull out your clothes line ❗️❗️❗️")
                            .setCancelable(false)
                            .setPositiveButton("\uD83D\uDC4D OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                }

                if (sw_Line.isChecked()){
                    stateLine = 0;
                }
            }
        });

        picLight_kit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLight_kit =!stateLight_kit;
                if(stateLight_kit){
                    light_kit.setValue("Open");
                    picLight_kit.setImageResource(R.drawable.lampbulbon);
                    sw_Light_kit.setChecked(true);
                }else{
                    light_kit.setValue("Close");
                    picLight_kit.setImageResource(R.drawable.lampbulboff);
                    sw_Light_kit.setChecked(false);
                }
            }
        });
        picAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateAlarm =!stateAlarm;
                if(stateAlarm){
                    alarm.setValue("Open");
                    sw_Alarm.setChecked(true);
                }else{
                    alarm.setValue("Close");
                    sw_Alarm.setChecked(false);
                }
            }
        });
    }

    private void anhxa() {
        picLine = getActivity().findViewById(R.id.pic_line);
        picLight_kit = getActivity().findViewById(R.id.pic_light_off_kit);
        picAlarm = getActivity().findViewById(R.id.pic_alarm);

        sw_Line = getActivity().findViewById(R.id.swLine);
        sw_Alarm = getActivity().findViewById(R.id.swAlarm);
        sw_Light_kit = getActivity().findViewById(R.id.swLight_kit);

        txtLabelLine = getActivity().findViewById(R.id.txt_label_line);
        txtStatusLine = getActivity().findViewById(R.id.txt_status_line);


    }

}
