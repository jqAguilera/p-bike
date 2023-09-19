package cl.jdcsolutions.p_bike;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginApp extends AppCompatActivity {



    private String userRol;
    Button btnIniciarSesion;
    EditText etCorreo, etPassword;

    TextView btnRegistro;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_app);



        if (loadPreference()){

            Intent intent= new Intent (LoginApp.this, MainActivity.class);
            startActivity(intent);

        }

        mAuth = FirebaseAuth.getInstance();

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String correoUser = etCorreo.getText().toString().trim();
                String passUser = etPassword.getText().toString().trim();

                if (correoUser.isEmpty() || passUser.isEmpty()){

                    Toast.makeText(LoginApp.this, "Debe Ingresar los datos!", Toast.LENGTH_SHORT).show();

                }else {

                    loginUser(correoUser, passUser);

                }

            }
        });


    }

    private void loginUser(String correoUser, String passUser){

        mAuth.signInWithEmailAndPassword(correoUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String userId = mAuth.getCurrentUser().getUid();

                    if (task.isSuccessful()){
                        mFirestore = FirebaseFirestore.getInstance();
                        DocumentReference datosUser = mFirestore.collection("usuarios").document(userId);
                        datosUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot datosUser, @Nullable FirebaseFirestoreException error) {
                                userRol = datosUser.getString("rol");
                                boolean sesion;
                                if (userRol.equals("alumno")){


                                    Intent intent = new Intent(LoginApp.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(LoginApp.this, "Bienvenido!", Toast.LENGTH_SHORT).show();
                                    sesion = true;
                                    savePreference(sesion);

                                }else {

                                    Toast.makeText(LoginApp.this, "Acceso denegado!", Toast.LENGTH_SHORT).show();
                                    sesion = false;
                                    savePreference(sesion);
                                }
                            }
                        });
                    }else {
                        Toast.makeText(LoginApp.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginApp.this, "Error al Iniciar Sesion!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void savePreference(boolean sesion){

        SharedPreferences preferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sesion", sesion);

        editor.commit();

    }

    private boolean loadPreference(){

        SharedPreferences preferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        boolean sesion = preferences.getBoolean("sesion", false);

        return sesion;
    }

}