package cl.jdcsolutions.p_bike.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;

import cl.jdcsolutions.p_bike.Objetos.Bicicleta;
import cl.jdcsolutions.p_bike.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BicisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BicisFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    Button btnAgregar, btnSpinner;

    View view;

    Spinner spinner;
    TextView tvMarca, tvColor;
    ArrayAdapter<String> spinnerAdapter;
    ArrayList<Bicicleta> bicicleta;

    FragmentTransaction transaction;

    Fragment AgregarBicis, ListBicis;

    public BicisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BicisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BicisFragment newInstance(String param1, String param2) {
        BicisFragment fragment = new BicisFragment();
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
        view = inflater.inflate(R.layout.fragment_bicis, container, false);

        spinner = view.findViewById(R.id.spinner);
        tvColor = view.findViewById(R.id.tvColor);
        tvMarca = view.findViewById(R.id.tvMarca);

        spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);




        bicicleta = new ArrayList<Bicicleta>();
        obtenerDatosSpinner(bicicleta);

        ListBicis = new FragmentListBicis();

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.contenedorBicis,ListBicis).commit();


        // Inflate the layout for this fragment
        return view;
    }

    private void obtenerDatosSpinner(ArrayList<Bicicleta> bicicleta) {

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String userId = mAuth.getCurrentUser().getUid();

        mFirestore.collection("bicicletas").whereEqualTo("id_usuario", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        spinnerAdapter.clear();
                        int item = 0;
                        String Marca, Color, id;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            item++;
                            spinnerAdapter.add("N° " + item);
                            Marca = document.getString("marca");
                            Color = document.getString("color");
                            id = document.getId();


                            bicicleta.add(new Bicicleta(Marca, Color, id, item));
                        }

                        spinnerAdapter.notifyDataSetChanged();
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                                Bicicleta bici = bicicleta.get(position);

                                tvMarca.setText(bici.getMarca());
                                tvColor.setText(bici.getColor());

                                DocumentReference biciOn = mFirestore.collection("usuarios").document(userId);
                                biciOn.update("bicicletaOn", bici.getId());

                                savePreference(bici.getId());

                                System.out.println("-------------------");
                                System.out.println("-------------------");
                                System.out.println("-------------------");
                                System.out.println(position);
                                System.out.println(bicicleta.size());
                                System.out.println("-------------------");
                                System.out.println("-------------------");
                                System.out.println("-------------------");
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void savePreference(String bici){

        SharedPreferences preferences = getActivity().getSharedPreferences("Bicicleta", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("bicicleta", bici);

        editor.commit();

    }
}