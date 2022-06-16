package com.example.frontend;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<Message> {

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
            row = inflater.inflate(R.layout.left, parent, false);
        }else{
            row = inflater.inflate(R.layout.right, parent, false);
        }
        if (chatMessageObj.type.equals("string")){
            chatText = (TextView) row.findViewById(R.id.msgr);
            chatText.setText(chatMessageObj.message);
            chatText.getLayoutParams().height = 110;
        }
        else if (chatMessageObj.type.equals("mp4")){
            VideoView videoView = (VideoView) row.findViewById(R.id.msgr_video);

            MediaController mediaController = new MediaController(this.context);

            videoView.setVideoURI(Uri.parse(chatMessageObj.message));
            videoView.setMediaController(mediaController);
            videoView.getLayoutParams().height = 200;

            videoView.start();
        }
        else if (chatMessageObj.type.equals("png") || chatMessageObj.type.equals("jpg")){
            ImageView imageView = (ImageView) row.findViewById(R.id.msgr_img);
            imageView.getLayoutParams().height = 250;
            imageView.setImageURI(Uri.parse(chatMessageObj.message));

        }


        return row;
    }
}
