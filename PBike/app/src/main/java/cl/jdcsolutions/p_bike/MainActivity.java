package cl.jdcsolutions.p_bike;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.jdcsolutions.p_bike.Fragments.BicisFragment;
import cl.jdcsolutions.p_bike.Fragments.FragmentListBicis;
import cl.jdcsolutions.p_bike.Fragments.Home;
import cl.jdcsolutions.p_bike.Fragments.PerfilFragment;
import cl.jdcsolutions.p_bike.Fragments.fragmentInfoRegistro;
import cl.jdcsolutions.p_bike.Objetos.Bicicleta;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction transaction;
    Fragment HomeFragment, PerfilFragment, BicisFragment, infoRegistro;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    @Override
    public void onBackPressed() {

    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        askNotificationPermission();

        HomeFragment = new Home();
        PerfilFragment = new PerfilFragment();
        BicisFragment = new BicisFragment();



        subTemaFCM();
        regTokenFCM();
        getInfo();

        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                getInfo();

                sendNotification("EstadoEstacionamiento", "TEST APP");

            }
        });

        ImageButton btnPerfil = (ImageButton) findViewById(R.id.btnPerfil);

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.contenedorPrincipal,PerfilFragment).commit();
            }
        });

        ImageButton btnBicis = (ImageButton) findViewById(R.id.btnBicis);

        btnBicis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedorPrincipal,BicisFragment).commit();

            }
        });




    }
    
    public void regTokenFCM(){




        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        String token = task.getResult();
                        System.out.println(token);

                        saveToken(token);



                    }
                });
    }

    private void saveToken(String token){

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        mFirestore.collection("tokenFCM")
                .whereEqualTo("token", token)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String Marca, Color, id;
                            QuerySnapshot document = task.getResult();
                            if(document.size() == 0){
                                String userId = mAuth.getUid();
                                Map<String, Object> Token = new HashMap<>();

                                Token.put("id_alumno", userId);
                                Token.put("token", token);


                                mFirestore.collection("tokenFCM").add(Token).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error al guardar Token!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {

                        }
                    }
                });





    }



    public void getInfo(){


        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String userId = mAuth.getUid();




        mFirestore.collection("registro").whereEqualTo("id_alumno", userId).whereEqualTo("estado", true).whereEqualTo("hora_salida", null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        HomeFragment = new Home();
                        infoRegistro = new fragmentInfoRegistro();


                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();

                            if (document.size() != 0){

                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.contenedorPrincipal,infoRegistro).commit();

                            }else if (document.size() == 0){
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.contenedorPrincipal,HomeFragment).commit();
                            }

                        } else {

                        }
                    }
                });
    }

    public void subTemaFCM(){
        FirebaseMessaging.getInstance().subscribeToTopic("EstadoEstacionamiento")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }

                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void sendNotification(String topic, String message) {
        RemoteMessage notification = new RemoteMessage.Builder("/topics/" + topic)
                .setMessageId("asd")
                .addData("message", message)
                .build();

        FirebaseMessaging.getInstance().send(notification);
    }
}