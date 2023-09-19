package cl.jdcsolutions.p_bike.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cl.jdcsolutions.p_bike.AdapterViewBicicletas;
import cl.jdcsolutions.p_bike.R;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgregarBicicleta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgregarBicicleta extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentTransaction transaction;

    Button btnGuardar, btnCancelar, btnTest;

    EditText etMarca, etColor;

    View view;

    Fragment FragmentListBicis;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    public AgregarBicicleta() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgregarBicicleta.
     */
    // TODO: Rename and change types and number of parameters
    public static AgregarBicicleta newInstance(String param1, String param2) {
        AgregarBicicleta fragment = new AgregarBicicleta();
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

        view = inflater.inflate(R.layout.fragment_agregar_bicicleta, container, false);


        FragmentListBicis = new FragmentListBicis();

        etMarca = view.findViewById(R.id.etMarca);
        etColor = view.findViewById(R.id.etColor);

        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                transaction = getActivity().getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.contenedorBicis,FragmentListBicis).commit();

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Color = etColor.getText().toString().trim();
                String Marca = etMarca.getText().toString().trim();

                if (Color.isEmpty() || Marca.isEmpty()){

                    Toast.makeText(getActivity(), "Debe Completar los campos!", Toast.LENGTH_SHORT).show();

                } else {

                    saveBicicleta(Marca,Color);

                    transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.contenedorBicis,FragmentListBicis).commit();

                }

            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void saveBicicleta(String Marca, String Color){

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        String id = mAuth.getCurrentUser().getUid();

        Map<String, Object> bici = new HashMap<>();

        bici.put("id_usuario", id);
        bici.put("marca", Marca);
        bici.put("color", Color);

        mFirestore.collection("bicicletas").add(bici).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                Toast.makeText(getActivity(), "Datos Guardados!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });


    }




}