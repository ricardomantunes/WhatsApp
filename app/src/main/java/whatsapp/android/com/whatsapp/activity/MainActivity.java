package whatsapp.android.com.whatsapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import whatsapp.android.com.whatsapp.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnsair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        btnsair = (Button) findViewById(R.id.button3);

        btnsair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }
}
