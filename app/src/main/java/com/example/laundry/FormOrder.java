package com.example.laundry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class FormOrder extends AppCompatActivity {

    TextView idOfCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_order);
        idOfCustomer = findViewById(R.id.idOfCustomer);
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        idOfCustomer.setText(id);
    }
}