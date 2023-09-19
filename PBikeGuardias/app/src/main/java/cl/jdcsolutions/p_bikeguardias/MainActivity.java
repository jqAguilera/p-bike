package cl.jdcsolutions.p_bikeguardias;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import cl.jdcsolutions.p_bikeguardias.Fragments.fragmentDatosAlumno;
import cl.jdcsolutions.p_bikeguardias.Fragments.fragmentEstacionamiento;
import cl.jdcsolutions.p_bikeguardias.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {



    FirebaseAuth mAuth;

    FirebaseFirestore mFirestore;
    FragmentTransaction transaction;
    Fragment Estacionamiento, datosAlumno, Perfil;

    String userId, biciId;


    String Nombre, Rut, Carrera;

    TextView tvNombre, tvRut, tvCarrera, tvColor, tvMarca, tvRegistro;

    ImageView ivFoto;

    ActivityMainBinding binding;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null){

        }else {

            userId = result.getContents();

            verificarAlumno(userId);
        }
    });

    public void mostrarImg(String idBicicleta){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("bicicletas/" + idBicicleta);


        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();

            Glide.with(this).load(imageUrl).into(ivFoto);
        }).addOnFailureListener(exception -> {

        });

    }

    private void verificarAlumno(String userId) {

        datosAlumno = new fragmentDatosAlumno();

        mFirestore = FirebaseFirestore.getInstance();

        DocumentReference datosUser = mFirestore.collection("usuarios").document(userId);
        datosUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot datosUser, @Nullable FirebaseFirestoreException error) {
                Nombre = datosUser.getString("nombre");
                Rut = datosUser.getString("rut");
                Carrera = datosUser.getString("carrera");
                tvNombre = datosAlumno.getView().findViewById(R.id.tvNombre);
                tvNombre.setText(Nombre);
                tvCarrera = datosAlumno.getView().findViewById(R.id.tvCarrera);
                tvCarrera.setText(Carrera);
                tvRut = datosAlumno.getView().findViewById(R.id.tvRut);
                tvRut.setText(Rut);

                biciId = datosUser.getString("bicicletaOn");
                tvRegistro = datosAlumno.getView().findViewById(R.id.tvRegistro);
                if (datosUser.getBoolean("estado")){
                    tvRegistro.setText("Si ");
                    tvRegistro.setTextColor(Color.GREEN);
                } else {
                    tvRegistro.setText("No ");
                    tvRegistro.setTextColor(Color.RED);
                }

                ivFoto = datosAlumno.getView().findViewById(R.id.ivFoto);

                mostrarImg(biciId);

                DocumentReference datosBici = mFirestore.collection("bicicletas").document(biciId);
                datosBici.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot datosBici, @Nullable FirebaseFirestoreException error) {
                        tvColor = datosAlumno.getView().findViewById(R.id.tvColor);
                        tvMarca = datosAlumno.getView().findViewById(R.id.tvMarca);
                        tvMarca.setText(datosBici.getString("marca"));
                        tvColor.setText(datosBici.getString("color"));
                    }
                });

            }
        });



        transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.contenedorPrincipal,datosAlumno).commit();


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Estacionamiento = new fragmentEstacionamiento();

        getSupportFragmentManager().beginTransaction().add(R.id.contenedorPrincipal,Estacionamiento).commit();

        ImageButton btnEstacionamiento = (ImageButton) findViewById(R.id.btnEstacionamiento);

        btnEstacionamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.contenedorPrincipal,Estacionamiento).commit();


            }
        });
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.getRoot();

        ImageButton btnLeerQR = (ImageButton) findViewById(R.id.btnLeerQR);
        btnLeerQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanner();
            }
        });

        ImageButton btnSalir = (ImageButton) findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();

                mAuth.signOut();

            }

        });

    }


    @Override
    public void onStart() {


        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        try {
            //  Block of code to try
            String uId = mAuth.getCurrentUser().getUid();
            if(uId == null){

                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                DocumentReference userEstado = mFirestore.collection("guardiadeturno").document("1");
                userEstado.update("guardia", uId);

            }
        }
        catch(Exception e) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }

    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }



    public void scanner() {


        ScanOptions options  = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Escanear Codigo!");
        options.setCameraId(0);
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);
        options.setCaptureActivity(CaptureActivityPortraint.class);
        options.setBarcodeImageEnabled(false);

        barcodeLauncher.launch(options);


    }


}