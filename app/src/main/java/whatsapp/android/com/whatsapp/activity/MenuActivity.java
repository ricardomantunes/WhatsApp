package whatsapp.android.com.whatsapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import whatsapp.android.com.whatsapp.Adapter.TabAdapter;
import whatsapp.android.com.whatsapp.R;
import whatsapp.android.com.whatsapp.helper.Base64Custom;
import whatsapp.android.com.whatsapp.helper.Preferences;
import whatsapp.android.com.whatsapp.helper.SlidingTabLayout;
import whatsapp.android.com.whatsapp.model.Contact;
import whatsapp.android.com.whatsapp.model.User;

public class MenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference();
    private Toolbar toolbar;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_page);

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccente));

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.item_logout:
                logOutUser();
                return true;
            case R.id.item_add:
                openAddContacts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openAddContacts()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("New Contact");
        alertDialog.setMessage("User email to add");
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(this);
        alertDialog.setView(editText);

        alertDialog.setPositiveButton("Add Contact", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String emailContact = editText.getText().toString();

                if (emailContact.isEmpty())
                {
                    Toast.makeText(MenuActivity.this, "Insert the email", Toast.LENGTH_LONG).show();
                }
                else
                {
                    final String idContact = Base64Custom.convertBase64(emailContact);

                    DatabaseReference contactReference = firebaseDB.child("users").child(idContact);

                    contactReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null)
                            {
                                User userContact = dataSnapshot.getValue(User.class);

                                Preferences preferences = new Preferences(MenuActivity.this);
                                String loggedUseerID = preferences.getIdentifier();

                                Contact contact = new Contact();
                                contact.setUserID(idContact);
                                contact.setEmail(userContact.getEmail());
                                contact.setName(userContact.getName());

                                DatabaseReference newContactReference = firebaseDB.child("contacts")
                                                                            .child(loggedUseerID)
                                                                            .child(idContact);

                                newContactReference.setValue(contact);
                            }
                            else
                            {
                                Toast.makeText(MenuActivity.this, "User not found", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //User contact = (User)firebaseDB.child("users").child(idContact);
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    private void logOutUser()
    {
        mAuth.signOut();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
