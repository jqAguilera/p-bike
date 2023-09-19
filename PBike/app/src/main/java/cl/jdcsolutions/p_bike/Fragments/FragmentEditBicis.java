package cl.jdcsolutions.p_bike.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import cl.jdcsolutions.p_bike.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentEditBicis#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEditBicis extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button btnGuardar, btnCancelar;

    String idBicicleta, marca, color;

    FirebaseFirestore mFirestore;

    View view;

    EditText Marca, Color;

    Fragment fragmentBicis;





    public FragmentEditBicis() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentEditBicis.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentEditBicis newInstance(String param1, String param2) {
        FragmentEditBicis fragment = new FragmentEditBicis();
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
            idBicicleta = getArguments().getString("idBici");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_bicis, container, false);
        getBici(idBicicleta);

        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentBicis = new BicisFragment();
                if (Marca.getText().toString().trim().isEmpty() || Color.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(), "Debes Completar los Datos!", Toast.LENGTH_SHORT).show();

                }else {
                    updateBici(idBicicleta);
                    changeFragment(fragmentBicis);
                    Toast.makeText(getActivity(), "Datos actualizados!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCancelar = view.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentBicis = new BicisFragment();
                changeFragment(fragmentBicis);
            }
        });




        return view;
    }

    public void updateBici(String id){

        Marca = view.findViewById(R.id.edtMarca);
        Color = view.findViewById(R.id.edtColor);

        DocumentReference datosBici = mFirestore.collection("bicicletas").document(id);
        datosBici.update("color", Color.getText().toString());
        datosBici.update("marca", Marca.getText().toString());


    }

    public void getBici(String id){



        Marca = view.findViewById(R.id.edtMarca);
        Color = view.findViewById(R.id.edtColor);

        mFirestore = FirebaseFirestore.getInstance();

        DocumentReference datosBici = mFirestore.collection("bicicletas").document(id);
        datosBici.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot datosBici, @Nullable FirebaseFirestoreException error) {
                Marca.setText(datosBici.getString("marca"));
                Color.setText(datosBici.getString("color"));

            }
        });

    }

    public void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorPrincipal, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}