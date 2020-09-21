package com.sivoyrepartidor.acmarkettendero.activities;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sivoyrepartidor.acmarkettendero.R;
import com.sivoyrepartidor.acmarkettendero.adapters.ProductosCarritoAdapter;
import com.sivoyrepartidor.acmarkettendero.model.EstatusModel;
import com.sivoyrepartidor.acmarkettendero.model.PedidoDetalleModel;
import com.sivoyrepartidor.acmarkettendero.model.PedidoModel;
import com.sivoyrepartidor.acmarkettendero.model.ProductosModel;
import com.sivoyrepartidor.acmarkettendero.utils.LocalizacionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PedidoDetalleActivity extends Activity {

    ListView listaView;
    TextView tvTarifa,tvTotal,tvNoPedido,tvCliente,tvDireccionCliente,tvDireccionTienda,tvMetodoPago,tvTelefonoCliente;
    List<ProductosModel> listaPedido;
    Spinner spEstatusPedido;
    ProductosCarritoAdapter productosAdapter;
    PedidoModel pedido;
    private FusedLocationProviderClient fusedLocationClient;
    String pkRepartidor,URL_API,URL_API2,URL_API3,URL_API4,LATITUD,LONGITUD,ESTATUS_ID;
    ArrayAdapter<String> comboAdapterEstatus;
    List<EstatusModel>listaEstatus;
    List<String>listaEstatusS;
    LocalizacionUtils localiza;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_detalle);
        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        pedido= (PedidoModel)bolsa.getSerializable("pedido");

        URL_API=getString(R.string.URL_HOST)+"ActualizaLocalizacionRepartidor";
        URL_API2=getString(R.string.URL_HOST)+"CambiaEstatusPedidoRepartidor";
        URL_API3=getString(R.string.URL_HOST)+"ObtenerEstatusPedidos";
        URL_API4=getString(R.string.URL_HOST)+"ObtenerPedidoDetalleByPk";

        tvNoPedido=findViewById(R.id.tvNumeroPedido);
        tvCliente=findViewById(R.id.tvClientePedido);
        tvDireccionTienda=findViewById(R.id.tvDireccionTiendaPedido);
        tvDireccionCliente=findViewById(R.id.tvDireccionEntregaPedido);
        spEstatusPedido=findViewById(R.id.spEstatusPedido);
        tvMetodoPago=findViewById(R.id.tvMetodoPagoPedidoDetalle);
        tvTelefonoCliente=findViewById(R.id.tvTelefonoClientePedidoDetalle);

        tvTelefonoCliente.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                llamar();
                return  true;
            }
        });
        listaEstatusS= new ArrayList<>();
        comboAdapterEstatus = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, listaEstatusS);
        spEstatusPedido.setAdapter(comboAdapterEstatus);

        obtenerEstatusPedidos();

        tvNoPedido.setText(""+pedido.PK);
        tvCliente.setText(pedido.CLIENTE);
        tvDireccionTienda.setText(pedido.DIRECCION_TIENDA);
        tvDireccionCliente.setText(pedido.DIRECCION);
        listaView=findViewById(R.id.listaProductos);
        tvTarifa=findViewById(R.id.tvTarifaPedidoDetalle);
        tvTotal=findViewById(R.id.tvTotalPedidoDetalle);


        listaPedido=new ArrayList<>();
        ProductosModel aux;
        double total=0;
        for(PedidoDetalleModel detalle :pedido.LISTA){
            aux=new ProductosModel();
            aux.PRODUCTO=detalle.PRODUCTO;
            aux.PRECIO=detalle.PRECIO;
            aux.CANTIDAD=detalle.CANTIDAD;
            aux.IMAGEN=detalle.IMAGEN;
            total+=(aux.PRECIO*aux.CANTIDAD);
            listaPedido.add(aux);
        }


        productosAdapter = new ProductosCarritoAdapter(this, listaPedido);
        listaView.setAdapter(productosAdapter);

        total+=pedido.PRECIO_ENTREGA;
        tvTarifa.setText("Tarifa: $"+pedido.PRECIO_ENTREGA);
        tvTotal.setText("Total: $"+total);

        SharedPreferences preferences =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        pkRepartidor=preferences.getString("PK","");

        spEstatusPedido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ESTATUS_ID=""+(i+1);

                ActualizaEstatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        localiza=new LocalizacionUtils(this,PedidoDetalleActivity.this,locationCallback);


    }


    public  void abreDireccionTienda(View view){
        Uri intentUri = Uri.parse("geo:"+pedido.LATITUD_TIENDA+","+pedido.LONGITUD_TIENDA+"?z=16&q="+pedido.LATITUD_TIENDA+","+pedido.LONGITUD_TIENDA+""+pedido.DIRECCION_TIENDA);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);

    }

    public  void abreDireccionEntrega(View view){
        Uri intentUri = Uri.parse("geo:"+pedido.LATITUD+","+pedido.LONGITUD+"?z=16&q="+pedido.LATITUD+","+pedido.LONGITUD+""+pedido.DIRECCION);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);

    }

    int REQUEST_LOCATION=1;
    public void pedirPermisos(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Aquí muestras confirmación explicativa al usuario
                // por si rechazó los permisos anteriormente
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
        }else{
            locationSend();
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
                            String dir= getDireccion(getApplicationContext(),location);
                            SharedPreferences preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferencias.edit();
                            editor.putString("Lat",String.valueOf(location.getLatitude()));
                            editor.putString("Lon",String.valueOf(location.getLongitude()));
                            editor.putString("Direccion",dir);
                            editor.commit();
                        }
                    }
                });

    }

    public static String getDireccion(Context context, Location loc) {
        String direccion="";
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion=DirCalle.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return direccion;
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
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

    public void ActualizaEstatus(){

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Actualizando estatus...");
        progressDialog.show();


        JSONObject datos = new JSONObject();
        try {
            datos.put("PK",""+pedido.PK);
            datos.put("PK_ESTATUS",ESTATUS_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API2,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progressDialog.dismiss();

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
                        progressDialog.dismiss();
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
        obtenerPedidoDetalle();

    }

    public void obtenerEstatusPedidos(){

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Obteniendo estatus de pedido...");
        progressDialog.show();

        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API3, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progressDialog.dismiss();
                            int posicion=0;
                            int result = (int) response.get("resultado");

                            if(result == 1){
                                JSONArray status=response.getJSONArray("estatus");
                                EstatusModel aux;
                                if(listaEstatus==null){
                                    listaEstatus=new ArrayList<>();
                                }
                                listaEstatusS.clear();
                                for (int i=0;i<status.length();i++) {
                                    JSONObject producto= status.getJSONObject(i);
                                    aux=new EstatusModel();
                                    aux.PK=producto.getString("pk");
                                    aux.ESTATUS=producto.getString("estatus");
                                    listaEstatus.add(aux);
                                    listaEstatusS.add(aux.ESTATUS);
                                    if(aux.ESTATUS.equals(pedido.ESTATUS)){
                                        posicion=listaEstatusS.size()-1;
                                    }
                                }

                                comboAdapterEstatus.notifyDataSetChanged();
                                spEstatusPedido.setSelection(posicion);

                            }else{

                                String error=response.getString("mensaje");
                                _ShowAlert("Error",error);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
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

    public void obtenerPedidoDetalle(){

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Obteniendo detalle de pedido...");
        progressDialog.show();

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK",""+pedido.PK);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API4, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progressDialog.dismiss();
                            int result = (int) response.get("resultado");

                            if(result == 1){
                                JSONObject pedidoobj=response.getJSONObject("pedido");

                                pedido=new PedidoModel();
                                pedido.PK=pedidoobj.getInt("pk");
                                pedido.CLIENTE=pedidoobj.getString("cliente");
                                pedido.DIRECCION_TIENDA=pedidoobj.getString("direccioN_TIENDA");
                                pedido.DIRECCION=pedidoobj.getString("direccion");
                                pedido.PRECIO_ENTREGA=pedidoobj.getDouble("envio");
                                pedido.TOTAL=pedidoobj.getDouble("total");
                                pedido.TELEFONO_CLIENTE=pedidoobj.getString("telefonO_CLIENTE");
                                pedido.LATITUD=pedidoobj.getString("latitud");
                                pedido.LONGITUD=pedidoobj.getString("longitud");
                                pedido.LATITUD_TIENDA=pedidoobj.getString("latituD_TIENDA");
                                pedido.LONGITUD_TIENDA=pedidoobj.getString("longituD_TIENDA");

                                if(pedidoobj.get("metodO_PAGO").equals("T")) {
                                    pedido.METODO_PAGO = "Tarjeta";
                                    try {
                                        JSONObject cargoobj=pedidoobj.getJSONObject("cargo");
                                        pedido.METODO_PAGO="Tarjeta - "+cargoobj.get("estatus");
                                    }catch (Exception exc){}
                                }else{
                                    pedido.METODO_PAGO="Efectivo";
                                }

                                tvTelefonoCliente.setText(pedido.TELEFONO_CLIENTE);
                                tvMetodoPago.setText(pedido.METODO_PAGO);
                                tvNoPedido.setText(""+pedido.PK);
                                tvCliente.setText(pedido.CLIENTE);
                                tvDireccionTienda.setText(pedido.DIRECCION_TIENDA);
                                tvDireccionCliente.setText(pedido.DIRECCION);
                                tvTarifa.setText("Tarifa: $"+pedido.PRECIO_ENTREGA);
                                tvTotal.setText("Total: $"+pedido.TOTAL);

                                JSONArray productos=pedidoobj.getJSONArray("lista");
                                ProductosModel aux;
                                if(listaPedido==null){
                                    listaPedido=new ArrayList<>();
                                }
                                listaPedido.clear();
                                for (int i=0;i<productos.length();i++) {
                                    JSONObject productoite= productos.getJSONObject(i);
                                    aux=new ProductosModel();
                                    aux.PK=productoite.getString("pk");
                                    aux.PRODUCTO=productoite.getString("producto");
                                    aux.PRECIO=productoite.getDouble("precio");
                                    aux.CANTIDAD=productoite.getDouble("cantidad");
                                    aux.IMAGEN=productoite.getString("imagen");
                                    listaPedido.add(aux);
                                }
                                ViewGroup.LayoutParams layoutParams = listaView.getLayoutParams();
                                float tamaño= (listaPedido.size()*70);
                                layoutParams.height = convertDpToPixels(tamaño,getApplicationContext()); //this is in pixels
                                listaView.setLayoutParams(layoutParams);

                                productosAdapter.notifyDataSetChanged();
                            }else{

                                String error=response.getString("mensaje");
                                _ShowAlert("Error",error);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
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
    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }
    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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
                LATITUD=String.valueOf(location.getLatitude());
                LONGITUD=String.valueOf(location.getLongitude());
                actualizaUbicacion();
                //Toast.makeText(getApplicationContext(),"latitud:"+location.getLatitude()+", Longitud:"+location.getLongitude(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void llamar() {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+pedido.TELEFONO_CLIENTE));
        startActivity(i);
    }

}
