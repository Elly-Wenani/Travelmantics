package com.example.travelmantics;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_insert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUtil.openFbReference("traveldeals");

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
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
            clean();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDeal() {
        String title = tvTitle.getText().toString();
        String description = tvDescription.getText().toString();
        String price = tvPrice.getText().toString();

        TravelDeal deal = new TravelDeal(title, description, price, "");
        mDatabaseReference.push().setValue(deal);
    }

    private void clean() {
        tvTitle.setText("");
        tvDescription.setText("");
        tvPrice.setText("");
        tvTitle.requestFocus();
    }
}