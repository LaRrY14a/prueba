package com.example.google;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter; // Ahora el tipo es UserAdapter, asegúrate de que esta clase esté definida
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_user); // Asegúrate de que este sea el layout correcto

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el ArrayList de usuarios
        users = new ArrayList<>();

        // Llamamos a la función que obtiene los usuarios de la base de datos
        fetchUsers();

        // Establecer el adaptador
        adapter = new UserAdapter(users, user -> {
            // Cuando un usuario es seleccionado, lanzamos el chat
            Intent intent = new Intent(UserSelectionActivity.this, chat.class);
            intent.putExtra("receiverName", user.getName());
            intent.putExtra("receiverId", user.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    // Función para obtener los usuarios de la base de datos
    private void fetchUsers() {
        String url = "https://kwpdiqpkogexclgzuqxh.supabase.co/rest/v1/datosUsuario_register?select=nombre,id";
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt3cGRpcXBrb2dleGNsZ3p1cXhoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjkxMDkyMDgsImV4cCI6MjA0NDY4NTIwOH0.jTjtI4B8TumSN8c_8L3txR9CQ-RsPA1rLBYzUdWbtpw";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonUser = jsonArray.getJSONObject(i);
                            String name = jsonUser.getString("nombre");
                            String id = jsonUser.getString("id");

                            users.add(new User(name, id)); // Añadimos el nuevo usuario a la lista
                        }
                        adapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("UserSelection", "Error al obtener los usuarios", error)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", apiKey);
                headers.put("Authorization", "Bearer " + apiKey);
                return headers;
            }
        };

        // Aquí debe ir el código para agregar la solicitud a la cola de Volley (asegúrate de tener la cola configurada correctamente)
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

}

