package com.example.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

public class DealInsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int PICTURE_RESULT = 42; //The answer to everything
    EditText tvTitle;
    EditText tvDescription;
    EditText tvPrice;
    TravelDeal mDeal;
    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_insert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FirebaseUtil.openFbReference("traveldeals");

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        mImageView = findViewById(R.id.image);

        final Intent intent = getIntent();
        TravelDeal mDeal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (mDeal == null) {
            mDeal = new TravelDeal();
        }
        this.mDeal = mDeal;
        tvTitle.setText(mDeal.getTitle());
        tvDescription.setText(mDeal.getDescription());
        tvPrice.setText(mDeal.getPrice());
        showImage(mDeal.getImageUrl());
        Button btnImage = findViewById(R.id.btnImage);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent,
                        "Insert Picture"), PICTURE_RESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            //menu.findItem(R.id.insert_menu).setVisible(true);
            enableEditText(true);
            findViewById(R.id.btnImage).setEnabled(true);
        } else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
            findViewById(R.id.btnImage).setEnabled(false);
            //findViewById(R.id.btnImage).setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_menu) {
            saveDeal();
            Toast.makeText(this, "Deal saved", Toast.LENGTH_SHORT).show();
            backToList();
            clean();
            return true;
        }

        if (id == R.id.delete_menu) {
            deleteDeal();
            Toast.makeText(this, "Deal deleted", Toast.LENGTH_SHORT).show();
            backToList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //String url = taskSnapshot.getDownloadUrl.getString();
                    //String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                    String url = imageUri.toString();
                    String pictureName = taskSnapshot.getStorage().getPath();
                    mDeal.setImageUrl(url);
                    mDeal.setImageName(pictureName);
                    Log.d("Url", url);
                    Log.d("Name", pictureName);
                    showImage(url);
                }
            });
        }
    }

    private void saveDeal() {
        mDeal.setTitle(tvTitle.getText().toString());
        mDeal.setDescription(tvDescription.getText().toString());
        mDeal.setPrice(tvPrice.getText().toString());

        //Updates the values
        if (mDeal.getId() == null) {
            mDatabaseReference.push().setValue(mDeal);
        } else {
            mDatabaseReference.child(mDeal.getId()).setValue(mDeal);
        }
    }

    private void deleteDeal() {
        if (mDeal == null) {
            Toast.makeText(this, "Save deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(mDeal.getId()).removeValue();
        if (mDeal.getImageName() != null && mDeal.getImageName().isEmpty() == false){
            StorageReference picref = FirebaseUtil.mStorage.getReference().child(mDeal.getImageName());
            picref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete image: ", "Image deleted successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete image: ", e.getMessage());
                }
            });
        }
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        DealInsertActivity.this.finish();
    }

    private void clean() {
        tvTitle.setText("");
        tvDescription.setText("");
        tvPrice.setText("");
        tvTitle.requestFocus();
    }

    private void enableEditText(boolean isEnabled) {
        tvTitle.setEnabled(isEnabled);
        tvDescription.setEnabled(isEnabled);
        tvPrice.setEnabled(isEnabled);
    }

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(mImageView);
        }
    }
}