package com.javasampleapproach.firebase.crudfirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText mTitle, mDesc;
    private Button mSaveBtn, mShowBtn;
    private FirebaseFirestore db;
    private String uTitle, uDesc, uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = findViewById(R.id.edit_title);
        mDesc = findViewById(R.id.edit_desc);
        mSaveBtn = findViewById(R.id.save_btn);
        mShowBtn = findViewById(R.id.showall_btn);

        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mSaveBtn.setText("Update");
            uTitle = bundle.getString("uTitle");
            uId = bundle.getString("uId");
            uDesc = bundle.getString("uDesc");
            mTitle.setText(uTitle);
            mDesc.setText(uDesc);
        }else{
            mSaveBtn.setText("Save");
        }

        mShowBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowActivity.class));
            }
        });


        mSaveBtn.setOnClickListener(new View.OnClickListener(){
            // ctrl+o / alt+insert => to create override
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String desc = mDesc.getText().toString();

                Bundle bundle1 = getIntent().getExtras();
                if (bundle1 != null){
                    String id = uId;
                    updateToFireStore(id, title, desc);
                }else{
                    String id = UUID.randomUUID().toString();
                    saveToFireStore(id, title, desc);
                }

            }
        });
    }

    private void updateToFireStore(String id, String title, String desc){

        db.collection("Documents").document(id).update("title", title, "desc", desc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Data Update!!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Error : " + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFireStore(String id, String title, String desc){

        // data yang di query
        if (!title.isEmpty() && !desc.isEmpty()){
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("title", title);
            map.put("desc", desc);

            // mengirim data
            db.collection("Documents").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Data Save !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Empty Fields not Allowed", Toast.LENGTH_SHORT).show();
        }

    }
}