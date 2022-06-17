package com.example.frontend;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OnlineUsersArrayAdapter extends ArrayAdapter<String> {

    private List<String> userList = new ArrayList<>();
    private Context context;
    private TextView userText;

    public OnlineUsersArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    @Override
    public void add(String user) {
        userList.add(user);
        super.add(user);
    }

    public boolean hasUser(String user){
        return userList.contains(user);
    }

    public String getItem(int index) {
        return this.userList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String username = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        row = inflater.inflate(R.layout.users, parent, false);
        userText = (TextView) row.findViewById(R.id.user1);
        userText.setText(username);
        row.setEnabled(false);

        return row;
    }
}
