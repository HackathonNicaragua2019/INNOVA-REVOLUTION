package com.example.app_gro;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment implements SensorEventListener2 {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView imageView;
    private SensorManager sensorManager;
    private TextView grados;
    private Sensor pressure;
    private static final int PICK_IMAGE = 100;
    ProgressDialog progressDialog;
    View view;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InicioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inicio, container, false);
        AppCompatImageButton imageButton2 = view.findViewById(R.id.imgButtonReciente2);
        AppCompatImageButton imageButton1 = view.findViewById(R.id.imgButtonReciente1);
        sensorManager = (SensorManager)view.getContext().getSystemService(Context.SENSOR_SERVICE);
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE, true);
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_UI);
        grados = view.findViewById(R.id.txtGrados);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setMessage("Analizando imagen..."); // Setting Message
                progressDialog.setTitle("Por favor, espere..."); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        progressDialog.dismiss();
                        startActivity(new Intent(view.getContext(), ResultadoDiagnostico.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });
        imageButton1.setOnClickListener(v -> startActivity(new Intent(view.getContext(), ResultadoDiagnostico.class)));
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        String txt = "";
        synchronized (this) {
            txt = event.values[0] + " ºC";
            grados.setText(txt);
            Toast.makeText(view.getContext(), Float.toString(event.values[0]), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(view.getContext(), Float.toString(event.values[0]), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Toast.makeText(view.getContext(), "OnAccurracy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_UI);
       // Toast.makeText(view.getContext(), "OnResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {
        Toast.makeText(view.getContext(),"onFlushCompleted", Toast.LENGTH_SHORT).show();
    }
}