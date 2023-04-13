package com.example.overlay.idea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this,UpdateActivity.class));
    }

//    public void checkUpdate(View view) {
//        //UpdateActivity.start(this);
//
//    }


}