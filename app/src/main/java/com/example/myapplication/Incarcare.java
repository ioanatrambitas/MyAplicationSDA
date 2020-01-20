package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Incarcare extends AppCompatActivity {

    Button selectFile, upload;
    TextView notification;
    Uri pdfUri; //adresa pdf

    FirebaseStorage storage; //pentru incarcare fisier
    FirebaseDatabase database; // pentru memorare adresa fisier
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incarcare);


        storage = FirebaseStorage.getInstance(); //returneaza un obiect Firebase Storage
        database = FirebaseDatabase.getInstance(); // returneaza un obiect Firebase Databse

        selectFile = findViewById(R.id.selectFile);
        upload = findViewById(R.id.upload);
        notification = findViewById(R.id.notification);

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Incarcare.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectPdf();
                }
                else
                {
                    ActivityCompat.requestPermissions(Incarcare.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfUri!=null)
                    uploadFile(pdfUri);
                else
                    Toast.makeText(Incarcare.this, "Selecteaza fisier", Toast.LENGTH_SHORT).show();
            }

            private void uploadFile(Uri pdfUri) {
                progressDialog = new ProgressDialog(Incarcare.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("Incarcare fisier...");
                progressDialog.show();

                final String fileName =System.currentTimeMillis()+"";
                //    StorageReference reference =  storage.child("uploads/"+System.currentTimeMillis()+".pdf");

                //      StorageReference storageReference = storage.getRFeference();
                final StorageReference storageReference = storage.getReference();

                storageReference.child("upload").child(fileName).putFile(pdfUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(Incarcare.this, "Se verifica", Toast.LENGTH_SHORT).show();
                                String url =taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                                //      Task<Uri> url =taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                DatabaseReference reference = database.getReference();

                               // Map<String,String> doc = new HashMap<>();
                              //  doc.put("url", url);
                                reference.child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                            Toast.makeText(Incarcare.this, "Fisier incarcat cu succes", Toast.LENGTH_SHORT).show();
                                        else

                                            Toast.makeText(Incarcare.this, "Eroare incarcare fisier1", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                startActivity(new Intent(getApplicationContext(),Incarcare.class));

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Incarcare.this, "Eroare incarcare fisier2", Toast.LENGTH_SHORT).show();
                        e.getMessage();
                        e.printStackTrace();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                        int currentProgress= (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setProgress(currentProgress);

                    }
                });

            }

        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED)

            selectPdf();

        else

            Toast.makeText(Incarcare.this, "Va rugam verificati permisiunile...", Toast.LENGTH_SHORT).show();
    }


    private void selectPdf()
    {
        //ofera selectarea fisierului utilizand file manager


        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //verifica daca utilizatorul a selectat sau nu un fisier
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();  //returneaza adresa fisier selectat
            notification.setText("Un fisier selectat: " + data.getData().getLastPathSegment());
        } else {
            Toast.makeText(this, "Va rugam selectati fisierul", Toast.LENGTH_SHORT).show();
        }


    }
}
