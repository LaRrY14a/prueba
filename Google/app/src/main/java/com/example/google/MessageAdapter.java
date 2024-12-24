package com.example.google;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> messageList;

    public MessageAdapter(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Asignación de los valores a los TextViews
        holder.senderName.setText(message.getSender()); // Nombre del remitente
        holder.message.setText(message.getMessage()); // Contenido del mensaje
        holder.time.setText(message.getTimestamp()); // Hora o timestamp del mensaje

        // Lógica para el avatar: mostrar la primera letra del nombre del remitente
        holder.avatarText.setText(String.valueOf(message.getSender().charAt(0)).toUpperCase());

        // Ajustar el color del texto según si el mensaje ha sido leído
        int textColor = message.isRead() ? android.R.color.darker_gray : android.R.color.black;
        holder.senderName.setTextColor(holder.itemView.getContext().getResources().getColor(textColor));
        holder.message.setTextColor(holder.itemView.getContext().getResources().getColor(textColor));
    }

    @Override
    public int getItemCount() {
        return messageList.size();  // Regresa el tamaño de la lista de mensajes
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, message, time, avatarText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            avatarText = itemView.findViewById(R.id.avatarText);
        }
    }
}
