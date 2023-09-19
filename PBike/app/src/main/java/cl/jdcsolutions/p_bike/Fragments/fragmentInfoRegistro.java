package cl.jdcsolutions.p_bike.Fragments;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cl.jdcsolutions.p_bike.R;
import cl.jdcsolutions.p_bike.databinding.ActivityMainBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentInfoRegistro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentInfoRegistro extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String Lugar;

    View view;

    TextView tvUserName, tvLugar1, tvHora1;

    FirebaseAuth mAuth;
    ImageButton btnQR;
    FirebaseFirestore mFirestore;

    Fragment Home;
    FragmentTransaction transaction;

    ActivityMainBinding binding;
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null){

        }else {
            Lugar = result.getContents();
            verificardorRegistro(Lugar);
        }
    });




    public fragmentInfoRegistro() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentInfoRegistro.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentInfoRegistro newInstance(String param1, String param2) {
        fragmentInfoRegistro fragment = new fragmentInfoRegistro();
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
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_info_registro, container, false);


        getInfo();


        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference datosUser = mFirestore.collection("usuarios").document(userId);
        datosUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot datosUser, @Nullable FirebaseFirestoreException error) {
                tvUserName = view.findViewById(R.id.tvUsername);
                tvUserName.setText(datosUser.getString("nombre"));

            }
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.getRoot();

        btnQR = view.findViewById(R.id.btnQR);



        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanner();


                /*testQR = vista.findViewById(R.id.tvCantDisponible);
                testQR.setText(etQRtest);
                */

            }


        });



        return view;
    }

    public void scanner() {


        ScanOptions options  = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Escanear Codigo!");
        options.setCameraId(0);
        options.setOrientationLocked(false);
        options.setBeepEnabled(true);
        options.setCaptureActivity(CaptureActivityPortraint.class);
        options.setBarcodeImageEnabled(false);

        barcodeLauncher.launch(options);


    }

    private void verificardorRegistro(String lugar){


        SimpleDateFormat h = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String hora = h.format(date);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String userId = mAuth.getCurrentUser().getUid();


        mFirestore.collection("registro")
                .whereEqualTo("id_alumno", userId).whereEqualTo("estado", true).whereEqualTo("idLugar", lugar)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documento = task.getResult();
                            if (documento.size() == 0){
                                mFirestore.collection("registro")
                                        .whereEqualTo("id_alumno", userId).whereEqualTo("estado", true)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                QuerySnapshot documento2 = task.getResult();
                                                if (documento2.size() == 0){

                                                } else {
                                                    Toast.makeText(getActivity(), "Error al Registrar!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference estadoLugar = mFirestore.collection("lugar").document(lugar);
                                    DocumentReference estadoRegistro = mFirestore.collection("registro").document(document.getId());
                                    DocumentReference estadoAlumno = mFirestore.collection("usuarios").document(userId);
                                    estadoAlumno.update("estado", false);
                                    estadoRegistro.update("estado", false);
                                    estadoRegistro.update("hora_salida", hora);
                                    estadoLugar.update("estado", false);
                                    registroCerrado();
                                    Toast.makeText(getActivity(), "Salida marcada con exito!", Toast.LENGTH_LONG).show();
                                }
                            }
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

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference infoRegistro = mFirestore.collection("registro").document(document.getId());
                                infoRegistro.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        String horaLlegada, lugarEstacionamiento;
                                        horaLlegada = value.getString("hora_llegada");
                                        lugarEstacionamiento = value.getString("idLugar");
                                        editInfoRegistro(lugarEstacionamiento, horaLlegada);
                                    }
                                });
                            }
                        } else {

                        }
                    }
                });
    }

    public void registroCerrado(){

        Home = new Home();
        transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.contenedorPrincipal,Home).commit();
    }

    public void editInfoRegistro(String lugar, String hora){


        tvHora1 = (TextView) view.findViewById(R.id.tvHora);
        tvHora1.setText(hora);
        tvLugar1 = (TextView) view.findViewById(R.id.tvLugar1);
        tvLugar1.setText(lugar);


    }
}