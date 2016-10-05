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
import whatsapp.android.com.whatsapp.helper.Preferences;
import whatsapp.android.com.whatsapp.model.Message;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private ArrayList<Message> messages;

    public MessageAdapter(Context c, ArrayList<Message> objects) {
        super(c, 0, objects);
        this.context = c;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (messages != null)
        {
            Message message = messages.get(position);

            Preferences preferences = new Preferences(context);
            String idLoggedUser = preferences.getIdentifier();

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            if(idLoggedUser.equals(message.getUserID()))
            {
                view = inflater.inflate(R.layout.item_message_right, parent, false);
            }
            else
            {
                view = inflater.inflate(R.layout.item_message_left, parent, false);
            }

            TextView textView = (TextView) view.findViewById(R.id.tv_message);
            textView.setText(message.getMessage());
        }

        return view;
    }
}
