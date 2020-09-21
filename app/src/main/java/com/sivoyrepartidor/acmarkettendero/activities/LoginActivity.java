package com.sivoyrepartidor.acmarkettendero.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sivoyrepartidor.acmarkettendero.R;
import com.sivoyrepartidor.acmarkettendero.model.TenderoModel;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etUsuario,etPassword;
    String user,pass;
    Button btnLogin;
    ProgressBar progressBar;
    String URL_API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsuario=findViewById(R.id.etUsuario);
        etPassword=findViewById(R.id.etPasswordLogin);
        btnLogin=findViewById(R.id.btnIngresarLogin);
        URL_API =getString(R.string.URL_HOST)+"LoginRepartidor";
        pedirPermisos();
    }

    public void iniciarSesion(View view){
        user=etUsuario.getText().toString();
        pass=etPassword.getText().toString();
        login();
    }

    public void login() {

        if (!validate()) {
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Iniciando sesion...");
        progressDialog.show();

        JSONObject datos = new JSONObject();
        try {
            datos.put("TELEFONO", user);
            datos.put("PASSWORD",pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progressDialog.dismiss();
                            btnLogin.setEnabled(true);
                            int result = (int) response.get("resultado");

                            if(result == 1){
                                JSONObject ten=response.getJSONObject("repartidor");
                                TenderoModel tendero=new TenderoModel();
                                tendero.PK=ten.getString("pk");
                                tendero.NOMBRE=ten.getString("nombre");
                                tendero.APATERNO=ten.getString("apaterno");
                                tendero.AMATERNO=ten.getString("amaterno");
                                tendero.ESTADO=ten.getString("estado");
                                tendero.MUNICIPIO=ten.getString("municipio");
                                tendero.COLONIA=ten.getString("colonia");
                                tendero.CALLE=ten.getString("calle");
                                tendero.NUMERO=ten.getString("numero");
                                tendero.TELEFONO=ten.getString("telefono");
                                tendero.CORREO=ten.getString("correo");
                                tendero.IMAGEN=ten.getString("imagen");
                                tendero.BASICO=ten.getString("basico");
                                tendero.URGENTE=ten.getString("urgente");
                                tendero.FOLIO=ten.getString("folio");
                                tendero.PASSWORD=ten.getString("password");
                                tendero.FECHA_C=ten.getString("fechA_C");
                                tendero.FECHA_M=ten.getString("fechA_M");

                                SharedPreferences preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferencias.edit();
                                editor.putString("PK",tendero.PK);
                                editor.putString("NOMBRE",tendero.NOMBRE);
                                editor.putString("AMATERNO",tendero.AMATERNO);
                                editor.putString("APATERNO",tendero.APATERNO);
                                editor.putString("ESTADO",tendero.ESTADO);
                                editor.putString("MUNICIPIO",tendero.MUNICIPIO);
                                editor.putString("COLONIA",tendero.COLONIA);
                                editor.putString("CALLE",tendero.CALLE);
                                editor.putString("NUMERO",tendero.NUMERO);
                                editor.putString("TELEFONO",tendero.TELEFONO);
                                editor.putString("IMAGEN",tendero.IMAGEN);
                                editor.putString("URGENTE",tendero.URGENTE);
                                editor.putString("FOLIO",tendero.FOLIO);
                                editor.putString("PASSWORD",tendero.PASSWORD);
                                editor.putString("FECHA_C",tendero.FECHA_C);
                                editor.putString("FECHA_M",tendero.FECHA_M);
                                editor.putBoolean("LOGUEADO",true);

                                editor.commit();
                                Intent main=new Intent(getApplicationContext(), MainActivity.class);
                                //TODO_SERGIO Intent main=new Intent(getApplicationContext(), TiendasActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(main);
                                finish();

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
                        btnLogin.setEnabled(true);
                        _ShowAlert("Error","¡Ocurrio un error intente màs tarde!");
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

    public boolean validate(){
        if(user.isEmpty()){
            _ShowAlert("Error","¡Telefono no puede estar vacío!");
            return false;
        }else if(pass.isEmpty()){
            _ShowAlert("Error","¡Contraseña no puede estar vacío!");
            return false;
        }

        return true;
    }

    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
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

    public void btnCreaCuenta(View view){
        //Intent intento = new Intent(getApplicationContext(),Registro1Activity.class);
        //startActivity(intento);
    }

    int REQUEST_LOCATION=1;
    public void pedirPermisos(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE},
                    REQUEST_LOCATION);
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Por favor es necesario aceptar los permisos para usar Polar", Toast.LENGTH_LONG).show();
            }
        }
    }

}

