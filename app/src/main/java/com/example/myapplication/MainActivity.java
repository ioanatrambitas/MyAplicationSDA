package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.textclassifier.ConversationAction;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Logare;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText vNume,vEmail,vParola,vTelefon;
    Button vButonInreg;
    TextView vButonLog;
    FirebaseAuth fAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   //     setContentView(R.layout.layout.activity_main);


        vNume = findViewById(R.id.nume);
        vEmail = findViewById(R.id.email);
        vParola = findViewById(R.id.parola);
        vTelefon = findViewById(R.id.telefon);
        vButonInreg = findViewById(R.id.buttonReg);
        vButonLog = findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();

        vButonInreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail = vEmail.getText().toString().trim();
                String sParola = vParola.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);


                //Conditi pentru email si parola
                if (TextUtils.isEmpty(sEmail)) {
                    vEmail.setError("Lipsa email");
                    return;
                }
                if (TextUtils.isEmpty(sParola)) {
                    vParola.setError("Lipsa parola");
                    return;
                }
                if (sParola.length() < 6) {
                    vParola.setError("Parola trebuie sa aiba minim 6 caractere");
                    return;
                }

                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), Logare.class));
                    finish();
                }


//Inregistrarea utilizatorului in baza de date

                fAuth.createUserWithEmailAndPassword(sEmail, sParola).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful()) {
                            Exception exception = task.getException();
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Exception exception = task.getException();
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();


                        }
                    }
                });
                startActivity(new Intent(getApplicationContext(), Logare.class));

            }
        });



        vButonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Logare.class));
            }
        });

    }



}
