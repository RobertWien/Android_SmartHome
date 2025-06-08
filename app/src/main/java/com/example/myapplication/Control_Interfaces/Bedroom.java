package com.example.myapplication.Control_Interfaces;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class Bedroom extends Fragment {
    private Animation rotateAnimation_bed;
    protected ImageView picAir_bed,picFan_bed,picLight_bed,Fan_controll;
    private Switch sw_Air_bed,sw_Fan_bed,sw_Light_bed;
    private TextView Temp_bed, Humid_bed, Fan_speed;

    private boolean stateAir_bed,stateFan_bed,stateLight_bed;
    //String light,fan, air_conditioner;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_bedroom, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // đọc id cho các đối tượng từ xml
        anhxa_kit();
        //thay doi database realtime

        final DatabaseReference light_bed= database.getReference("Bedroom/Light");
        final DatabaseReference fan_bed = database.getReference("Bedroom/Fan");
        final DatabaseReference air_conditioner_bed = database.getReference("Bedroom/Air_conditioner");

        final DatabaseReference humidity_bed = database.getReference("Bedroom/Humidity");
        final DatabaseReference temperature_bed = database.getReference("Bedroom/Temperature");

        final DatabaseReference fanControll = database.getReference("Bedroom/Fan_controll");
        final DatabaseReference fanSpeed = database.getReference("Bedroom/Fan_speed");

        // Đọc nhiệt độ và độ ẩm từ Firebase

        temperature_bed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Temp_bed.setText((snapshot.getValue()+" °C").toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        humidity_bed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Humid_bed.setText((snapshot.getValue()+" %").toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Đọc dữ liệu của tốc độ quay của quạt gửi lên từ phần cứng thông qua Firebase
        fanSpeed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Fan_speed.setText(("FAN SPEED: " + snapshot.getValue()+" UNIT").toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Điều khiển tốc độ quạt
        Fan_controll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Fan_controll.isSelected()) {
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setTitle("CHOOSE SPEED UNIT");
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.controll_speed);
                    //EditText editUnit = (EditText) dialog.findViewById(R.id.edit_unit);

                    EditText editUnit = (EditText) dialog.findViewById(R.id.edit_unit);

                    Button btnUnitOk = (Button) dialog.findViewById(R.id.btn_unit_ok);
                    Button btnUnitCancel = (Button) dialog.findViewById(R.id.btn_unit_cancel);

                    btnUnitOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (editUnit.length() != 0 && editUnit.length() <= 2 ) {
                                String unit = editUnit.getText().toString().trim();

                                if (unit.equals("1") || unit.equals("2") || unit.equals("3") || unit.equals("4")
                                        || unit.equals("5") || unit.equals("6") || unit.equals("7") || unit.equals("8")
                                        || unit.equals("9") || unit.equals("10") ) {

                                    fanControll.setValue(unit);
                                    dialog.cancel();
                                    Toast.makeText(getActivity(), "You changed the unit speed of fan to " + unit, Toast.LENGTH_SHORT).show();

                                } else {
                                    editUnit.setError("Please enter a valid value unit from 1 to 10");
                                    editUnit.requestFocus();
                                }

                            } else {
                                // thông báo kéo chưa nhập thông sô tốc độ quạt
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("WARNING")
                                        .setMessage("Please, Enter value for speed unit of fan and it must be from 1 to 10 !!!")
                                        .setIcon(R.drawable.ic_warning)
                                        .setCancelable(false)
                                        .setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();
                                editUnit.setError("Please enter your value here !!!");;
                                editUnit.requestFocus();
                            }
                        }
                    });
                    btnUnitCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("⚠️WARNING")
                            .setMessage("Please, turn on the fan to enter " + "Select Fan Speed Unit Panel !!!")
                            .setCancelable(false)
                            .setPositiveButton("\uD83D\uDCA1 UNDERSTAND", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();

                }
            }
        });

        // đối với máy lạnh
        air_conditioner_bed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateAir_bed = true; //bat thiet bi máy lạnh
                    picAir_bed.setImageResource(R.drawable.aircondition_bed);
                    sw_Air_bed.setChecked(true);
                    Toast.makeText(getActivity(),"Turn on Air Conditioner", Toast.LENGTH_SHORT).show();
                }else if(value.equals("Close")){
                    stateAir_bed = false;//tat thiet bi máy lạnh
                    sw_Air_bed.setChecked(false);
                    picAir_bed.setImageResource(R.drawable.airconditionoff_bed);
                    Toast.makeText(getActivity(),"Turn off Air Conditioner",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Đối với ĐÈN
        light_bed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value =snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateLight_bed = true;
                    picLight_bed.setImageResource(R.drawable.lampbulbon_bed);
                    sw_Light_bed.setChecked(true);
                    Toast.makeText(getActivity(),"Turn on Light",Toast.LENGTH_SHORT).show();
                }else if(value.equals("Close")){
                    stateLight_bed = false;
                    sw_Light_bed.setChecked(false);
                    picLight_bed.setImageResource(R.drawable.lampbulboff_bed);
                    Toast.makeText(getActivity(),"Turn off Light",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Đối với QUẠT
        fan_bed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value =snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateFan_bed = true;
                    rotateAnimation();
                    sw_Fan_bed.setChecked(true);
                    Fan_controll.setSelected(true);
                    Toast.makeText(getActivity(),"Turn on the Fan",Toast.LENGTH_SHORT).show();
                }else if(value.equals("Close")){
                    stateFan_bed = false;
                    sw_Fan_bed.setChecked(false);
                    picFan_bed.clearAnimation();

                    Fan_controll.setSelected(false);

                    Toast.makeText(getActivity(),"Turn off the Fan", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sw_Air_bed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    air_conditioner_bed.setValue("Open");
                }else{
                    air_conditioner_bed.setValue("Close");
                }
            }
        });
        sw_Light_bed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    light_bed.setValue("Open");
                }else{
                    light_bed.setValue("Close");
                }
            }
        });
        sw_Fan_bed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fan_bed.setValue("Open");
                }else{
                    fan_bed.setValue("Close");
                }
            }
        });


        picAir_bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateAir_bed =!stateAir_bed;
                if(stateAir_bed){
                    air_conditioner_bed.setValue("Open");
                    picAir_bed.setImageResource(R.drawable.aircondition_bed);
                    sw_Air_bed.setChecked(true);
                }else{
                    air_conditioner_bed.setValue("Close");
                    picAir_bed.setImageResource(R.drawable.airconditionoff_bed);
                    sw_Air_bed.setChecked(false);
                }
            }
        });
        picLight_bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLight_bed =!stateLight_bed;
                if(stateLight_bed){
                    light_bed.setValue("Open");
                    picLight_bed.setImageResource(R.drawable.lampbulbon_bed);
                    sw_Light_bed.setChecked(true);
                }else{
                    light_bed.setValue("Close");
                    picLight_bed.setImageResource(R.drawable.lampbulboff_bed);
                    sw_Light_bed.setChecked(false);
                }
            }
        });
        picFan_bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateFan_bed =!stateFan_bed;
                if(stateFan_bed){
                    fan_bed.setValue("Open");
                    rotateAnimation();
                    sw_Fan_bed.setChecked(true);
                }else{
                    fan_bed.setValue("Close");
                    picFan_bed.clearAnimation();
                    sw_Fan_bed.setChecked(false);
                }
            }
        });
    }

    private void anhxa_kit() {
        picAir_bed = getActivity().findViewById(R.id.pic_air_off_bed);
        picLight_bed = getActivity().findViewById(R.id.pic_light_off_bed);
        picFan_bed = getActivity().findViewById(R.id.pic_fan_on_bed);

        sw_Air_bed = getActivity().findViewById(R.id.swAir_bed);
        sw_Fan_bed = getActivity().findViewById(R.id.swFan_bed);
        sw_Light_bed = getActivity().findViewById(R.id.swLight_bed);

        Temp_bed = getActivity().findViewById(R.id.temp_bed);
        Humid_bed = getActivity().findViewById(R.id.humid_bed);

        Fan_speed = getActivity().findViewById(R.id.fan_speed);
        Fan_controll = getActivity().findViewById(R.id.fan_controll);

    }
    private void rotateAnimation() {
        rotateAnimation_bed = AnimationUtils.loadAnimation(getActivity(),R.anim.rotationfan_bed);
        picFan_bed.startAnimation(rotateAnimation_bed);
    }
}
