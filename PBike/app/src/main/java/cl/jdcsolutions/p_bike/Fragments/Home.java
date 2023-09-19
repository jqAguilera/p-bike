package cl.jdcsolutions.p_bike.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cl.jdcsolutions.p_bike.AdapterViewBicicletas;
import cl.jdcsolutions.p_bike.Objetos.Bicicleta;
import cl.jdcsolutions.p_bike.R;
import cl.jdcsolutions.p_bike.databinding.ActivityMainBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;





    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    String Lugar, userName;

    Fragment InfoRegistro1, Home;





    TextView tvUserName, tvLugaresDisp;

    // Ni idea que esa webada CONSULTAR!!

    ActivityMainBinding binding;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null){

        }else {
            Lugar = result.getContents();
            verificardorRegistro(Lugar);





        }
    });

    // Fin de la webada

    boolean pass;

    ImageButton btnQR;
    View vista;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getGuardia();

        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference datosUser = mFirestore.collection("usuarios").document(userId);
        datosUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot datosUser, @Nullable FirebaseFirestoreException error) {
                userName = datosUser.getString("nombre");
                tvUserName = vista.findViewById(R.id.tvUsername);
                tvUserName.setText(userName);

            }
        });


        lugaresDisponibles();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.getRoot();

        vista = inflater.inflate(R.layout.fragment_home, container, false);

        btnQR = vista.findViewById(R.id.btnQR);



        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanner();


            }


        });


        return vista;
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

    public void sendNotification(String topic, String message) {
        RemoteMessage notification = new RemoteMessage.Builder("/topics/" + topic)
                .setMessageId("asd")
                .addData("message", message)
                .build();

        FirebaseMessaging.getInstance().send(notification);
    }

    private void addRegistro(String Lugar){



        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat h = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String hora = h.format(date);
        String fecha = f.format(date);


        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        String userid = mAuth.getCurrentUser().getUid();

        

        String bici = loadPreferenceBici();
        String guardia = loadPreferenceGuardia();


        Map<String, Object> registro = new HashMap<>();

        registro.put("id_alumno", userid);
        registro.put("idLugar", Lugar);
        registro.put("fecha", fecha);
        registro.put("hora_llegada", hora);
        registro.put("hora_salida", null);
        registro.put("estado", true);
        registro.put("bicicleta", bici);
        registro.put("guardia", guardia);


        mFirestore.collection("registro").add(registro).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {


                DocumentReference estadoLugar = mFirestore.collection("lugar").document(Lugar);
                estadoLugar.update("estado", true);

                DocumentReference estadoAlumno = mFirestore.collection("usuarios").document(userid);
                estadoAlumno.update("estado", true);

                Toast.makeText(getActivity(), "Datos Guardados!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });




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

                                                        mFirestore.collection("registro").whereEqualTo("estado", true)
                                                                .whereEqualTo("idLugar", lugar)
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        QuerySnapshot document3 = task.getResult();
                                                                        if (document3.size() == 0){
                                                                            addRegistro(lugar);
                                                                            registroActivo();
                                                                        } else {
                                                                            Toast.makeText(getActivity(), "Error al Registrar!", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference estadoLugar = mFirestore.collection("lugar").document(lugar);
                                    DocumentReference estadoRegistro = mFirestore.collection("registro").document(document.getId());
                                    estadoRegistro.update("estado", false);
                                    estadoRegistro.update("hora_salida", hora);
                                    estadoLugar.update("estado", false);

                                    Toast.makeText(getActivity(), "Salida marcada con exito!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });


    }

    public void registroActivo(){

        InfoRegistro1 = new fragmentInfoRegistro();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorPrincipal, InfoRegistro1);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void lugaresDisponibles(){


        mFirestore = FirebaseFirestore.getInstance();


        mFirestore.collection("lugar").whereEqualTo("estado", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            String cant = String.valueOf(document.size());
                            tvLugaresDisp = vista.findViewById(R.id.tvCantDisponible);
                            tvLugaresDisp.setText(cant);

                        } else {

                        }
                    }
                });
    }



    private String loadPreferenceBici(){

        SharedPreferences preferences = getActivity().getSharedPreferences("Bicicleta", Context.MODE_PRIVATE);

        String bici1 = preferences.getString("bicicleta", "default");

        return bici1;
    }

    private void getGuardia(){
        mFirestore = FirebaseFirestore.getInstance();


        mFirestore.collection("guardiadeturno")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String guardiaId;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            guardiaId = document.getString("guardia");
                            savePreferenceGuardia(guardiaId);
                        }
                    }
                });

    }

    public void savePreferenceGuardia(String sesion){

        SharedPreferences preferences = getActivity().getSharedPreferences("Guardia", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("guardia", sesion);

        editor.commit();

    }

    private String loadPreferenceGuardia(){

        SharedPreferences preferences = getActivity().getSharedPreferences("Guardia", Context.MODE_PRIVATE);

        String bici1 = preferences.getString("guardia", "default");

        return bici1;
    }



}