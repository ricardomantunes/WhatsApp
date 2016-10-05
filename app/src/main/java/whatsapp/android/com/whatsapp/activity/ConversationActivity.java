package whatsapp.android.com.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import whatsapp.android.com.whatsapp.Adapter.MessageAdapter;
import whatsapp.android.com.whatsapp.R;
import whatsapp.android.com.whatsapp.helper.Base64Custom;
import whatsapp.android.com.whatsapp.helper.Preferences;
import whatsapp.android.com.whatsapp.model.Conversation;
import whatsapp.android.com.whatsapp.model.Message;

public class ConversationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMessage;
    private ImageButton btMessage;
    private ListView listView;
    private ArrayAdapter<Message> arrayAdapter;
    private ArrayList<Message> messages;
    private ValueEventListener valueEventListenerMessages;
    private Conversation conversation;

    private String nameUserToSend;
    private String idUserToSend;

    private String idLoggedUser;
    private String nameLoggedUser;

    private DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference messagesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        toolbar = (Toolbar) findViewById(R.id.tb_conversation);
        editMessage = (EditText) findViewById(R.id.edit_message);
        btMessage = (ImageButton) findViewById(R.id.bt_send);
        listView = (ListView) findViewById(R.id.lv_messages);

        Preferences preferences = new Preferences(ConversationActivity.this);
        idLoggedUser = preferences.getIdentifier();
        nameLoggedUser = preferences.getName();

        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            nameUserToSend = extra.getString("name");
            idUserToSend = Base64Custom.convertBase64(extra.getString("email"));
        }

        toolbar.setTitle(nameUserToSend);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        messages = new ArrayList<>();
        /*arrayAdapter = new ArrayAdapter<String>(
                ConversationActivity.this,
                android.R.layout.simple_list_item_1,
                messages
        );*/

        arrayAdapter = new MessageAdapter(ConversationActivity.this, messages);

        listView.setAdapter(arrayAdapter);

        messagesReference = firebaseDB.child("messages").child(idLoggedUser).child(idUserToSend);

        valueEventListenerMessages = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();

                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    Message message = data.getValue(Message.class);

                    messages.add(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        messagesReference.addValueEventListener(valueEventListenerMessages);

        btMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editMessage.getText().toString();

                if(messageText.isEmpty()) {
                    Toast.makeText(ConversationActivity.this, "Insert a message to send", Toast.LENGTH_LONG).show();
                }
                else{
                    Message message = new Message();
                    message.setMessage(messageText);
                    message.setUserID(idLoggedUser);

                    Boolean returnFrom = saveFirebaseMessage(idLoggedUser, idUserToSend, message);
                    if (!returnFrom)
                    {
                        Toast.makeText(ConversationActivity.this,
                                "Problem sending message. Please try again",
                                Toast.LENGTH_LONG).show();
                    }

                    Boolean returnTo = saveFirebaseMessage(idUserToSend, idLoggedUser, message);
                    if (!returnTo)
                    {
                        Toast.makeText(ConversationActivity.this,
                                "Problem sending message. Please try again",
                                Toast.LENGTH_LONG).show();
                    }

                    conversation = new Conversation();
                    conversation.setIdUser(idUserToSend);
                    conversation.setName(nameUserToSend);
                    conversation.setMessage(messageText);

                    Boolean returnFromConv = saveFirebaseConversation(idLoggedUser, idUserToSend, conversation);
                    if (!returnFromConv)
                    {
                        Toast.makeText(ConversationActivity.this,
                                "Problem saving conversation. Please try again",
                                Toast.LENGTH_LONG).show();
                    }

                    conversation = new Conversation();
                    conversation.setIdUser(idLoggedUser);
                    conversation.setName(nameLoggedUser);
                    conversation.setMessage(messageText);

                    Boolean returnToConv = saveFirebaseConversation(idUserToSend, idLoggedUser, conversation);
                    if (!returnToConv)
                    {
                        Toast.makeText(ConversationActivity.this,
                                "Problem saving conversation. Please try again",
                                Toast.LENGTH_LONG).show();
                    }

                    editMessage.setText("");
                }
            }
        });
    }

    private boolean saveFirebaseMessage(String idFrom, String idTo, Message message)
    {
        try {
            DatabaseReference newMessageReference = firebaseDB.child("messages")
                    .child(idFrom)
                    .child(idTo)
                    .push();

            newMessageReference.setValue(message);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveFirebaseConversation(String idFrom, String idTo, Conversation conversation)
    {
        try {
            DatabaseReference newMessageReference = firebaseDB.child("conversations")
                    .child(idFrom)
                    .child(idTo);

            newMessageReference.setValue(conversation);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onRestart() {
        super.onStart();
        messagesReference.addValueEventListener(valueEventListenerMessages);
    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesReference.removeEventListener(valueEventListenerMessages);
    }
}
