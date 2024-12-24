package com.example.google;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;  // Asegúrate de importar RecyclerView
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONObject;
import androidx.recyclerview.widget.RecyclerView;


import java.util.concurrent.TimeUnit;
import java.util.List;

public class RealtimeChatClient {

    private WebSocket webSocket;
    private MessageListener listener;

    // Interfaz para manejar mensajes nuevos
    public interface MessageListener {
        void onNewMessage(String sender, String receiver, String message, String timestamp);
    }

    private List<Message> messages;  // Tu lista de mensajes
    private MessageAdapter adapter;  // El adaptador del RecyclerView
    private RecyclerView recyclerView;  // El RecyclerView

    public RealtimeChatClient(List<Message> messages, MessageAdapter adapter, RecyclerView recyclerView) {
        this.messages = messages;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }

    public void connect(String supabaseUrl, String supabaseKey, MessageListener messageListener) {
        this.listener = messageListener;

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // Mantener la conexión abierta indefinidamente
                .build();

        Request request = new Request.Builder()
                .url(supabaseUrl + "/realtime/v1")
                .addHeader("apikey", supabaseKey)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                System.out.println("Conexión en tiempo real establecida.");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("RealtimeChat", "Mensaje recibido: " + text);
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    String event = jsonObject.getString("event");
                    if ("INSERT".equals(event)) {
                        JSONObject newRecord = jsonObject.getJSONObject("new");
                        String sender = newRecord.getString("sender");
                        String receiver = newRecord.getString("receiver");
                        String message = newRecord.getString("message");
                        String timestamp = newRecord.getString("timestamp");

                        // Llamar al listener para manejar el nuevo mensaje
                        if (listener != null) {
                            listener.onNewMessage(sender, receiver, message, timestamp);
                        }
                    }
                } catch (Exception e) {
                    Log.e("RealtimeChat", "Error al procesar el mensaje: " + text, e);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e("RealtimeChat", "Error en la conexión en tiempo real.", t);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d("RealtimeChat", "Conexión en tiempo real cerrada: " + reason);
            }
        });
    }

    // Método para cerrar la conexión
    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Cerrado por el cliente");
        }
    }

    // Implementación de onNewMessage para agregar el mensaje al RecyclerView
    public void onNewMessage(String sender, String receiver, String message, String timestamp) {
        Log.d("RealtimeChat", "Nuevo mensaje recibido: " + sender + ": " + message);

        // Filtrar mensajes para esta conversación
        if ((sender.equals("Yo") && receiver.equals("OtroUsuario")) ||
                (sender.equals("OtroUsuario") && receiver.equals("Yo"))) {

            messages.add(new Message(sender, message, timestamp, "received", false, "No Asunto"));

            // Actualizar el RecyclerView en el hilo principal
            recyclerView.post(() -> {
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);
            });
        }
    }
}
