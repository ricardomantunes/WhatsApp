package whatsapp.android.com.whatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import whatsapp.android.com.whatsapp.R;
import whatsapp.android.com.whatsapp.model.Contact;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private ArrayList<Contact> contacts;

    public ContactAdapter(Context c, ArrayList<Contact> objects) {
        super(c, 0, objects);

        this.context = c;
        this.contacts = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;

        if (contacts != null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_contacts, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.tv_name);

            Contact contact = contacts.get(position);
            textView.setText(contact.getName());
        }

        return view;
    }
}
