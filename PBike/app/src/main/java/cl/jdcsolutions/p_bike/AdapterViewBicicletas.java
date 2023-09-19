package cl.jdcsolutions.p_bike;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cl.jdcsolutions.p_bike.Fragments.AgregarBicicleta;
import cl.jdcsolutions.p_bike.Fragments.FotoBicicleta;
import cl.jdcsolutions.p_bike.Fragments.FragmentEditBicis;
import cl.jdcsolutions.p_bike.Fragments.FragmentListBicis;
import cl.jdcsolutions.p_bike.Fragments.fragmentInfoRegistro;
import cl.jdcsolutions.p_bike.Objetos.Bicicleta;

public class AdapterViewBicicletas  extends RecyclerView.Adapter<AdapterViewBicicletas.ViewHolderBicis> {


    ArrayList<Bicicleta> listBicis;



    private Context context;
    private OnButtonClickListener listener;

    public interface OnButtonClickListener {
        void onButtonClick();
    }
    public AdapterViewBicicletas(Context context) {
        this.context = context;
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    public AdapterViewBicicletas(ArrayList<Bicicleta> listBicis) {
        this.listBicis = listBicis;

    }


    @NonNull
    @Override
    public ViewHolderBicis onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bicicletas, null,false);
        TextView tvEditar = view.findViewById(R.id.tvEditar);
        tvEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager(); // O getChildFragmentManager() si estás dentro de un fragmento
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                FragmentEditBicis editBicis = new FragmentEditBicis();

                fragmentTransaction.replace(R.id.contenedorBicis, editBicis); // Reemplaza "container" con el ID del contenedor donde deseas mostrar el fragmento
                fragmentTransaction.addToBackStack(null); // Agrega la transacción a la pila de retroceso (back stack)
                fragmentTransaction.commit();
            }
        });

        return new ViewHolderBicis(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewBicicletas.ViewHolderBicis holder, int position) {

        holder.crearBici(listBicis.get(position));

    }




    @Override
    public int getItemCount() {

        return listBicis.size();
    }

    public class ViewHolderBicis extends RecyclerView.ViewHolder{

        TextView tvMarca, tvColor, tvEditar, tvNumero, tvFoto;

        public ViewHolderBicis(@NonNull View itemView) {

            super(itemView);


            tvNumero = itemView.findViewById(R.id.tvNumero);
            tvMarca = itemView.findViewById(R.id.tvMarca);
            tvColor = itemView.findViewById(R.id.tvColor);
            tvEditar = itemView.findViewById(R.id.tvEditar);

            tvEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Bicicleta bici = listBicis.get(position);

                        FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager(); // O getChildFragmentManager() si estás dentro de un fragmento
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        FragmentEditBicis editBicis = new FragmentEditBicis();

                        Bundle bundle = new Bundle();
                        bundle.putString("idBici", bici.getId());
                        editBicis.setArguments(bundle);

                        fragmentTransaction.replace(R.id.contenedorBicis, editBicis);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();




                    }
                }
            });

            tvFoto = itemView.findViewById(R.id.tvVerFoto);
            tvFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Bicicleta bici = listBicis.get(position);

                        FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        FotoBicicleta fotoBicicleta = new FotoBicicleta();

                        Bundle bundle = new Bundle();
                        bundle.putString("idBici", bici.getId());
                        fotoBicicleta.setArguments(bundle);

                        fragmentTransaction.replace(R.id.contenedorBicis, fotoBicicleta);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();




                    }
                }
            });



        }


        public void crearBici(Bicicleta bicicleta) {

            String numero = String.valueOf(bicicleta.getNumero());
            tvMarca.setText(bicicleta.getMarca());
            tvColor.setText(bicicleta.getColor());
            tvNumero.setText(numero + ".-");

        }


    }


}
