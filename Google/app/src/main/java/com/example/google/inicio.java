package com.example.google;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class inicio extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://kwpdiqpkogexclgzuqxh.supabase.co/rest/v1/datosUsuario_register";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt3cGRpcXBrb2dleGNsZ3p1cXhoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjkxMDkyMDgsImV4cCI6MjA0NDY4NTIwOH0.jTjtI4B8TumSN8c_8L3txR9CQ-RsPA1rLBYzUdWbtpw";

    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inicio);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button aceptar = findViewById(R.id.aceptar);
        Button registrar = findViewById(R.id.registrar);

        // Botón para iniciar sesión
        aceptar.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                verificarCredenciales(email, password);
            } else {
                Toast.makeText(inicio.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para registrarse
        registrar.setOnClickListener(view -> {
            Intent i = new Intent(inicio.this, registro.class);
            startActivity(i);
        });
    }

    private void verificarCredenciales(String email, String password) {
        // URL para consultar si el correo y la contraseña coinciden
        String urlConsulta = SUPABASE_URL + "?correo=eq." + email + "&select=*";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest solicitud = new JsonArrayRequest(
                Request.Method.GET,
                urlConsulta,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                // Verificar si la contraseña coincide
                                JSONObject usuario = response.getJSONObject(0);
                                String contrasenaRegistrada = usuario.getString("contraseña");

                                if (password.equals(contrasenaRegistrada)) {
                                    // Iniciar sesión y redirigir al perfil
                                    Toast.makeText(inicio.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(inicio.this, perfil.class); // Cambiar a tu actividad de perfil
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(inicio.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(inicio.this, "El correo no está registrado", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(inicio.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SUPABASE_ERROR", "Error en la consulta: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e("SUPABASE_ERROR", "Código de estado: " + error.networkResponse.statusCode);
                        }
                        Toast.makeText(inicio.this, "Error en la consulta", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SUPABASE_API_KEY);
                headers.put("apikey", SUPABASE_API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(solicitud);
    }
}


