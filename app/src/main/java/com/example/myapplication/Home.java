package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Control_Interfaces.Bedroom;
import com.example.myapplication.Control_Interfaces.Chat_Home;
import com.example.myapplication.Control_Interfaces.Favorites;
import com.example.myapplication.Control_Interfaces.History;
import com.example.myapplication.Control_Interfaces.Kitchen;
import com.example.myapplication.Control_Interfaces.Livingroom;
import com.example.myapplication.Control_Interfaces.Password;
import com.example.myapplication.Control_Interfaces.Profile;
import com.example.myapplication.Control_Interfaces.Scan_QRcode;
import com.example.myapplication.Control_Interfaces.Security;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final int FRAGMENT_HOME = 0 ;
    private static final int FRAGMENT_SECURITY = 1 ;
    private static final int FRAGMENT_LIVING = 2 ;
    private static final int FRAGMENT_BEDROOM = 3 ;
    private static final int FRAGMENT_KITCHEN = 4 ;
    private static final int FRAGMENT_FAVORITE = 5 ;
    private static final int FRAGMENT_HISTORY = 6;
    private static final int FRAGMENT_PROFILE = 7;

    // FRAGMENT_PASSWORD thực chất là family
    private static final int FRAGMENT_PASSWORD = 8;

    private static final int TOOLBAR_CHATBOX = 0;
    private static final int TOOLBAR_VOICE = 1;
    private static final int TOOLBAR_QR_CODE = 2;
    private static final int TOOLBAR_NOTIFICATION = 3;
    private static final int TOOLBAR_SETTING = 4;
    private static final int TOOLBAR_LOGOUT = 5;
    private static final int TOOLBAR_SEARCH = 6;

    private int mCurrentFragment = FRAGMENT_HOME;

    private int mCurrentToolbar = TOOLBAR_CHATBOX;

    private DrawerLayout mDrawerLayout;

    // khai báo cho điều khiển bằng giọng nói
    int RESULT_SPEECH = 1 ;
    ImageView btn_Speak;
    EditText vtText;
    Button btn_vt_go, btn_vt_cancel;

    // Khái báo biến đọc nhiệt độ khi có yêu cầu từ nhận diện giọng nói
    String tempLiv, humidLiv, tempBed, humidBed;

    // khai báo check trạng thái điều khiển bằng giọng nói
    boolean check_voice = false;

    // biểu kiểm soát tình trạng của nút mic là true hay false

    boolean check_voice_status = true;

    @SuppressWarnings("ALL")


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // getSupportActionBar().hide();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBar actionBar = getSupportActionBar();
        // có thể không cần dùng lệnh này
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        // ic_menu_sort_by_size là ảnh từ thư viện không phải ảnh lấy từ drawable
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_home);    //Icon muốn hiện thị
        actionBar.setDisplayUseLogoEnabled(true);

        replaceFragment(new Chat_Home());
        navigationView.getMenu().findItem(R.id.nav_chat_home).setChecked(true);
    }

    // Tạo các icon trên thanh toolbar có chức năng mở rộng của actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id_toolbar = item.getItemId();

        ActionBar actionBar = getSupportActionBar();
        //Toobar đã như ActionBar

        if (id_toolbar == R.id.chat){
            if (mCurrentFragment != FRAGMENT_HOME || mCurrentToolbar!= TOOLBAR_CHATBOX) {
                Toast.makeText(Home.this, "Enter ChatBox", Toast.LENGTH_SHORT).show();

                replaceFragment(new Chat_Home());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_home);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_HOME;
                mCurrentToolbar = TOOLBAR_CHATBOX;

                // onBackPressed();
            }

        } else if (id_toolbar == R.id.qrcode) {

            Toast.makeText(Home.this, "Enter Scanning QR Code Tool", Toast.LENGTH_SHORT).show();

            replaceFragment(new Scan_QRcode());

            actionBar.setTitle("");

            mCurrentToolbar = TOOLBAR_QR_CODE;

            //  onBackPressed();


        } else if (id_toolbar == R.id.voice) {

            Toast.makeText(Home.this, "NOW, YOU CAN CONTROLL YOUR SMART HOME BY YOUR VOICE !", Toast.LENGTH_SHORT).show();

            actionBar.setTitle("");

            // Điều khiển app bằng giọng nói


            Dialog dialog = new Dialog(Home.this);
            dialog.setTitle("Hộp thoại xử lý");
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.voice_panel);

            vtText = (EditText) dialog.findViewById(R.id.tvText);
            btn_Speak = (ImageView) dialog.findViewById(R.id.btnSpeak);
            btn_vt_go = (Button) dialog.findViewById(R.id.btn_voice_controll_go);
            btn_vt_cancel = (Button) dialog.findViewById(R.id.btn_voice_control_cancel);

            btn_Speak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // set biểu tưởng hủy nổi lên trên biểu tưởng cái mic
                    if (check_voice_status || vtText.getText().length() != 0){
                        btn_Speak.setImageResource(R.drawable.ic_cancel);
                    } else {
                        btn_Speak.setImageResource(R.drawable.ic_mic);
                    }
                    check_voice_status = !check_voice_status;

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
                    try {
                        startActivityForResult(intent, RESULT_SPEECH);
                    } catch (ActivityNotFoundException e){
                        Toast.makeText(getApplicationContext(),"Your device don't support changing languages !!!",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

            btn_vt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            btn_vt_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String vttxt = vtText.getText().toString().trim().toLowerCase();

                    if (vttxt.equals("go to living room") || vttxt.equals("please go to living room") ||
                            vttxt.equals("come to living room") || vttxt.equals("please come to living room") ||
                            vttxt.equals("switch to living room") || vttxt.equals("please switch to living room") ||
                            vttxt.equals("open living room") || vttxt.equals("please open living room")) {
                        if (mCurrentFragment != FRAGMENT_LIVING) {
                            replaceFragment(new Livingroom());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_livingroom);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_LIVING;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to security system") || vttxt.equals("please go to security system") ||
                            vttxt.equals("come to security system") || vttxt.equals("please come to security system") ||
                            vttxt.equals("switch to security system") || vttxt.equals("please switch to security system") ||
                            vttxt.equals("open security system") || vttxt.equals("please open security system")) {
                        if (mCurrentFragment != FRAGMENT_SECURITY) {
                            replaceFragment(new Security());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_security);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_SECURITY;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to bedroom") || vttxt.equals("please go to bedroom") ||
                            vttxt.equals("come to bedroom") || vttxt.equals("please come to bedroom") ||
                            vttxt.equals("switch to bedroom") || vttxt.equals("please switch to bedroom") ||
                            vttxt.equals("open bedroom") || vttxt.equals("please open bedroom")) {
                        if (mCurrentFragment != FRAGMENT_BEDROOM) {
                            replaceFragment(new Bedroom());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_bedroom);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_BEDROOM;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to kitchen") || vttxt.equals("please go to kitchen") ||
                            vttxt.equals("come to kitchen") || vttxt.equals("please come to kitchen") ||
                            vttxt.equals("switch to kitchen") || vttxt.equals("please switch to kitchen") ||
                            vttxt.equals("open kitchen") || vttxt.equals("please open kitchen")) {
                        if (mCurrentFragment != FRAGMENT_KITCHEN) {
                            replaceFragment(new Kitchen());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_kitchen);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_KITCHEN;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to favorite") || vttxt.equals("please go to favorite") ||
                            vttxt.equals("come to favorite") || vttxt.equals("please come to favorite") ||
                            vttxt.equals("switch to favorite") || vttxt.equals("please switch to favorite") ||
                            vttxt.equals("open favorite") || vttxt.equals("please open favorite") ||
                            vttxt.equals("go to favorites") || vttxt.equals("please go to favorites") ||
                            vttxt.equals("come to favorites") || vttxt.equals("please come to favorites") ||
                            vttxt.equals("switch to favorites") || vttxt.equals("please switch to favorites") ||
                            vttxt.equals("open favorites") || vttxt.equals("please open favorites")) {
                        if (mCurrentFragment != FRAGMENT_FAVORITE) {
                            replaceFragment(new Favorites());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_favorite);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_FAVORITE;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to history") || vttxt.equals("please go to history") ||
                            vttxt.equals("come to history") || vttxt.equals("please come to history") ||
                            vttxt.equals("switch to history") || vttxt.equals("please switch to history") ||
                            vttxt.equals("open history") || vttxt.equals("please open history")) {
                        if (mCurrentFragment != FRAGMENT_HISTORY) {
                            replaceFragment(new History());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_history);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_HISTORY;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to profile") || vttxt.equals("please go to profile") ||
                            vttxt.equals("come to profile") || vttxt.equals("please come to profile") ||
                            vttxt.equals("switch to profile") || vttxt.equals("please switch to profile") ||
                            vttxt.equals("open profile") || vttxt.equals("please open profile") ||
                            vttxt.equals("go to my profile") || vttxt.equals("please go to my profile") ||
                            vttxt.equals("come to my profile") || vttxt.equals("please come to my profile") ||
                            vttxt.equals("switch to my profile") || vttxt.equals("please switch to my profile") ||
                            vttxt.equals("open my profile") || vttxt.equals("please open my profile")) {
                        if (mCurrentFragment != FRAGMENT_PROFILE) {
                            replaceFragment(new Profile());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_profile);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_PROFILE;
                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to family review") || vttxt.equals("please go to family review") ||
                            vttxt.equals("come to family review") || vttxt.equals("please come to family review") ||
                            vttxt.equals("switch to family review") || vttxt.equals("please switch to family review") ||
                            vttxt.equals("open family review") || vttxt.equals("please open family review") ||
                            vttxt.equals("go to my family review") || vttxt.equals("please go to my family review") ||
                            vttxt.equals("come to my family review") || vttxt.equals("please come to my family review") ||
                            vttxt.equals("switch to my family review") || vttxt.equals("please switch to my family review") ||
                            vttxt.equals("open my family review") || vttxt.equals("please open my family review")) {
                        if (mCurrentFragment != FRAGMENT_PASSWORD) {
                            replaceFragment(new Password());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_family);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_PASSWORD;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to home") || vttxt.equals("please go to home") ||
                            vttxt.equals("come to home") || vttxt.equals("please come to home") ||
                            vttxt.equals("switch to home") || vttxt.equals("please switch to home") ||
                            vttxt.equals("open home") || vttxt.equals("please open home") ||
                            vttxt.equals("go to chatbot") || vttxt.equals("please go to chatbot") ||
                            vttxt.equals("come to chatbot") || vttxt.equals("please come to chatbot") ||
                            vttxt.equals("switch to chatbot") || vttxt.equals("please switch to chatbot") ||
                            vttxt.equals("open chatbot") || vttxt.equals("please open chatbot") ||
                            vttxt.equals("go to chat bot") || vttxt.equals("please go to chat bot") ||
                            vttxt.equals("come to chat bot") || vttxt.equals("please come to chat bot") ||
                            vttxt.equals("switch to chat bot") || vttxt.equals("please switch to chat bot") ||
                            vttxt.equals("open chat bot") || vttxt.equals("please open chat bot")) {

                        if (mCurrentFragment != FRAGMENT_HOME || mCurrentToolbar != TOOLBAR_CHATBOX) {
                            replaceFragment(new Chat_Home());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_home);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_HOME;
                            mCurrentToolbar = TOOLBAR_CHATBOX;

                            check_voice = true;


                            // onBackPressed();
                        }

                    }

                    if (vttxt.equals("go to setting") || vttxt.equals("please go to setting") ||
                            vttxt.equals("come to setting") || vttxt.equals("please come to setting") ||
                            vttxt.equals("switch to setting") || vttxt.equals("please switch to setting") ||
                            vttxt.equals("open setting") || vttxt.equals("please open setting") ||
                            vttxt.equals("go to settings") || vttxt.equals("please go to settings") ||
                            vttxt.equals("come to settings") || vttxt.equals("please come to settings") ||
                            vttxt.equals("switch to settings") || vttxt.equals("please switch to settings") ||
                            vttxt.equals("open settings") || vttxt.equals("please open settings")) {
                        Toast.makeText(Home.this, "Enter Setting Panel", Toast.LENGTH_SHORT).show();

                        // replaceFragment(new Scan_QRcode());
                        Intent intent = new Intent(Home.this, Setting.class);
                        startActivity(intent);

                        check_voice = true;

                        actionBar.setTitle("");
                        mCurrentToolbar = TOOLBAR_SETTING;

                    }

                    if (vttxt.equals("logout") || vttxt.equals("log out") ||
                            vttxt.equals("i want to logout") || vttxt.equals("i want to log out") ||
                            vttxt.equals("please logout") || vttxt.equals("please log out") ||
                            vttxt.equals("exit") || vttxt.equals("please exit"))  {

                        Toast.makeText(Home.this, "You logged out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        mCurrentToolbar = TOOLBAR_LOGOUT;

                        check_voice = true;

                        Intent intent = new Intent(Home.this, Login.class);
                        startActivity(intent);
                        finish();
                    }

                    // cảnh báo nếu từ giọng nói điều khiển bị sai

                    check_voice = !check_voice;

                    if (check_voice) {

                        vtText.setError("There is an error or your request is so confusing. Please try again !!!");
                        vtText.requestFocus();
                        check_voice = !check_voice;
                        vtText.setText("");
                    }
                }
            });
            dialog.show();
            ///////////////////////////////////////


            mCurrentToolbar = TOOLBAR_VOICE;

        } else if (id_toolbar == R.id.setting){
            Toast.makeText(Home.this, "Enter Setting Panel", Toast.LENGTH_SHORT).show();

            // replaceFragment(new Scan_QRcode());
            Intent intent = new Intent(Home.this, Setting.class);
            startActivity(intent);

            actionBar.setTitle("");
            mCurrentToolbar = TOOLBAR_SETTING;

        } else if (id_toolbar == R.id.logout) {

            Toast.makeText(Home.this, "You logged out", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            mCurrentToolbar = TOOLBAR_LOGOUT;

            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish();
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        ActionBar actionBar = getSupportActionBar();
        //Toobar đã như ActionBar

        if (id == R.id.nav_chat_home) {
            if (mCurrentFragment != FRAGMENT_HOME || mCurrentToolbar!= TOOLBAR_CHATBOX) {
                replaceFragment(new Chat_Home());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_home);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_HOME;
                mCurrentToolbar = TOOLBAR_CHATBOX;

                onBackPressed();
            }
        } else if ( id == R.id.nav_security){
            if (mCurrentFragment != FRAGMENT_SECURITY){
                replaceFragment(new Security());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_security);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_SECURITY;

                onBackPressed();
            }
        } else if ( id == R.id.nav_livingroom){
            if (mCurrentFragment != FRAGMENT_LIVING){
                replaceFragment(new Livingroom());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_livingroom);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_LIVING;

                onBackPressed();
            }
        } else if ( id == R.id.nav_bedroom){
            if (mCurrentFragment != FRAGMENT_BEDROOM){
                replaceFragment(new Bedroom());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_bedroom);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_BEDROOM;

                onBackPressed();
            }
        } else if ( id == R.id.nav_kitchen){
            if (mCurrentFragment != FRAGMENT_KITCHEN){
                replaceFragment(new Kitchen());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_kitchen);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_KITCHEN;

                onBackPressed();
            }
        } else if ( id == R.id.nav_favorite){
            if (mCurrentFragment != FRAGMENT_FAVORITE){
                replaceFragment(new Favorites());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_favorite);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_FAVORITE;

                onBackPressed();
            }
        } else if ( id == R.id.nav_history){
            if (mCurrentFragment != FRAGMENT_HISTORY){
                replaceFragment(new History());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_history);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_HISTORY;

                onBackPressed();
            }
        }  else if ( id == R.id.nav_profile){
            if (mCurrentFragment != FRAGMENT_PROFILE){
                replaceFragment(new Profile());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_profile);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_PROFILE;

                onBackPressed();
            }
        }  else if ( id == R.id.nav_password){
            if (mCurrentFragment != FRAGMENT_PASSWORD){
                replaceFragment(new Password());

                actionBar.setTitle("");
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.ic_family);    //Icon muốn hiện thị
                actionBar.setDisplayUseLogoEnabled(true);

                mCurrentFragment = FRAGMENT_PASSWORD;

                onBackPressed();
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout,fragment);

        transaction.addToBackStack(fragment.getClass().getSimpleName());

        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ActionBar actionBar = getSupportActionBar();

        //Toobar đã như ActionBar
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK || data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    vtText.setText(text.get(0));

                    String vttxt = vtText.getText().toString().trim().toLowerCase();

                    if (vttxt.equals("go to living room") || vttxt.equals("please go to living room") ||
                            vttxt.equals("come to living room") || vttxt.equals("please come to living room") ||
                            vttxt.equals("switch to living room") || vttxt.equals("please switch to living room") ||
                            vttxt.equals("open living room") || vttxt.equals("please open living room")) {
                        if (mCurrentFragment != FRAGMENT_LIVING) {
                            replaceFragment(new Livingroom());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_livingroom);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_LIVING;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to security system") || vttxt.equals("please go to security system") ||
                            vttxt.equals("come to security system") || vttxt.equals("please come to security system") ||
                            vttxt.equals("switch to security system") || vttxt.equals("please switch to security system") ||
                            vttxt.equals("open security system") || vttxt.equals("please open security system")) {
                        if (mCurrentFragment != FRAGMENT_SECURITY) {
                            replaceFragment(new Security());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_security);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_SECURITY;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to bedroom") || vttxt.equals("please go to bedroom") ||
                            vttxt.equals("come to bedroom") || vttxt.equals("please come to bedroom") ||
                            vttxt.equals("switch to bedroom") || vttxt.equals("please switch to bedroom") ||
                            vttxt.equals("open bedroom") || vttxt.equals("please open bedroom")) {
                        if (mCurrentFragment != FRAGMENT_BEDROOM) {
                            replaceFragment(new Bedroom());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_bedroom);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_BEDROOM;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to kitchen") || vttxt.equals("please go to kitchen") ||
                            vttxt.equals("come to kitchen") || vttxt.equals("please come to kitchen") ||
                            vttxt.equals("switch to kitchen") || vttxt.equals("please switch to kitchen") ||
                            vttxt.equals("open kitchen") || vttxt.equals("please open kitchen")) {
                        if (mCurrentFragment != FRAGMENT_KITCHEN) {
                            replaceFragment(new Kitchen());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_kitchen);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_KITCHEN;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to favorite") || vttxt.equals("please go to favorite") ||
                            vttxt.equals("come to favorite") || vttxt.equals("please come to favorite") ||
                            vttxt.equals("switch to favorite") || vttxt.equals("please switch to favorite") ||
                            vttxt.equals("open favorite") || vttxt.equals("please open favorite") ||
                            vttxt.equals("go to favorites") || vttxt.equals("please go to favorites") ||
                            vttxt.equals("come to favorites") || vttxt.equals("please come to favorites") ||
                            vttxt.equals("switch to favorites") || vttxt.equals("please switch to favorites") ||
                            vttxt.equals("open favorites") || vttxt.equals("please open favorites")) {
                        if (mCurrentFragment != FRAGMENT_FAVORITE) {
                            replaceFragment(new Favorites());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_favorite);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_FAVORITE;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to history") || vttxt.equals("please go to history") ||
                            vttxt.equals("come to history") || vttxt.equals("please come to history") ||
                            vttxt.equals("switch to history") || vttxt.equals("please switch to history") ||
                            vttxt.equals("open history") || vttxt.equals("please open history")) {
                        if (mCurrentFragment != FRAGMENT_HISTORY) {
                            replaceFragment(new History());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_history);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_HISTORY;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to profile") || vttxt.equals("please go to profile") ||
                            vttxt.equals("come to profile") || vttxt.equals("please come to profile") ||
                            vttxt.equals("switch to profile") || vttxt.equals("please switch to profile") ||
                            vttxt.equals("open profile") || vttxt.equals("please open profile") ||
                            vttxt.equals("go to my profile") || vttxt.equals("please go to my profile") ||
                            vttxt.equals("come to my profile") || vttxt.equals("please come to my profile") ||
                            vttxt.equals("switch to my profile") || vttxt.equals("please switch to my profile") ||
                            vttxt.equals("open my profile") || vttxt.equals("please open my profile")) {
                        if (mCurrentFragment != FRAGMENT_PROFILE) {
                            replaceFragment(new Profile());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_profile);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_PROFILE;
                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to family review") || vttxt.equals("please go to family review") ||
                            vttxt.equals("come to family review") || vttxt.equals("please come to family review") ||
                            vttxt.equals("switch to family review") || vttxt.equals("please switch to family review") ||
                            vttxt.equals("open family review") || vttxt.equals("please open family review") ||
                            vttxt.equals("go to my family review") || vttxt.equals("please go to my family review") ||
                            vttxt.equals("come to my family review") || vttxt.equals("please come to my family review") ||
                            vttxt.equals("switch to my family review") || vttxt.equals("please switch to my family review") ||
                            vttxt.equals("open my family review") || vttxt.equals("please open my family review")) {
                        if (mCurrentFragment != FRAGMENT_PASSWORD) {
                            replaceFragment(new Password());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_family);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_PASSWORD;

                            check_voice = true;


                            // onBackPressed();
                        }
                    }

                    if (vttxt.equals("go to home") || vttxt.equals("please go to home") ||
                            vttxt.equals("come to home") || vttxt.equals("please come to home") ||
                            vttxt.equals("switch to home") || vttxt.equals("please switch to home") ||
                            vttxt.equals("open home") || vttxt.equals("please open home") ||
                            vttxt.equals("go to chatbot") || vttxt.equals("please go to chatbot") ||
                            vttxt.equals("come to chatbot") || vttxt.equals("please come to chatbot") ||
                            vttxt.equals("switch to chatbot") || vttxt.equals("please switch to chatbot") ||
                            vttxt.equals("open chatbot") || vttxt.equals("please open chatbot") ||
                            vttxt.equals("go to chat bot") || vttxt.equals("please go to chat bot") ||
                            vttxt.equals("come to chat bot") || vttxt.equals("please come to chat bot") ||
                            vttxt.equals("switch to chat bot") || vttxt.equals("please switch to chat bot") ||
                            vttxt.equals("open chat bot") || vttxt.equals("please open chat bot")) {

                        if (mCurrentFragment != FRAGMENT_HOME || mCurrentToolbar != TOOLBAR_CHATBOX) {
                            replaceFragment(new Chat_Home());

                            actionBar.setTitle("");
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setLogo(R.drawable.ic_home);    //Icon muốn hiện thị
                            actionBar.setDisplayUseLogoEnabled(true);

                            mCurrentFragment = FRAGMENT_HOME;
                            mCurrentToolbar = TOOLBAR_CHATBOX;

                            check_voice = true;


                            // onBackPressed();
                        }

                    }

                    if (vttxt.equals("go to setting") || vttxt.equals("please go to setting") ||
                            vttxt.equals("come to setting") || vttxt.equals("please come to setting") ||
                            vttxt.equals("switch to setting") || vttxt.equals("please switch to setting") ||
                            vttxt.equals("open setting") || vttxt.equals("please open setting") ||
                            vttxt.equals("go to settings") || vttxt.equals("please go to settings") ||
                            vttxt.equals("come to settings") || vttxt.equals("please come to settings") ||
                            vttxt.equals("switch to settings") || vttxt.equals("please switch to settings") ||
                            vttxt.equals("open settings") || vttxt.equals("please open settings")) {
                        Toast.makeText(Home.this, "Enter Setting Panel", Toast.LENGTH_SHORT).show();

                        // replaceFragment(new Scan_QRcode());
                        Intent intent = new Intent(Home.this, Setting.class);
                        startActivity(intent);

                        check_voice = true;

                        actionBar.setTitle("");
                        mCurrentToolbar = TOOLBAR_SETTING;

                    }

                    if (vttxt.equals("logout") || vttxt.equals("log out") ||
                            vttxt.equals("i want to logout") || vttxt.equals("i want to log out") ||
                            vttxt.equals("please logout") || vttxt.equals("please log out") ||
                            vttxt.equals("exit") || vttxt.equals("please exit"))  {

                        Toast.makeText(Home.this, "You logged out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        mCurrentToolbar = TOOLBAR_LOGOUT;

                        check_voice = true;

                        Intent intent = new Intent(Home.this, Login.class);
                        startActivity(intent);
                        finish();
                    }

                    // phần điều khiển các thiết bị bằng giọng nói

                    // phòng KHÁCH

                    // khai báo firebase
                    final DatabaseReference light_liv = database.getReference("Livingroom/Light");


                    if (vttxt.equals("turn on living room light") || vttxt.equals("please turn on living room light") ||
                            vttxt.equals("i want to turn on living room light") || vttxt.equals("please i want to turn on living room light") ||
                            vttxt.equals("i want to turn on the light of living room") || vttxt.equals("please i want to turn on the light of living room")) {

                        light_liv.setValue("Open");
                        Toast.makeText(Home.this, "Turn on livingroom light", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("turn off living room light") || vttxt.equals("please turn off living room light") ||
                            vttxt.equals("i want to turn off living room light") || vttxt.equals("please i want to turn off living room light") ||
                            vttxt.equals("i want to turn off the light of living room") || vttxt.equals("please i want to turn off the light of living room")) {
                        light_liv.setValue("Close");
                        Toast.makeText(Home.this, "Turn off livingroom light", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }


                    final DatabaseReference fan_liv = database.getReference("Livingroom/Fan");


                    if (vttxt.equals("turn on living room fan") || vttxt.equals("please turn on living room fan") ||
                            vttxt.equals("i want to turn on living room fan") || vttxt.equals("please i want to turn on living room fan") ||
                            vttxt.equals("i want to turn on the fan of living room") || vttxt.equals("please i want to turn on the fan of living room")) {

                        fan_liv.setValue("Open");
                        Toast.makeText(Home.this, "Turn on livingroom fan", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("turn off living room fan") || vttxt.equals("please turn off living room fan") ||
                            vttxt.equals("i want to turn off living room fan") || vttxt.equals("please i want to turn off living room fan") ||
                            vttxt.equals("i want to turn off the fan of living room") || vttxt.equals("please i want to turn off the fan of living room")) {
                        fan_liv.setValue("Close");
                        Toast.makeText(Home.this, "Turn off livingroom fan", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }

                    final DatabaseReference air_liv = database.getReference("Livingroom/Air_conditioner");

                    if (vttxt.equals("turn on living room conditioner") || vttxt.equals("please turn on living room conditioner") ||
                            vttxt.equals("i want to turn on living room conditioner") || vttxt.equals("please i want to turn on living room conditioner") ||
                            vttxt.equals("i want to turn on the conditioner of living room") || vttxt.equals("please i want to turn on the conditioner of living room")) {

                        air_liv.setValue("Open");
                        Toast.makeText(Home.this, "Turn on livingroom conditioner", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("turn off living room conditioner") || vttxt.equals("please turn off living room conditioner") ||
                            vttxt.equals("i want to turn off living room conditioner") || vttxt.equals("please i want to turn off living room conditioner") ||
                            vttxt.equals("i want to turn off the conditioner of living room") || vttxt.equals("please i want to turn off the conditioner of living room")) {
                        air_liv.setValue("Close");
                        Toast.makeText(Home.this, "Turn off livingroom conditioner", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }
                    // yêu cầu cho biết nhiệt độ và độ ẩm phòng khách bằng giọng nói
                    final DatabaseReference temp_liv = database.getReference("Livingroom/Temperature");
                    final DatabaseReference humid_liv = database.getReference("Livingroom/Humidity");

                    if (vttxt.equals("tell me living room temperature")|| vttxt.equals("tell me living room humidity") ||
                            vttxt.equals("please tell me living room temperature") || vttxt.equals("please tell me living room humidity") ||
                            vttxt.equals("i want to know living room temperature") || vttxt.equals("i want to know living room humidity") ||
                            vttxt.equals("i want to know the temperature of the living room") || vttxt.equals("i want to know the humidity of the living room") ||
                            vttxt.equals("tell me living room temperature and humidity")|| vttxt.equals("tell me living room humidity and temperature") ||
                            vttxt.equals("please tell me living room temperature and humidity") || vttxt.equals("please tell me living room humidity and temperature") ||
                            vttxt.equals("i want to know living room temperature and humidity") || vttxt.equals("i want to know living room humidity and temperature") ||
                            vttxt.equals("i want to know the temperature and the humidity of the living room") || vttxt.equals("i want to know the humidity and the temperature of the living room")) {

                        check_voice = true;

                        temp_liv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tempLiv = snapshot.getValue().toString();

                                humid_liv.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot_1) {
                                        humidLiv = snapshot_1.getValue().toString();
                                        new AlertDialog.Builder(Home.this,android.R.style.Theme_Material_Dialog_Alert)
                                                .setTitle("ABOUT THE ATMOSPHERE OF LIVINGROOM !")
                                                .setMessage("The temperature is " + tempLiv + " °C" + " and           The humid is " + humidLiv + " %")
                                                .setIcon(R.drawable.ic_notification)
                                                .setCancelable(false)
                                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).create().show();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    // xử lý bằng giọng nói cho Security system

                    if (vttxt.equals("tell me security status") || vttxt.equals("tell me security condtion") ||
                            vttxt.equals("tell me security warning") || vttxt.equals("tell me warning of security system") ||
                            vttxt.equals("i want to know security status") || vttxt.equals("i want to know security condtion") ||
                            vttxt.equals("i want to know security warning") || vttxt.equals("i want to know warning of security system")) {

                        final DatabaseReference warning = database.getReference("Security/Warning");

                        check_voice = true;

                        warning.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String value = snapshot.getValue().toString();

                                if (value.equals("Normal")) {
                                    value = "NORMAL, EVETHING IS OK !!!";

                                } else if (value.equals("There is fire")) {
                                    value = "DANGEROUS, THERE IS FIRE !!!" ;

                                } else  if (value.equals("Gas Leakage")) {
                                    value = "DANGEROUS, THERE IS GAS LEAKAGE !!!";

                                } else  if (value.equals("High Temperature")) {
                                    value = "DANGEROUS BECAUSE OF HIGH TEMPERATURE !!!";

                                } else if (value.equals("Suspicious object")) {
                                    value = "BE CAREFUL, THERE IS SUSPICIOUS OBJECT !!!";
                                } else if (value.equals("Rain")) {
                                    value = "IT'S RANING NOW !!!";
                                }

                                new AlertDialog.Builder(Home.this,android.R.style.Theme_Material_Dialog_Alert)
                                        .setTitle("SECURITY SYSTEM WARNING!")
                                        .setMessage(value)
                                        .setIcon(R.drawable.ic_dangerous)
                                        .setCancelable(false)
                                        .setPositiveButton("I UNDERSTAND", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (vttxt.equals("tell me gate security status") || vttxt.equals("tell me gate security condition") ||
                            vttxt.equals("tell me gate security warning") || vttxt.equals("tell me warning of gate security system") ||
                            vttxt.equals("i want to know gate security status") || vttxt.equals("i want to know gate security condtion") ||
                            vttxt.equals("i want to know gate security warning") || vttxt.equals("i want to know warning of gate security system")) {

                        final DatabaseReference door_warning = database.getReference("Security/Door");
                        final DatabaseReference door_solve = database.getReference("Security/Door_solve");

                        check_voice = true;

                        door_warning.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String value = snapshot.getValue().toString();

                                if (value.equals("Normal")) {
                                    value = "NORMAL, EVETHING IS OK !!!";

                                } else if (value.equals("Guest")) {
                                    value = "WELCOME, YOU HAVE GUEST !!!" ;

                                } else  if (value.equals("Theft")) {
                                    value = "DANGEROUS, YOUR HOUSE APPEARING THEFT !!!";

                                }

                                new AlertDialog.Builder(Home.this,android.R.style.Theme_Material_Dialog_Alert)
                                        .setTitle("DOOR SECURITY WARNING! ")
                                        .setMessage(value)
                                        .setIcon(R.drawable.ic_dangerous)
                                        .setCancelable(false)
                                        .setPositiveButton("I UNDERSTAND", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                door_warning.setValue("Normal");
                                                door_solve.setValue("Normal");
                                            }
                                        }).create().show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    final DatabaseReference door = database.getReference("Security/Door_status");
                    if (vttxt.equals("open the gate") || vttxt.equals("please open the gate") ||
                            vttxt.equals("i want to open the gate") || vttxt.equals("please i want to open the gate") ||
                            vttxt.equals("i want to open the door") || vttxt.equals("please i want to open the door of my house")) {

                        door.setValue("Open");
                        Toast.makeText(Home.this, "Open the door", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("close the gate") || vttxt.equals("please close the gate") ||
                            vttxt.equals("i want to close the gate") || vttxt.equals("please i want to close the gate") ||
                            vttxt.equals("i want to close the door") || vttxt.equals("please i want to close the door of my house")) {
                        door.setValue("Close");
                        Toast.makeText(Home.this, "Close the door", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }

                    // xử lý điều khiển phòng ngủ bằng giọng nói

                    final DatabaseReference light_bed = database.getReference("Bedroom/Light");


                    if (vttxt.equals("turn on bedroom light") || vttxt.equals("please turn on bedroom light") ||
                            vttxt.equals("i want to turn on bedroom light") || vttxt.equals("please i want to turn on bedroom light") ||
                            vttxt.equals("i want to turn on the light of bedroom") || vttxt.equals("please i want to turn on the light of bedroom")) {

                        light_bed.setValue("Open");
                        Toast.makeText(Home.this, "Turn on bedroom light", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("turn off bedroom light") || vttxt.equals("please turn off bedroom light") ||
                            vttxt.equals("i want to turn off bedroom light") || vttxt.equals("please i want to turn off bedroom light") ||
                            vttxt.equals("i want to turn off the light of bedroom") || vttxt.equals("please i want to turn off the light of bedroom")) {
                        light_bed.setValue("Close");
                        Toast.makeText(Home.this, "Turn off bedroom light", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }


                    final DatabaseReference fan_bed = database.getReference("Bedroom/Fan");


                    if (vttxt.equals("turn on bedroom fan") || vttxt.equals("please turn on bedroom fan") ||
                            vttxt.equals("i want to turn on bedroom fan") || vttxt.equals("please i want to turn on bedroom fan") ||
                            vttxt.equals("i want to turn on the fan of bedroom") || vttxt.equals("please i want to turn on the fan of bedroom")) {

                        fan_bed.setValue("Open");
                        Toast.makeText(Home.this, "Turn on bedroom fan", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("turn off bedroom fan") || vttxt.equals("please turn off bedroom fan") ||
                            vttxt.equals("i want to turn off bedroom fan") || vttxt.equals("please i want to turn off bedroom fan") ||
                            vttxt.equals("i want to turn off the fan of bedroom") || vttxt.equals("please i want to turn off the fan of bedroom")) {
                        fan_bed.setValue("Close");
                        Toast.makeText(Home.this, "Turn off bedroom fan", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }

                    final DatabaseReference air_bed = database.getReference("Bedroom/Air_conditioner");

                    if (vttxt.equals("turn on bedroom conditioner") || vttxt.equals("please turn on bedroom conditioner") ||
                            vttxt.equals("i want to turn on bedroom conditioner") || vttxt.equals("please i want to turn on bedroom conditioner") ||
                            vttxt.equals("i want to turn on the conditioner of bedroom") || vttxt.equals("please i want to turn on the conditioner of bedroom")) {

                        air_bed.setValue("Open");
                        Toast.makeText(Home.this, "Turn on bedroom conditioner", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("turn off bedroom conditioner") || vttxt.equals("please turn off bedroom conditioner") ||
                            vttxt.equals("i want to turn off bedroom conditioner") || vttxt.equals("please i want to turn off bedroom conditioner") ||
                            vttxt.equals("i want to turn off the conditioner of bedroom") || vttxt.equals("please i want to turn off the conditioner of bedroom")) {
                        air_liv.setValue("Close");
                        Toast.makeText(Home.this, "Turn off bedroom conditioner", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }

                    // yêu cầu cho biết nhiệt độ và độ ẩm phòng khách bằng giọng nói
                    final DatabaseReference temp_bed = database.getReference("Bedroom/Temperature");
                    final DatabaseReference humid_bed = database.getReference("Bedroom/Humidity");

                    if (vttxt.equals("tell me bedroom temperature")|| vttxt.equals("tell me bedroom humidity") ||
                            vttxt.equals("please tell me bedroom temperature") || vttxt.equals("please tell me bedroom humidity") ||
                            vttxt.equals("i want to know bedroom temperature") || vttxt.equals("i want to know bedroom humidity") ||
                            vttxt.equals("i want to know the temperature of the bedroom") || vttxt.equals("i want to know the humidity of the bedroom") ||
                            vttxt.equals("tell me bedroom temperature and humidity")|| vttxt.equals("tell me bedroom humidity and temperature") ||
                            vttxt.equals("please tell me bedroom temperature and humidity") || vttxt.equals("please tell me bedroom humidity and temperature") ||
                            vttxt.equals("i want to know bedroom temperature and humidity") || vttxt.equals("i want to know bedroom humidity and temperature") ||
                            vttxt.equals("i want to know the temperature and the humidity of the bedroom") || vttxt.equals("i want to know the humidity and the temperature of the bedroom")) {

                        check_voice = true;

                        temp_bed.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tempBed = snapshot.getValue().toString();

                                humid_bed.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot_1) {
                                        humidBed = snapshot_1.getValue().toString();
                                        new AlertDialog.Builder(Home.this,android.R.style.Theme_Material_Dialog_Alert)
                                                .setTitle("ABOUT THE ATMOSPHERE OF BEDROOM !")
                                                .setMessage("The temperature is " + tempBed + " °C" + " and           The humid is " + humidBed + " %")
                                                .setIcon(R.drawable.ic_notification)
                                                .setCancelable(false)
                                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).create().show();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    // xử lý điều khiển kithen bằng giọng nói

                    final DatabaseReference light_kit = database.getReference("Kitchen/Light");


                    if (vttxt.equals("turn on kitchen light") || vttxt.equals("please turn on kitchen light") ||
                            vttxt.equals("i want to turn on kitchen light") || vttxt.equals("please i want to turn on kitchen light") ||
                            vttxt.equals("i want to turn on the light of kitchen") || vttxt.equals("please i want to turn on the light of kitchen")) {

                        light_kit.setValue("Open");
                        Toast.makeText(Home.this, "Turn on kitchen light", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("turn off kitchen light") || vttxt.equals("please turn off kitchen light") ||
                            vttxt.equals("i want to turn off kitchen light") || vttxt.equals("please i want to turn off kitchen light") ||
                            vttxt.equals("i want to turn off the light of kitchen") || vttxt.equals("please i want to turn off the light of kitchen")) {
                        light_kit.setValue("Close");
                        Toast.makeText(Home.this, "Turn off kitchen light", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }


                    final DatabaseReference fan_kit = database.getReference("Kitchen/Fan");


                    if (vttxt.equals("turn on kitchen fan") || vttxt.equals("please turn on kitchen fan") ||
                            vttxt.equals("i want to turn on kitchen fan") || vttxt.equals("please i want to turn on kitchen fan") ||
                            vttxt.equals("i want to turn on the fan of kitchen") || vttxt.equals("please i want to turn on the fan of kitchen")) {

                        fan_kit.setValue("Open");
                        Toast.makeText(Home.this, "Turn on kitchen fan", Toast.LENGTH_SHORT).show();
                        check_voice = true;

                    } else if ( vttxt.equals("turn off kitchen fan") || vttxt.equals("please turn off kitchen fan") ||
                            vttxt.equals("i want to turn off kitchen fan") || vttxt.equals("please i want to turn off kitchen fan") ||
                            vttxt.equals("i want to turn off the fan of kitchen") || vttxt.equals("please i want to turn off the fan of kitchen")) {
                        fan_kit.setValue("Close");
                        Toast.makeText(Home.this, "Turn off kitchen fan", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                    }


                    final DatabaseReference line = database.getReference("Kitchen/Clothes_line");
                    Boolean checkLineStatus = false;

                    if (vttxt.equals("the line in") || vttxt.equals("please the line in")) {
                        line.setValue("In");
                        Toast.makeText(Home.this, "Pull in clothes line indoors !!!", Toast.LENGTH_SHORT).show();
                        check_voice = true;
                        checkLineStatus = true;

                    } else if ( vttxt.equals("the line out") || vttxt.equals("please the line out")) {

                        line.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String value = snapshot.getValue(String.class);
                                if (value.equals("Rain_In")){
                                    new AlertDialog.Builder(Home.this)
                                            .setTitle("⚠️WARNING")
                                            .setMessage("It's raining, Please don't pull out your clothes line ❗️❗️❗️")
                                            .setCancelable(false)
                                            .setPositiveButton("\uD83D\uDC4D OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).create().show();
                                } else if (value.equals("In") ) {
                                    Toast.makeText(Home.this, "You must enter ktchen panel to do this request !!!", Toast.LENGTH_SHORT).show();
                                    new AlertDialog.Builder(Home.this)
                                            .setTitle("⚠️WARNING")
                                            .setMessage("Pull in manually task only be done in kitchen panel with this request !!!")
                                            .setCancelable(false)
                                            .setPositiveButton("\uD83D\uDC4D OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).create().show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    // cảnh báo nếu từ giọng nói điều khiển bị sai

                    check_voice = !check_voice;

                    if (check_voice) {

                        vtText.setError("There is an error or your request is so confusing. Please try again !!!");
                        vtText.requestFocus();
                        check_voice = !check_voice;
                        vtText.setText("");
                    }

                    break;
                }

        }
    }

}