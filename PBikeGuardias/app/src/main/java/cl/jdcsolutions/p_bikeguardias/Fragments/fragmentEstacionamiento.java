package cl.jdcsolutions.p_bikeguardias.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

import cl.jdcsolutions.p_bikeguardias.Lugar;
import cl.jdcsolutions.p_bikeguardias.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentEstacionamiento#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentEstacionamiento extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View vista;

    FirebaseFirestore mFirestore;

    Button btnActualizar;

    LinearLayout sw1, sw2, sw3, sw4, sw5;

    public fragmentEstacionamiento() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentEstacionamiento.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentEstacionamiento newInstance(String param1, String param2) {
        fragmentEstacionamiento fragment = new fragmentEstacionamiento();
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

        vista = inflater.inflate(R.layout.fragment_estacionamiento, container, false);

        ArrayList<Lugar> lugar;

        lugar = new ArrayList<Lugar>();

        sw1 = vista.findViewById(R.id.linearLayout1);
        sw2 = vista.findViewById(R.id.linearLayout2);
        sw3 = vista.findViewById(R.id.linearLayout3);
        sw4 = vista.findViewById(R.id.linearLayout4);
        sw5 = vista.findViewById(R.id.linearLayout5);

        obtenerEstados(lugar);
        btnActualizar = vista.findViewById(R.id.btnActualizar);

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerEstados(lugar);
            }
        });

        return vista;
    }

    public void obtenerEstados(ArrayList<Lugar> lugar){

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("lugar")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id;
                            boolean estado;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                                estado = document.getBoolean("estado");
                                lugar.add(new Lugar(id, estado));
                            }
                        }

                        for (Lugar i : lugar) {
                            switch(Integer.parseInt(i.getId())) {
                                case 1:
                                    if (i.isEstado()){
                                        sw1.setBackgroundColor(Color.GREEN);
                                    } else {
                                        sw1.setBackgroundColor(Color.RED);
                                    }
                                    break;
                                case 2:
                                    if (i.isEstado()){
                                        sw2.setBackgroundColor(Color.GREEN);
                                    } else {
                                        sw2.setBackgroundColor(Color.RED);
                                    }
                                    break;
                                case 3:
                                    if (i.isEstado()){
                                        sw3.setBackgroundColor(Color.GREEN);
                                    } else {
                                        sw3.setBackgroundColor(Color.RED);
                                    }
                                    break;
                                case 4:
                                    if (i.isEstado()){
                                        sw4.setBackgroundColor(Color.GREEN);
                                    } else {
                                        sw4.setBackgroundColor(Color.RED);
                                    }
                                    break;
                                case 5:
                                    if (i.isEstado()){
                                        sw5.setBackgroundColor(Color.GREEN);
                                    } else {
                                        sw5.setBackgroundColor(Color.RED);
                                    }
                                    break;
                                default:
                            }
                        }
                    }
                });

    }
}