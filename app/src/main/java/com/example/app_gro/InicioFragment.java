package com.example.app_gro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.icu.text.TimeZoneFormat;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
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
    private final int SOLICITUD_TOMAR_FOTO = 1;
    private final int SOLICITUD_SELECCIONAR_FOTO = 2;
    private SensorManager sensorManager;
    private TextView grados;
    public AppCompatImageButton imageButton1;
    public RelativeLayout rlSubirFoto;
    private final String permisoCamera = Manifest.permission.CAMERA;
    String permisoWriteStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String permisoReadStorage = Manifest.permission.READ_EXTERNAL_STORAGE;
    private Sensor pressure;
    String urlFoto = "";
    private static final int PICK_IMAGE = 100;
    ProgressDialog progressDialog;
    AppCompatImageButton imageButton2;
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
        imageButton2 = view.findViewById(R.id.imgButtonReciente2);
        imageButton1 = view.findViewById(R.id.imgButtonReciente1);
        sensorManager = (SensorManager)view.getContext().getSystemService(Context.SENSOR_SERVICE);
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE, true);
        rlSubirFoto = view.findViewById(R.id.rl1);
        RelativeLayout tomarFoto = view.findViewById(R.id.rlTomarFoto);
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
        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dispararIntentTomarFoto();
                capturarFoto();
            }
        });
        rlSubirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFoto();
            }
        });
        return view;
    }

    private void capturarFoto() {
        pedirPermisos();
    }

    private void seleccionarFoto() {
        pedirPermisosSeleccionarfoto();
    }

    private void pedirPermisosSeleccionarfoto() {
        boolean proveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permisoReadStorage);
        if(proveerContexto){
            solicitudPermisoSeleccionarFoto();
        }else{
            solicitudPermisoSeleccionarFoto();
        }
    }

    public void pedirPermisos(){
        boolean proveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permisoCamera);
        if(proveerContexto){
            solicitudPermiso();
        }else{
            solicitudPermiso();
        }
    }

    private void solicitudPermiso() {
        String permisoWriteStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String permisoReadStorage = Manifest.permission.READ_EXTERNAL_STORAGE;
        String[] arrayPermisos = new String[]{permisoCamera, permisoReadStorage, permisoWriteStorage};
        requestPermissions(arrayPermisos, SOLICITUD_TOMAR_FOTO);
    }
    private void solicitudPermisoSeleccionarFoto() {
        String permisoReadStorage = Manifest.permission.READ_EXTERNAL_STORAGE;
        String[] arrayPermisos = new String[]{permisoReadStorage};
        requestPermissions(arrayPermisos, SOLICITUD_SELECCIONAR_FOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case SOLICITUD_TOMAR_FOTO:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    try {
                        dispararIntentTomarFoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getContext(), "No se han otorgado permisos para tomar foto", Toast.LENGTH_SHORT).show();
                }
                break;
            case SOLICITUD_SELECCIONAR_FOTO:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dispararIntentSeleccionarFoto();
                }else{
                    Toast.makeText(getContext(), "No se han otorgado permisos para seleccionar una foto", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void dispararIntentSeleccionarFoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), SOLICITUD_SELECCIONAR_FOTO);
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
    public void dispararIntentTomarFoto() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getContext().getPackageManager()) != null){
            File archivoFoto = crearArchivoImagen();
            if(archivoFoto != null){
                Uri urlFotoUri = FileProvider.getUriForFile(getContext(), "com.example.app_gro", archivoFoto);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, urlFotoUri);
                startActivityForResult(intent, SOLICITUD_TOMAR_FOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SOLICITUD_TOMAR_FOTO:
                if(resultCode == Activity.RESULT_OK){
                    Uri uri = Uri.parse(urlFoto);
                    try {
                        InputStream stream = getContext().getContentResolver().openInputStream(uri);
                        Bitmap imageBitmap = BitmapFactory.decodeStream(stream);
                        imageButton1.setImageBitmap(imageBitmap);
                        agregarImagenGaleria();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }else {
                    Log.d("ACTIVITY_RESULT", String.valueOf(resultCode));
                }
                break;
            case SOLICITUD_SELECCIONAR_FOTO:
                if(resultCode == Activity.RESULT_OK){
                    Uri uri = Uri.parse(data.getData().toString());
                    try {
                        InputStream stream = getContext().getContentResolver().openInputStream(uri);
                        Bitmap imageBitmap = BitmapFactory.decodeStream(stream);
                        imageButton2.setImageBitmap(imageBitmap);
                        agregarImagenGaleria();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }


    }

    public void agregarImagenGaleria(){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(urlFoto);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        getContext().sendBroadcast(intent);
    }

    public File crearArchivoImagen() throws IOException {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String nombreArchivo = "JPEG" + timeStamp + "_";
        //File directory = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File directorio = Environment.getExternalStorageDirectory();
        File dirPicture = new File(directorio.getAbsolutePath() + "/Pictures/");
        File imagen = File.createTempFile(nombreArchivo, ".jpg", dirPicture);
        urlFoto = "file://" + imagen.getAbsolutePath();
        return imagen;
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