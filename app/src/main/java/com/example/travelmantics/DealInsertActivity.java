package com.example.travelmantics;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class DealInsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText tvTitle;
    EditText tvDescription;
    EditText tvPrice;
    TravelDeal mDeal;

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

        Intent intent = getIntent();
        TravelDeal mDeal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (mDeal == null) {
            mDeal = new TravelDeal();
        }
        this.mDeal = mDeal;
        tvTitle.setText(mDeal.getTitle());
        tvDescription.setText(mDeal.getDescription());
        tvPrice.setText(mDeal.getPrice());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);
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

        if (id == R.id.delete_menu){
            deleteDeal();
            Toast.makeText(this, "Deal deleted", Toast.LENGTH_SHORT).show();
            backToList();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void deleteDeal(){
        if (mDeal == null){
            Toast.makeText(this, "Save deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(mDeal.getId()).removeValue();
    }

    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void clean() {
        tvTitle.setText("");
        tvDescription.setText("");
        tvPrice.setText("");
        tvTitle.requestFocus();
    }
}