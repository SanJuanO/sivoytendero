package com.sivoyrepartidor.acmarkettendero.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sivoyrepartidor.acmarkettendero.R;
import com.sivoyrepartidor.acmarkettendero.utils.LocalizacionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity  {


    ProgressDialog progressDialog;
    String token,pkRepartidor,URL_API,URL_API2,URL_COMPARTIR,NOMBRE,IMAGEN,APELLIDOS,TELEDONO,LATITUD,LONGITUD;
    AppBarConfiguration appBarConfiguration;
    TextView tvNombre,tvTelefono;
    ImageView imFoto;
    DrawerLayout drawerLayoutMain;
    LocalizacionUtils localiza;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL_API=getString(R.string.URL_HOST)+"NuevoTokenRepartidor";
        URL_API2=getString(R.string.URL_HOST)+"ActualizaLocalizacionRepartidor";
        SharedPreferences preferencias =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        pkRepartidor=preferencias.getString("PK","");
        NOMBRE=preferencias.getString("NOMBRE","");
        APELLIDOS=preferencias.getString("APATERNO","");
        APELLIDOS=preferencias.getString("AMATERNO","");
        TELEDONO=preferencias.getString("TELEFONO","");
        IMAGEN=preferencias.getString("IMAGEN","");

        drawerLayoutMain=findViewById(R.id.drawerLayoutMain);
        NavController navController = Navigation.findNavController(this, R.id.fragmentMainHost);
        NavigationView navView = findViewById(R.id.leftNavigationMain);
        NavigationUI.setupWithNavController(navView, navController);

        /*
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_bars);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setDrawerLayout(drawerLayoutMain)
                        .build();

        View header = navView.getHeaderView(0);
        tvNombre = (TextView) header.findViewById(R.id.tvNombreNavHeader);
        //tvTelefono = (TextView) header.findViewById(R.id.tvTelefonoLeftHeaderNav);
        imFoto = (ImageView) header.findViewById(R.id.ivHeaderNav);
        tvNombre.setText(NOMBRE);
        if(!IMAGEN.isEmpty()){
            Picasso.with(this).load(IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(imFoto);
        }

        registraToken();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        localiza=new LocalizacionUtils(this,MainActivity.this,locationCallback);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid= item.getItemId();
        if(item.getItemId()==16908332){
            drawerLayoutMain.openDrawer(Gravity.START);
        }else{
            drawerLayoutMain.setDrawerTitle(Gravity.CENTER,item.getTitle());
        }
        return super.onOptionsItemSelected(item);
    }

    public void registraToken(){

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();

                JSONObject datos=new JSONObject();
                try{
                    datos.put("PK",pkRepartidor);
                    datos.put("TOKEN",deviceToken);
                }catch (Exception e){}

                RequestQueue requstQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int re=response.getInt("resultado");
                                }catch (Exception e){
                                    String error=e.toString();

                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.e("Rest Response",error.toString());
                            }
                        }
                ){
                    //here I want to post data to sever
                };

                int MY_SOCKET_TIMEOUT_MS = 15000;
                int maxRetries = 2;
                jsonObjectRequest.setRetryPolicy(new
                                DefaultRetryPolicy(
                                MY_SOCKET_TIMEOUT_MS,
                                maxRetries,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );

                requstQueue.add(jsonObjectRequest);



            }
        });
    }
    boolean requestingLocationUpdates=true;
    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {

        if(localiza!=null){
            localiza.startLocationUpdates();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        localiza.stopLocationUpdates();
    }

    public LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                //Toast.makeText(getApplicationContext(),"Localizacion null",Toast.LENGTH_SHORT).show();
                return;
            }
            for (Location location : locationResult.getLocations()) {
                //Toast.makeText(getApplicationContext(),"latitud:"+location.getLatitude()+", Longitud:"+location.getLongitude(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    int REQUEST_LOCATION=1;
    public void pedirPermisos(){

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                ||(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) ) {

            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE},
                    REQUEST_LOCATION);

        }else{
            locationSend();
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationSend();
            } else {
                Toast.makeText(this, "Por favor es necesario aceptar los permisos para usar Polar", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void locationSend(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>()   {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LATITUD= String.valueOf(location.getLatitude());
                            LONGITUD= String.valueOf(location.getLongitude());

                            actualizaUbicacion();
                        }
                    }
                });

    }

    public void actualizaUbicacion(){

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK",pkRepartidor);
            datos.put("LATITUD",LATITUD);
            datos.put("LONGITUD",LONGITUD);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API2,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            int result = (int) response.get("resultado");

                            if(result == 1){


                            }else{


                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Rest Response",error.toString());
                    }
                }
        ){
            //here I want to post data to sever
        };

        int MY_SOCKET_TIMEOUT_MS = 15000;
        int maxRetries = 2;
        jsonObjectRequest.setRetryPolicy(new
                        DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        requstQueue.add(jsonObjectRequest);

    }

    @Override
    public void onStart() {
        super.onStart();
        pedirPermisos();

    }

    public void abreMenu(View view){
        DrawerLayout drawerLayoutMain=  findViewById(R.id.drawerLayoutMain);
        drawerLayoutMain.openDrawer(Gravity.START);
    }

    public void cerrarSesion(View view){
        SharedPreferences preferencias =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean("LOGUEADO",false);
        editor.commit();
        Intent main=new Intent(this, LoginActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(main);
        finish();
    }


}
