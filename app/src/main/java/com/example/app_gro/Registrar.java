package com.example.app_gro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

public class Registrar extends AppCompatActivity {
    EditText username, email, passwd, confirmPasswd;
    ImageButton imgButtonGoogle;
    Button registerUser;
    FirebaseAuth firebaseAuth;
    Dialog dialog;
    protected ProgressDialog progressDialog;
    private GoogleSignInClient signInClient;
    private final static int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        imgButtonGoogle = findViewById(R.id.imgButtonGoogleRegister);
        registerUser = findViewById(R.id.btnRegisterUser);
        email = findViewById(R.id.editTextCorreoRegister);
        username = findViewById(R.id.editTextNombreRegister);
        passwd = findViewById(R.id.editTextPassRegister);
        dialog = new Dialog(Registrar.this);
        confirmPasswd = findViewById(R.id.editTextConfirmPassRegister);
        progressDialog = new ProgressDialog(Registrar.this);
        firebaseAuth = FirebaseAuth.getInstance();
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = email.getText().toString();
                String pass = passwd.getText().toString();
                String confirmPass = confirmPasswd.getText().toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    email.setError("Correo Inválido");
                    email.setFocusable(true);
                }else if(pass.length() < 6){
                    passwd.setError("La contraseña debe tener al menos 6 dígitos");
                    passwd.setFocusable(true);
                }else if(!pass.equals(confirmPass)){
                    confirmPasswd.setError("Las contraseñas no coinciden");
                    confirmPasswd.setFocusable(true);
                }else{
                    registrar(correo, pass);
                }
            }
        });
    }

    private void registrar(String correo, String pass) {
    }
}