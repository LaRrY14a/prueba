package com.example.google;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.google.User;


import java.util.ArrayList;

// Esta clase adapta los datos de los usuarios para el RecyclerView
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users;
    private OnUserClickListener onUserClickListener;

    public UserAdapter(ArrayList<User> users, OnUserClickListener onUserClickListener) {
        this.users = users;
        this.onUserClickListener = onUserClickListener;
    }

    // Método para inflar el layout del item y crear el ViewHolder
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    // Método para bindear los datos de un usuario con el ViewHolder
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameTextView.setText(user.getName());
        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(user));  // Accion al hacer clic
    }

    // Método que devuelve el número de elementos
    @Override
    public int getItemCount() {
        return users.size();
    }

    // Clase ViewHolder para manejar cada item
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewUserName); // Asegúrate de que este ID existe en tu layout
        }
    }

    // Interfaz para manejar el clic en el usuario
    public interface OnUserClickListener {
        void onUserClick(User user);
    }
}

