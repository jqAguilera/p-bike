package cl.jdcsolutions.p_bike.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

import cl.jdcsolutions.p_bike.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FotoBicicleta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FotoBicicleta extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String idBicicleta;
    TextView tvText;
    View view;
    ImageView imageView;
    Button btnSubirFoto, btnAtras;
    FragmentTransaction transaction;
    Fragment listBicis;

    public FotoBicicleta() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FotoBicicleta.
     */
    // TODO: Rename and change types and number of parameters
    public static FotoBicicleta newInstance(String param1, String param2) {
        FotoBicicleta fragment = new FotoBicicleta();
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
        view = inflater.inflate(R.layout.fragment_foto_bicicleta, container, false);

        tvText = view.findViewById(R.id.tvTest);
        imageView = view.findViewById(R.id.ivFoto);
        btnSubirFoto = view.findViewById(R.id.btnSubirFoto);
        btnAtras = view.findViewById(R.id.btnAtras);

        mostrarImg();

        btnSubirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagen();

                transaction = getActivity().getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.contenedorPrincipal,listBicis).commit();
            }
        });

        listBicis = new BicisFragment();

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction = getActivity().getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.contenedorPrincipal,listBicis).commit();
            }
        });


        return view;
    }

    public void mostrarImg(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("bicicletas/" + idBicicleta);


        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();

            Glide.with(this).load(imageUrl).into(imageView);
        }).addOnFailureListener(exception -> {
            tvText.setText("Error!, Bicicleta sin Foto.");
        });

    }
    public void subirImagen(){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Aquí puedes llamar al método para subir la imagen a Firebase Storage
            subirImagenFirebase(imageUri, idBicicleta);
        }
    }

    private void subirImagenFirebase(Uri imageUri, String idBici) {

        eliminarFoto(idBici);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference fileRef = storageRef.child("bicicletas/" + idBici);


        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getActivity(), "Imagen Subida con exito!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getActivity(), "Error al subir imagen!", Toast.LENGTH_SHORT).show();
                });
    }

    public void eliminarFoto(String idBici){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        StorageReference desertRef = storageRef.child("bicicletas/" + idBici);


        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

    }
}