package cl.jdcsolutions.p_bike.Fragments;

import android.location.GnssAntennaInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayoutStates;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

import cl.jdcsolutions.p_bike.AdapterViewBicicletas;
import cl.jdcsolutions.p_bike.Objetos.Bicicleta;
import cl.jdcsolutions.p_bike.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentListBicis#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentListBicis extends Fragment implements AdapterViewBicicletas.OnButtonClickListener  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    FragmentTransaction transaction;

    Fragment EditarBici, AgregarBicis;
    Button btnAgregar;

    ArrayList<Bicicleta> bicicleta;

    View view;

    RecyclerView rvBicis;

    private AdapterViewBicicletas adapter;


    public FragmentListBicis() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentListBicis.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentListBicis newInstance(String param1, String param2) {
        FragmentListBicis fragment = new FragmentListBicis();
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
        view = inflater.inflate(R.layout.fragment_list_bicis, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        bicicleta = new ArrayList<Bicicleta>();


        listarBicis(bicicleta);
        adapterView(bicicleta);

        AgregarBicis = new AgregarBicicleta();

        btnAgregar = view.findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction = getActivity().getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.contenedorBicis,AgregarBicis).commit();
            }
        });


        return view;
    }

    public void listarBicis(ArrayList<Bicicleta> bicicleta){

        rvBicis = view.findViewById(R.id.rvBicis);


        String userId = mAuth.getCurrentUser().getUid();

        mFirestore.collection("bicicletas")
                .whereEqualTo("id_usuario", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String Marca, Color, id;
                            int numero = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                numero++;
                                Marca = document.getString("marca");
                                Color = document.getString("color");
                                id = document.getId();


                                bicicleta.add(new Bicicleta(Marca, Color, id, numero));

                            }

                            rvBicis.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                            AdapterViewBicicletas adapterBicis = new AdapterViewBicicletas(bicicleta);
                            rvBicis.setAdapter(adapterBicis);


                        } else {

                        }
                    }
                });

    }

    public void adapterView(ArrayList<Bicicleta> bicicleta){


        bicicleta = new ArrayList<Bicicleta>();
        AdapterViewBicicletas adapter = new AdapterViewBicicletas(getActivity());
        adapter.setOnButtonClickListener(this);
        rvBicis.setAdapter(adapter);


    }

    public void openFragment(){
        EditarBici = new FragmentEditBicis();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorPrincipal, EditarBici);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }





    @Override
    public void onButtonClick() {

    }
}