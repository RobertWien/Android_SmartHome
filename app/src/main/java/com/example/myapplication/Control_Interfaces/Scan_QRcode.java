package com.example.myapplication.Control_Interfaces;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.myapplication.R;
import com.google.zxing.Result;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Scan_QRcode extends Fragment {

    private CodeScanner mCodeScanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // final Activity activity = getActivity();

        View root = inflater.inflate(R.layout.fragment_scan__q_rcode, container, false);

        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(getActivity(), scannerView);

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();

                        if (result.getText() != null ) {

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("\uD83C\uDFE0 SCAN RESULT")
                                    .setCancelable(false)
                                    .setMessage(result.getText())
                                    .setPositiveButton("Copy and Open", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                                            ClipData data = ClipData.newPlainText("result", result.getText());

                                            Toast.makeText(getActivity(), "Copy to Clipboard", Toast.LENGTH_SHORT).show();
                                            manager.setPrimaryClip(data);

                                            // mở Dialogue để chọn cách mở URL

                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("\uD83C\uDFE0 Open the URL")
                                                    .setMessage(result.getText())
                                                    .setPositiveButton("Web Browser", new DialogInterface.OnClickListener() {

                                                        // xử lý mở bằng Browser
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent browserIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse(result.getText()));
                                                            startActivity(browserIntent);
                                                        }
                                                        // xử lý mở bằng Custum View
                                                    }).setNegativeButton("Custom Tab", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    CustomTabsIntent.Builder customTabIntent = new CustomTabsIntent.Builder();
                                                    customTabIntent.setToolbarColor(Color.parseColor("#2196F3"));

                                                    openCustomTabs(getActivity(),customTabIntent.build(), Uri.parse(result.getText()));

                                                }
                                            }).create().show();

                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                        }
                    }
                });
            }
        });
        return root;
    }
    // hàm xử lý mở bằng custom tab
    public static void openCustomTabs(Activity activity, CustomTabsIntent customTabsIntent,
                                      Uri uri){
        String packageName = "com.android.chrome";

        if (packageName != null){
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity,uri);
        } else {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,uri));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}