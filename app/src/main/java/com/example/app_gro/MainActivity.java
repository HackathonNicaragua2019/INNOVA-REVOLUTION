package com.example.app_gro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class MainActivity extends AppCompatActivity {
    AppCompatButton btnRegistrar, btnIniciarSesion;
    ImageButton imgButtonGoogle, imgButtonFacebook;
    EditText passwd, correo;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    Dialog dialog;
    private GoogleSignInClient signInClient;
    private final static int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRegistrar = findViewById(R.id.btnLoginRegister);
        btnIniciarSesion = findViewById(R.id.btnLoginAcceder);
        imgButtonGoogle = findViewById(R.id.imgLoginButtonGoogle);
        passwd = findViewById(R.id.txtPasswdLogin);
        correo = findViewById(R.id.editTextCorreoLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(MainActivity.this);
        dialog = new Dialog(MainActivity.this);
        crearSolicitud();
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registrar.class));
            }
        });
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =correo.getText().toString();
                String pass = passwd.getText().toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    correo.setError("Correo Inválido");
                    correo.setFocusable(true);
                }else if(pass.length() < 6){
                    passwd.setError("La contraseña debe contener al menos 6 dígitos");
                    passwd.setFocusable(true);
                }else{
                    loginUser(email, pass);
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
        Intent signIntent = signInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
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
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("USUARIOS_APP");
                                reference.child(uid).setValue(datoUsuario);
                            }
                            startActivity(new Intent(MainActivity.this, Inicio.class));
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

    private void loginUser(String email, String pass) {
        progressDialog.setTitle("Ingresando");
        progressDialog.setMessage("Por favor espere...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            startActivity(new Intent(MainActivity.this, Inicio.class));
                            finish();
                        }else{
                            progressDialog.dismiss();
                            dialogNoInicio();
                        }
                    }
                });
    }

    private void crearSolicitud() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("610283412741-nq22g5n7mpe7q84qi1fi8ju7rukqtetm.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);
    }
}