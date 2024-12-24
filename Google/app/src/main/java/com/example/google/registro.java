package com.example.google;

import android.content.Intent; // Importar la clase Intent
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

public class registro extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://kwpdiqpkogexclgzuqxh.supabase.co/rest/v1/datosUsuario_register";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt3cGRpcXBrb2dleGNsZ3p1cXhoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjkxMDkyMDgsImV4cCI6MjA0NDY4NTIwOH0.jTjtI4B8TumSN8c_8L3txR9CQ-RsPA1rLBYzUdWbtpw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registro);

        // Asociar los campos de entrada
        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etApellidos = findViewById(R.id.etApellidos);
        EditText etContraseña = findViewById(R.id.etContraseña);

        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos ingresados
                String correo = etCorreo.getText().toString().trim();
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellidos.getText().toString().trim();
                String contraseña = etContraseña.getText().toString().trim();

                // Validar que todos los campos estén llenos
                if (!correo.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty() && !contraseña.isEmpty()) {
                    // Enviar los datos
                    enviarDatos(correo, nombre, apellido, contraseña);
                } else {
                    // Mostrar mensaje de error
                    Toast.makeText(registro.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void enviarDatos(String correo, String nombre, String apellido, String contraseña) {
        // Crear el objeto JSON con los datos a enviar
        JSONObject datosUsuario_register = new JSONObject();
        try {
            datosUsuario_register.put("correo", correo);
            datosUsuario_register.put("nombre", nombre);
            datosUsuario_register.put("apellido", apellido);
            datosUsuario_register.put("contraseña", contraseña);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SUPABASE_API_KEY);
        headers.put("apikey", SUPABASE_API_KEY);
        headers.put("Content-Type", "application/json");

        // Crear la solicitud POST
        JsonObjectRequest solicitud = new JsonObjectRequest(
                Request.Method.POST,
                SUPABASE_URL,
                datosUsuario_register,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log para ver la respuesta
                        Log.d("SUPABASE", "Respuesta: " + response.toString());

                        // Mostrar mensaje de éxito
                        Toast.makeText(registro.this, "Error en el envío de datos", Toast.LENGTH_SHORT).show();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("SUPABASE_ERROR", "Error en el envío de datos: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e("SUPABASE_ERROR", "Código de error: " + error.networkResponse.statusCode);
                            Log.e("SUPABASE_ERROR", "Respuesta del servidor: " + new String(error.networkResponse.data));
                        }

                        // Mostrar mensaje de error
                        Toast.makeText(registro.this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(registro.this, inicio.class);
                        startActivity(intent);
                        finish();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(solicitud);
    }
}
