package whatsapp.android.com.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import whatsapp.android.com.whatsapp.Adapter.ContactAdapter;
import whatsapp.android.com.whatsapp.R;
import whatsapp.android.com.whatsapp.activity.ConversationActivity;
import whatsapp.android.com.whatsapp.activity.LoginActivity;
import whatsapp.android.com.whatsapp.activity.MenuActivity;
import whatsapp.android.com.whatsapp.helper.Preferences;
import whatsapp.android.com.whatsapp.model.Contact;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contact> contacts;

    private ValueEventListener valueEventListenerContact;

    private DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference contactsReference;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contacts = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        listView = (ListView) view.findViewById(R.id.lv_contacts);

        /*adapter = new ArrayAdapter(
            getActivity(),
                R.layout.list_contacts,
                contacts
        );*/

        adapter = new ContactAdapter(getActivity(), contacts);

        listView.setAdapter(adapter);

        Preferences preferences = new Preferences(getActivity());
        String loggedUserID = preferences.getIdentifier();

        contactsReference = firebaseDB.child("contacts").child(loggedUserID);

        valueEventListenerContact = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts.clear();

                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    Contact contact = data.getValue(Contact.class);
                    contacts.add(contact);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        contactsReference.addValueEventListener(valueEventListenerContact);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);

                Contact contact = contacts.get(position);

                intent.putExtra("name", contact.getName());
                intent.putExtra("email", contact.getEmail());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        contactsReference.addValueEventListener(valueEventListenerContact);
    }

    @Override
    public void onStop() {
        super.onStop();
        contactsReference.removeEventListener(valueEventListenerContact);
    }
}
