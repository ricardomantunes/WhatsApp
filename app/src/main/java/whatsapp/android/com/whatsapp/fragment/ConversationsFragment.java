package whatsapp.android.com.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
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

import whatsapp.android.com.whatsapp.Adapter.ConversationAdapter;
import whatsapp.android.com.whatsapp.R;
import whatsapp.android.com.whatsapp.activity.ConversationActivity;
import whatsapp.android.com.whatsapp.activity.LoginActivity;
import whatsapp.android.com.whatsapp.helper.Base64Custom;
import whatsapp.android.com.whatsapp.helper.Preferences;
import whatsapp.android.com.whatsapp.model.Contact;
import whatsapp.android.com.whatsapp.model.Conversation;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Conversation> arrayAdapter;
    private ArrayList<Conversation> conversations;

    private DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conversationsReference;
    private ValueEventListener valueEventListenerContact;

    public ConversationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        conversations = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.lv_conversations);
        arrayAdapter = new ConversationAdapter(getActivity(), conversations);
        listView.setAdapter(arrayAdapter);

        Preferences preferences = new Preferences(getActivity());
        String idLoggedUser = preferences.getIdentifier();

        conversationsReference = firebaseDB.child("conversations").child(idLoggedUser);

        valueEventListenerContact = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversations.clear();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    Conversation conversation = data.getValue(Conversation.class);
                    conversations.add(conversation);
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        conversationsReference.addValueEventListener(valueEventListenerContact);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation = conversations.get(position);

                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                String email = Base64Custom.decodeBase64(conversation.getIdUser());
                intent.putExtra("name", conversation.getName());
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        conversationsReference.addValueEventListener(valueEventListenerContact);
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationsReference.removeEventListener(valueEventListenerContact);
    }

}
