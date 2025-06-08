package com.example.myapplication.Control_Interfaces;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public   class Security extends Fragment {
    private ImageView picDoor, picWarning,picDoorWarning;
    private Switch sw_Door;
    TextView txt_warning, txt_door_warning;
    private boolean stateDoor;
    ImageView changeDoorPass, forgotDoorPass;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_security, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // đọc id cho các đối tượng từ xml
        anhxa();
        //thay doi database realtime

        final DatabaseReference warning = database.getReference("Security/Warning");
        final DatabaseReference door_status = database.getReference("Security/Door_status");
        final DatabaseReference door_warning = database.getReference("Security/Door");
        final DatabaseReference door_solve = database.getReference("Security/Door_solve");


        // Xử lý an ninh của hệ thống nhà thông minh

        warning.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("Normal")) {
                    txt_warning.setText("     NORMAL");
                    picWarning.setImageResource(R.drawable.pic_normal);
                } else if (value.equals("There is fire")) {
                    txt_warning.setText(" THERE IS FIRE");
                    picWarning.setImageResource(R.drawable.pic_fire);
                } else if (value.equals("Gas leakage")) {
                    txt_warning.setText("  GAS LEAKAGE");
                    picWarning.setImageResource(R.drawable.pic_gas);
                } else if (value.equals("High temperature")) {
                    txt_warning.setText("HIGH TEMPERATURE");
                    picWarning.setImageResource(R.drawable.pic_temperature);
                } else if (value.equals("Suspicious object")) {
                    txt_warning.setText("SUSPICIOUS OBJECT");
                    picWarning.setImageResource(R.drawable.pic_suspicious);
                } else if (value.equals("Rain")) {
                    txt_warning.setText("  IT'S RAINING");
                    picWarning.setImageResource(R.drawable.pic_rain);
                }
                if (!value.equals("Normal")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
                    builder.setTitle("⚠ SECURITY WARNING");
                    builder.setMessage("WARNING: " + value.toUpperCase());
                    builder.setPositiveButton("I UNDERSTAND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // xử lý Normal khi đã nắm được tình hình liệu có rò khí gas, có lửa hay có vật thể khả nghi không

        picWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
                builder.setTitle("⚠ ALERT");
                builder.setMessage("YOU UNDERSTOOD THE WARNING CONDITION ? ");
                builder.setPositiveButton("I GOT IT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                        warning.setValue("Normal");
                    }
                });
                builder.show();
            }
        });

        // Xử lý an ninh khu vực DOOR của hệ thống nhà thông minh

        door_warning.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("Normal")) {
                    txt_door_warning.setText("       NORMAL");
                    picDoorWarning.setImageResource(R.drawable.pic_door_normal);
                } else if (value.equals("Guest")) {
                    txt_door_warning.setText("THERE IS GUEST");
                    door_solve.setValue("Abnormal");
                    picDoorWarning.setImageResource(R.drawable.pic_door_guest);
                } else if (value.equals("Theft")) {
                    txt_door_warning.setText("THERE IS THEFT");
                    door_solve.setValue("Abnormal");
                    picDoorWarning.setImageResource(R.drawable.pic_door_theft);
                }

                if (!value.equals("Normal")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
                    builder.setTitle("⚠ DOOR PROVISION WARNING");
                    builder.setMessage("WARNING: " + value.toUpperCase());
                    builder.setPositiveButton("I UNDERSTAND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                            //door_solve.setValue("Normal");
                            //door_warning.setValue("Normal");
                        }
                    });
                    builder.show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // xử lý Normal khi đã nắm được tình hình an ninh của cửa ra vào

        picDoorWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
                builder.setTitle("⚠ ALERT");
                builder.setMessage("YOU UNDERSTOOD THE DOOR CONDITION ? ");
                builder.setPositiveButton("I GOT IT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                        door_solve.setValue("Normal");
                        door_warning.setValue("Normal");
                    }
                });
                builder.show();
            }
        });


        // Xử lý điều khiển cửa ra vào
        door_status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value.equals("Open")){
                    stateDoor = true;
                    picDoor.setImageResource(R.drawable.pic_door_open);
                    sw_Door.setChecked(true);
                    Toast.makeText(getActivity(),"Open the door !",Toast.LENGTH_SHORT).show();
                } else if(value.equals("Close")){
                    stateDoor = false;//tat thiet bi máy lạnh
                    sw_Door.setChecked(false);
                    picDoor.setImageResource(R.drawable.pic_door_close);
                    Toast.makeText(getActivity(),"Close the door !",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sw_Door.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (!stateDoor) {
                        Dialog dialog = new Dialog(getActivity());
                        dialog.setTitle("Open Door");
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.open_door);
                        EditText fillOpenDoor = (EditText) dialog.findViewById(R.id.fill_open_door);
                        Button btnOpenOk = (Button) dialog.findViewById(R.id.btn_open_door_ok);
                        Button btnOpenCancel = (Button) dialog.findViewById(R.id.btn_open_door_cancel);

                        btnOpenOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String fillOpenDoorPassword = fillOpenDoor.getText().toString().trim();
                                final DatabaseReference doorPassword = database.getReference("Security/Door_password");

                                if (fillOpenDoorPassword.isEmpty()) {
                                    fillOpenDoor.setError("Password must be filled in");
                                    fillOpenDoor.requestFocus();
                                    return;
                                }
                                // Đọc dữ liệu mật khẩu của của Door về
                                doorPassword.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String value = snapshot.getValue(String.class);
                                        if (fillOpenDoorPassword.equals(value)) {
                                            stateDoor = !stateDoor;
                                            door_status.setValue("Open");
                                            picDoor.setImageResource(R.drawable.pic_door_open);
                                            sw_Door.setChecked(true);
                                            dialog.cancel();

                                        } else {
                                            fillOpenDoor.setError("Something went wrong or you entered wrong password of the door !!!");
                                            fillOpenDoor.requestFocus();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        btnOpenCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sw_Door.setChecked(false);
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    }
                }else{
                    door_status.setValue("Close");
                }
            }
        });

        picDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stateDoor) {
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setTitle("Open Door");
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.open_door);
                    EditText fillOpenDoor = (EditText) dialog.findViewById(R.id.fill_open_door);
                    Button btnOpenOk = (Button) dialog.findViewById(R.id.btn_open_door_ok);
                    Button btnOpenCancel = (Button) dialog.findViewById(R.id.btn_open_door_cancel);

                    btnOpenOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String fillOpenDoorPassword = fillOpenDoor.getText().toString().trim();
                            final DatabaseReference doorPassword = database.getReference("Security/Door_password");

                            if (fillOpenDoorPassword.isEmpty()) {
                                fillOpenDoor.setError("Password must be filled in");
                                fillOpenDoor.requestFocus();
                                return;
                            }
                            // Đọc dữ liệu mật khẩu của của Door về
                            doorPassword.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String value = snapshot.getValue(String.class);
                                    if (fillOpenDoorPassword.equals(value)) {
                                        stateDoor = !stateDoor;
                                        door_status.setValue("Open");
                                        picDoor.setImageResource(R.drawable.pic_door_open);
                                        sw_Door.setChecked(true);
                                        dialog.cancel();

                                    } else {
                                        fillOpenDoor.setError("Something went wrong or you entered wrong password of the door !!!");
                                        fillOpenDoor.requestFocus();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    btnOpenCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                } else {
                    stateDoor = !stateDoor;
                    door_status.setValue("Close");
                    picDoor.setImageResource(R.drawable.pic_door_close);
                    sw_Door.setChecked(false);
                }
            }
        });


        // Change door password
        changeDoorPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Change Door Password");
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.change_door_password);
                EditText fillOldDoorPass = (EditText) dialog.findViewById(R.id.fill_old_door_password);
                Button btnChangeOk = (Button) dialog.findViewById(R.id.btn_change_ok);
                Button btnChangeCancel = (Button) dialog.findViewById(R.id.btn_change_cancel);

                btnChangeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String oldDoorPass = fillOldDoorPass.getText().toString().trim();
                        final DatabaseReference door_password = database.getReference("Security/Door_password");

                        if(oldDoorPass.isEmpty()){
                            fillOldDoorPass.setError("Password must be filled in");
                            fillOldDoorPass.requestFocus();
                            return;
                        }
                        // Đọc dữ liệu mật khẩu của của Door về
                        door_password.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String value = snapshot.getValue(String.class);
                                if (oldDoorPass.equals(value)) {
                                    // mở ra bẳng đê nhập mật khẩu mới
                                    Dialog dialog_1 = new Dialog(getActivity());
                                    dialog_1.setTitle("ENTER NEW DOOR PASSWORD");
                                    dialog_1.setCancelable(false);
                                    dialog_1.setContentView(R.layout.process_change_door_password);
                                    EditText fillDoorPass = (EditText) dialog_1.findViewById(R.id.fill_door_password);
                                    EditText fillDoorReset = (EditText) dialog_1.findViewById(R.id.fill_door_reset);
                                    Button btnCancelResetConf = (Button) dialog_1.findViewById(R.id.btn_cancel_reset_conf);
                                    Button btnOkResetConf = (Button) dialog_1.findViewById(R.id.btn_ok_reset_conf);

                                    btnOkResetConf.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //thiết lập mật khẩu của mới
                                            String pass_word_door = fillDoorPass.getText().toString().trim();
                                            String retype_pass_door = fillDoorReset.getText().toString().trim();

                                            if (pass_word_door.isEmpty()) {
                                                fillDoorPass.setError("Password must be filled in");
                                                fillDoorPass.requestFocus();
                                                return;
                                            }
                                            if (pass_word_door.length() < 6) {
                                                fillDoorPass.setError("Password must have at least 6 characters");
                                                fillDoorPass.requestFocus();
                                                return;
                                            }

                                            if (retype_pass_door.isEmpty()) {
                                                fillDoorReset.setError("Confirm Password must be filled in");
                                                fillDoorReset.requestFocus();
                                                return;
                                            }
                                            if (retype_pass_door.length() < 6) {
                                                fillDoorReset.setError("Confirm Password must have at least 6 characters");
                                                fillDoorReset.requestFocus();
                                                return;
                                            }

                                            if (retype_pass_door.equals(oldDoorPass)) {
                                                fillDoorReset.setError("Your new door password is similar with old door password!");
                                                fillDoorReset.requestFocus();
                                                return;
                                            }

                                            if (!pass_word_door.equals(retype_pass_door)) {
                                                fillDoorReset.setError("Checkout !!!, your password and confirm password don't match !!!");
                                                fillDoorReset.requestFocus();
                                                return;

                                            } else {
                                                door_password.setValue(retype_pass_door);
                                                dialog.cancel();
                                                Toast.makeText(getActivity(), "You changed your door password successfully !!!", Toast.LENGTH_SHORT).show();
                                            }
                                            dialog_1.cancel();
                                        }
                                    });
                                    btnCancelResetConf.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog_1.cancel();
                                        }
                                    });
                                    dialog_1.show();

                                } else {
                                    fillOldDoorPass.setError("Something went wrong or you entered wrong old password of the door !!!");
                                    fillOldDoorPass.requestFocus();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                btnChangeCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        // xử lý quên mật khẩu của ra Door
        forgotDoorPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("FORGOT DOOR PASSWORD");
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.forgot_door_password);
                EditText confAccUser = (EditText) dialog.findViewById(R.id.conf_acc_user);
                EditText confAccPass = (EditText) dialog.findViewById(R.id.conf_acc_pass);
                Button btnConfCancel = (Button) dialog.findViewById(R.id.btn_conf_cancel);
                Button btnConfOk = (Button) dialog.findViewById(R.id.btn_conf_ok);

                btnConfOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pass_forgot = confAccPass.getText().toString().trim();
                        String user_forgot = confAccUser.getText().toString().trim();

                        final DatabaseReference User = database.getReference("Login/User");
                        final DatabaseReference Password = database.getReference("Login/Password");

                        // kiểm trả xem tên username có trùng khớp hay không
                        User.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String value = snapshot.getValue(String.class);

                                if (user_forgot.isEmpty()){
                                    confAccUser.setError("Email must be filled in");
                                    confAccUser.requestFocus();
                                    return;
                                }

                                if (! Patterns.EMAIL_ADDRESS.matcher(user_forgot).matches()){
                                    confAccUser.setError("Please enter a valid email !!!");;
                                    confAccUser.requestFocus();
                                    return;
                                }

                                if (!value.equals(user_forgot)) {
                                    confAccUser.setError("Your email doesn't match, please try again !!!");
                                    confAccUser.requestFocus();
                                    return;
                                } else {
                                    Password.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String value = snapshot.getValue(String.class);

                                            if(pass_forgot.isEmpty()){
                                                confAccPass.setError("Password must be filled in");
                                                confAccPass.requestFocus();
                                                return;
                                            }
                                            if(pass_forgot.length() < 6){
                                                confAccPass.setError("Password must have at least 6 characters");
                                                confAccPass.requestFocus();
                                                return;
                                            }

                                            if (!value.equals(pass_forgot)){
                                                confAccPass.setError("Your password doesn't match, please try again !!!");
                                                confAccPass.requestFocus();
                                                return;

                                            } else {
                                                final DatabaseReference door_password = database.getReference("Security/Door_password");

                                                Dialog dialog_1 = new Dialog(getActivity());
                                                dialog_1.setTitle("ENTER NEW DOOR PASSWORD");
                                                dialog_1.setCancelable(false);
                                                dialog_1.setContentView(R.layout.process_change_door_password);
                                                EditText fillDoorPass = (EditText) dialog_1.findViewById(R.id.fill_door_password);
                                                EditText fillDoorReset = (EditText) dialog_1.findViewById(R.id.fill_door_reset);
                                                Button btnCancelResetConf = (Button) dialog_1.findViewById(R.id.btn_cancel_reset_conf);
                                                Button btnOkResetConf = (Button) dialog_1.findViewById(R.id.btn_ok_reset_conf);

                                                btnOkResetConf.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        //thiết lập mật khẩu của mới
                                                        String pass_word_door = fillDoorPass.getText().toString().trim();
                                                        String retype_pass_door = fillDoorReset.getText().toString().trim();

                                                        if (pass_word_door.isEmpty()) {
                                                            fillDoorPass.setError("Password must be filled in");
                                                            fillDoorPass.requestFocus();
                                                            return;
                                                        }
                                                        if (pass_word_door.length() < 6) {
                                                            fillDoorPass.setError("Password must have at least 6 characters");
                                                            fillDoorPass.requestFocus();
                                                            return;
                                                        }

                                                        if (retype_pass_door.isEmpty()) {
                                                            fillDoorReset.setError("Confirm Password must be filled in");
                                                            fillDoorReset.requestFocus();
                                                            return;
                                                        }
                                                        if (retype_pass_door.length() < 6) {
                                                            fillDoorReset.setError("Confirm Password must have at least 6 characters");
                                                            fillDoorReset.requestFocus();
                                                            return;
                                                        }

                                                        if (!pass_word_door.equals(retype_pass_door)) {
                                                            fillDoorReset.setError("Checkout !!!, your password and confirm password don't match !!!");
                                                            fillDoorReset.requestFocus();
                                                            return;

                                                        } else {
                                                            door_password.setValue(retype_pass_door);
                                                            dialog.cancel();
                                                            Toast.makeText(getActivity(), "You changed your door password successfully !!!", Toast.LENGTH_SHORT).show();
                                                        }
                                                        dialog_1.cancel();
                                                    }
                                                });
                                                btnCancelResetConf.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog_1.cancel();
                                                    }
                                                });
                                                dialog_1.show();

                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // dialog.cancel();

                    }
                });
                btnConfCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();

            }
        });

    }

    private void anhxa() {
        picDoor = getActivity().findViewById(R.id.pic_door);
        picWarning = getActivity().findViewById(R.id.pic_security_warn);
        picDoorWarning = getActivity().findViewById(R.id.pic_security_door);

        sw_Door = getActivity().findViewById(R.id.swDoor);

        txt_warning = getActivity().findViewById(R.id.security_warning);
        txt_door_warning = getActivity().findViewById(R.id.security_door);

        changeDoorPass = getActivity().findViewById(R.id.change_door_pass);
        forgotDoorPass = getActivity().findViewById(R.id.forgot_door_pass);

    }
}
