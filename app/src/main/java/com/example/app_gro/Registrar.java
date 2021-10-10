package com.example.app_gro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

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
        imgButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        startActivityForResult(new Intent(signInClient.getSignInIntent()), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authenticationFirebase(account);
            }catch (ApiException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void authenticationFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()){
                                assert user != null;
                                String uid = user.getUid();
                                String correo = user.getEmail();
                                String nombre = user.getDisplayName();
                                HashMap<Object, String> datoUsuario = new HashMap<>();
                                datoUsuario.put("uid", uid);
                                datoUsuario.put("nombre", nombre);
                                datoUsuario.put("correo", correo);
                                //datoUsuario.put("pass", pass);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("USUARIOS_APP");
                                reference.child(uid).setValue(datoUsuario);
                            }
                            startActivity(new Intent(Registrar.this, Inicio.class));
                        }else{
                            dialogNoInicio();
                        }
                    }
                });
    }

    private void dialogNoInicio() {
        Button okNoInicio;
        dialog.setContentView(R.layout.no_session);
        okNoInicio = dialog.findViewById(R.id.okInicio);
        okNoInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void registrar(String correo, String pass) {
        progressDialog.setTitle("Registrando");
        progressDialog.setMessage("Por favor espere...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            String uid = user.getUid();
                            String correo = email.getText().toString();
                            String pass = passwd.getText().toString();
                            String nombre = username.getText().toString();
                            HashMap<Object,String> datoUsuario = new HashMap<>();
                            datoUsuario.put("uid", uid);
                            datoUsuario.put("nombre", nombre);
                            datoUsuario.put("correo", correo);
                            datoUsuario.put("pass", pass);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("USUARIOS_APP");
                            reference.child(uid).setValue(datoUsuario);
                            Toast.makeText(Registrar.this, "¡Registro Exitoso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Registrar.this, Inicio.class));
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(Registrar.this, "¡Ha ocurrido un error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Registrar.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}