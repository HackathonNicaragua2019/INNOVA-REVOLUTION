package com.example.app_gro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class PantallaCarga extends AppCompatActivity {
    ImageView logo, appName, splash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);
        logo = findViewById(R.id.logo);
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.tween_animation);
        logo.startAnimation(animacion);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(PantallaCarga.this, MainActivity.class));
            }
        }, 3000);
    }
}