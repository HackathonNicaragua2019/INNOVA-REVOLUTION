package com.example.app_gro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Inicio extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    InicioFragment inicioFragment = new InicioFragment();
    ComunidadFragment comunidadFragment = new ComunidadFragment();
    PerfilFragment perfilFragment = new PerfilFragment();
    EstadisticaFragment estadisticaFragment = new EstadisticaFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("APPGRO");
        actionBar.setDisplayShowHomeEnabled(false);
        ((ActionBar) actionBar).setDisplayHomeAsUpEnabled(false);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.inicioNav);
        loadFragment(inicioFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.inicioNav:
                    loadFragment(inicioFragment);
                    return true;
                case R.id.comunidadNav:
                    loadFragment(comunidadFragment);
                    return true;
                case R.id.perfilNav:
                    loadFragment(perfilFragment);
                    return true;
                case R.id.estadisticaNav:
                    loadFragment(estadisticaFragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_fragment, fragment);
        transaction.commit();
    }
}