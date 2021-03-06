package com.lotustechnologicalsolution.chiri.ChatModule.ViewHolders;


import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.lotustechnologicalsolution.chiri.ChatModule.ChatAdapter;
import com.lotustechnologicalsolution.chiri.ChatModule.ChatModel;
import com.lotustechnologicalsolution.R;


public class Chat_viewholder extends RecyclerView.ViewHolder {

    public TextView message, dateTxt, messageSeen, msgDate;
    public View view;

    public Chat_viewholder(View itemView) {
        super(itemView);
        view = itemView;

        this.message = view.findViewById(R.id.msgtxt);
        this.dateTxt =view.findViewById(R.id.datetxt);
        messageSeen =view.findViewById(R.id.message_seen);
        msgDate =view.findViewById(R.id.msg_date);

    }

    public void bind(final ChatModel item,
                     final ChatAdapter.OnLongClickListener longListener) {
        message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longListener.onLongclick(item,v);
                return false;
            }
        });
    }
}

