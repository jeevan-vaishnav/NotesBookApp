package com.example.csnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class UploadFile extends AppCompatActivity {

    ImageView imagebrose,imageupload,filelogo,cancelfiles;
    Uri filepath;
    EditText filetitle;

    StorageReference storageReference;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("mydocuments");


        filetitle = (EditText) findViewById(R.id.filetileID);

        filelogo = (ImageView) findViewById(R.id.fileLogoID);
        cancelfiles = (ImageView) findViewById(R.id.cancelfileID);
        imagebrose = (ImageView)findViewById(R.id.imageBroseID);
        imageupload = (ImageView)findViewById(R.id.imageuploadID);

        filelogo.setVisibility(View.INVISIBLE);
        cancelfiles.setVisibility(View.INVISIBLE);


        cancelfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filelogo.setVisibility(View.INVISIBLE);
                cancelfiles.setVisibility(View.INVISIBLE);
                imagebrose.setVisibility(View.VISIBLE);
            }
        });


        imagebrose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setType("application/pdf");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,"Select PDF Files"),101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        }); // image method


        imageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processUpload(filepath);
            }
        });


    }// oncreate method



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 &&  resultCode == RESULT_OK){
            filepath = data.getData();
            filelogo.setVisibility(View.VISIBLE);
            cancelfiles.setVisibility(View.VISIBLE);
            imagebrose.setVisibility(View.INVISIBLE);
        }
    }

    public void processUpload(Uri filepath) {

        final ProgressDialog pd =new ProgressDialog(this);
        pd.setTitle("File Uploading...");
        pd.show();

        StorageReference reference = storageReference.child("uploads/"+System.currentTimeMillis()+".pdf");
        reference.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                model obj = new model(filetitle.getText().toString(),uri.toString());
                                databaseReference.child(databaseReference.push().getKey()).setValue(obj);
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();

                                filelogo.setVisibility(View.INVISIBLE);
                                cancelfiles.setVisibility(View.INVISIBLE);
                                imagebrose.setVisibility(View.VISIBLE);
                                filetitle.setText("");
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float percent = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                pd.setMessage("Uploaded:" +(int)percent +"%");
            }
        });

    }


}