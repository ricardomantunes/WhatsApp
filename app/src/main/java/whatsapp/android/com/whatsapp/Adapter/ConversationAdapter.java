package whatsapp.android.com.whatsapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import whatsapp.android.com.whatsapp.R;
import whatsapp.android.com.whatsapp.model.Conversation;

public class ConversationAdapter extends ArrayAdapter<Conversation> {
    private ArrayList<Conversation> conversations;
    private Context context;
    private Conversation conversation;

    public ConversationAdapter(Context c, ArrayList<Conversation> objects) {
        super(c, 0, objects);
        this.context = c;
        this.conversations = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (conversations != null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_conversations, parent, false);

            TextView name = (TextView) view.findViewById(R.id.text_name);
            TextView lastConversation = (TextView) view.findViewById(R.id.text_last_conversation);

            conversation = conversations.get(position);
            name.setText(conversation.getName());
            lastConversation.setText(conversation.getMessage());
        }

        return view;
    }
}
