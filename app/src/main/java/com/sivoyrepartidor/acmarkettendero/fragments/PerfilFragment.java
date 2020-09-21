package com.sivoyrepartidor.acmarkettendero.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.sivoyrepartidor.acmarkettendero.R;
import com.sivoyrepartidor.acmarkettendero.activities.LoginActivity;
import com.google.android.material.navigation.NavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    MenuItem menuItem1;
    String NOMBRE,PRIMER_APELLIDO,SEGUNDO_APELLIDO,TELEFONO;
    EditText etNombre,etPrimerA,etSApellido,etTelefono;
    ImageView ivPerfil;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
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

        NavigationView navigationLeft = getActivity().findViewById(R.id.leftNavigationMain);
        Menu drawer_menu1 = navigationLeft.getMenu();
        menuItem1 = drawer_menu1.findItem(R.id.perfilFragment);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_perfil, container, false);

        etNombre=view.findViewById(R.id.etNombrePerfilFr);
        etPrimerA=view.findViewById(R.id.etPApellidoPerfilfr);
        etSApellido=view.findViewById(R.id.etSApellidoPerfilFr);
        etTelefono=view.findViewById(R.id.etTelefonoPerfilFr);

        SharedPreferences preference=getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        NOMBRE=preference.getString("NOMBRE","");
        PRIMER_APELLIDO=preference.getString("APATERNO","");
        SEGUNDO_APELLIDO=preference.getString("AMATERNO","");
        TELEFONO=preference.getString("TELEFONO","");

        etNombre.setText(NOMBRE);
        etPrimerA.setText(PRIMER_APELLIDO);
        etSApellido.setText(SEGUNDO_APELLIDO);
        etTelefono.setText(TELEFONO);

        if(!menuItem1.isChecked())
        {
            menuItem1.setChecked(true);
        }
        // Inflate the layout for this fragment
        return view;
    }

    public void abreMenu(View view){
        DrawerLayout drawerLayoutMain=  getActivity().findViewById(R.id.drawerLayoutMain);
        drawerLayoutMain.openDrawer(Gravity.START);
    }

    public void cerrarSesion(View view){
        SharedPreferences preferencias =getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean("LOGUEADO",false);
        editor.commit();
        Intent main=new Intent(getContext(), LoginActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(main);
        getActivity().finish();
    }


}
