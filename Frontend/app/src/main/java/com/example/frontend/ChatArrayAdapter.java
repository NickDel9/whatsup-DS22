package com.example.frontend;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<Message>  {

    private TextView chatText;
    private List<Message> chatMessageList = new ArrayList<Message>();
    private Context context;

    @Override
    public void add(Message object) {
        chatMessageList.add(object);
        super.add(object);
    }




    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public Message getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Message chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (chatMessageObj.left) {
            if (chatMessageObj.type.equals("mp4"))
                row = inflater.inflate(R.layout.left_img, parent, false);

            else if (chatMessageObj.type.equals("png") || chatMessageObj.type.equals("jpg"))
                row = inflater.inflate(R.layout.left_img, parent, false);

            else
                row = inflater.inflate(R.layout.left, parent, false);
        }else{
            if (chatMessageObj.type.equals("mp4"))
                row = inflater.inflate(R.layout.right_img, parent, false);

            else if (chatMessageObj.type.equals("png") || chatMessageObj.type.equals("jpg"))
                row = inflater.inflate(R.layout.right_img, parent, false);

            else
                row = inflater.inflate(R.layout.right, parent, false);

        }

        if (chatMessageObj.type.equals("string")){
            chatText = (TextView) row.findViewById(R.id.msgr);
            chatText.setText(chatMessageObj.message);
            chatText.getLayoutParams().height = 110;

        }
        else if (chatMessageObj.type.equals("mp4")){

            ImageView imageView = (ImageView) row.findViewById(R.id.msgr_img);
            imageView.getLayoutParams().height = 250;
            imageView.setImageResource(R.drawable.ic_baseline_ondemand_video_24);

        }
        else if (chatMessageObj.type.equals("png") || chatMessageObj.type.equals("jpg")){

            ImageView imageView = (ImageView) row.findViewById(R.id.msgr_img);
            imageView.getLayoutParams().height = 250;
            imageView.setImageURI(Uri.fromFile(new File(chatMessageObj.message)));


        }



        return row;
    }
}
