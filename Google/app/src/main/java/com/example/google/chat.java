package com.example.google;

import java.net.URLEncoder;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class chat extends AppCompatActivity {

    private MessageAdapter adapter;
    private ArrayList<Message> messages;
    private RealtimeChatClient realtimeClient;
    private String senderName; // El nombre del emisor
    private String receiverName; // El nombre del receptor
    private String senderId; // El ID único del emisor
    private String receiverId; // El ID único del receptor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        EditText messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Obtener los datos reales de los usuarios
        fetchRealUserNames();

        // Inicializar el cliente en tiempo real
        realtimeClient = new RealtimeChatClient(messages, adapter, recyclerView);
        realtimeClient.connect("https://kwpdiqpkogexclgzuqxh.supabase.co/rest/v1/messages",
                "tu_api_key",
                new RealtimeChatClient.MessageListener() {
                    @Override
                    public void onNewMessage(String sender, String receiver, String message, String timestamp) {
                        if ((sender.equals(senderName) && receiver.equals(receiverName)) ||
                                (sender.equals(receiverName) && receiver.equals(senderName))) {

                            messages.add(new Message(sender, message, timestamp, "received", false, "No Asunto"));
                            runOnUiThread(() -> {
                                adapter.notifyItemInserted(messages.size() - 1);
                                recyclerView.scrollToPosition(messages.size() - 1);
                            });
                        }
                    }
                });

        // Cargar los mensajes existentes entre los usuarios reales
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessageToSupabase(senderName, receiverName, messageText);
                messageInput.setText("");
            }
        });
    }

    // Función para obtener los nombres de los usuarios reales de la base de datos
    public void fetchRealUserNames() {
        String url = "https://kwpdiqpkogexclgzuqxh.supabase.co/rest/v1/datosUsuario_register?select=nombre,id";

        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt3cGRpcXBrb2dleGNsZ3p1cXhoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjkxMDkyMDgsImV4cCI6MjA0NDY4NTIwOH0.jTjtI4B8TumSN8c_8L3txR9CQ-RsPA1rLBYzUdWbtpw"; // Usa tu propia API key

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            // Asignar el nombre e ID del primer usuario como emisor
                            JSONObject firstUser = jsonArray.getJSONObject(0);
                            senderName = firstUser.getString("nombre"); // Nombre del emisor
                            senderId = firstUser.getString("id"); // ID del emisor

                            // Asignar el nombre e ID del segundo usuario como receptor
                            if (jsonArray.length() > 1) {
                                JSONObject secondUser = jsonArray.getJSONObject(1);
                                receiverName = secondUser.getString("nombre"); // Nombre del receptor
                                receiverId = secondUser.getString("id"); // ID del receptor
                            }

                            // Actualizar la UI con los nombres y cargar mensajes
                            updateChatWithRealUsers(senderName, receiverName);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("FetchUserNames", "Error al obtener los nombres de usuario", error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", apiKey);
                headers.put("Authorization", "Bearer " + apiKey);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    // Actualiza el chat con los nombres reales
    public void updateChatWithRealUsers(String sender, String receiver) {
        // Cambia el texto del botón de envío
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setText("Enviar mensaje a " + receiver); // Actualizar texto del botón

        // Cargar los mensajes con los usuarios reales
        fetchMessages(sender, receiver);
    }

    // Función para cargar los mensajes entre dos usuarios específicos
    public void fetchMessages(String user, String contact) {
        try {
            String url = "https://kwpdiqpkogexclgzuqxh.supabase.co/rest/v1/messages?or=(sender.eq."
                    + URLEncoder.encode(user, "UTF-8") + ",receiver.eq." + URLEncoder.encode(contact, "UTF-8")
                    + ")&order=timestamp.asc";

            String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt3cGRpcXBrb2dleGNsZ3p1cXhoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjkxMDkyMDgsImV4cCI6MjA0NDY4NTIwOH0.jTjtI4B8TumSN8c_8L3txR9CQ-RsPA1rLBYzUdWbtpw";

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            messages.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonMessage = jsonArray.getJSONObject(i);
                                String sender = jsonMessage.getString("sender");
                                String message = jsonMessage.getString("message");
                                String timestamp = jsonMessage.getString("timestamp");

                                messages.add(new Message(sender, message, timestamp, "received", false, "No Asunto"));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    Throwable::printStackTrace) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("apikey", apiKey);
                    headers.put("Authorization", "Bearer " + apiKey);
                    return headers;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Función para enviar mensajes a la base de datos
    public void sendMessageToSupabase(String sender, String receiver, String message) {
        String url = "https://kwpdiqpkogexclgzuqxh.supabase.co/rest/v1/messages";
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt3cGRpcXBrb2dleGNsZ3p1cXhoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjkxMDkyMDgsImV4cCI6MjA0NDY4NTIwOH0.jTjtI4B8TumSN8c_8L3txR9CQ-RsPA1rLBYzUdWbtpw";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("sender", sender);
            jsonBody.put("receiver", receiver);
            jsonBody.put("message", message);

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        String timestamp = "Ahora"; // Ajusta si tienes un timestamp del servidor
                        messages.add(new Message(sender, message, timestamp, "sent", false, "No Asunto"));
                        adapter.notifyItemInserted(messages.size() - 1);
                    },
                    Throwable::printStackTrace) {
                @Override
                public byte[] getBody() {
                    return jsonBody.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("apikey", apiKey);
                    headers.put("Authorization", "Bearer " + apiKey);
                    return headers;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realtimeClient.close();
    }
}
