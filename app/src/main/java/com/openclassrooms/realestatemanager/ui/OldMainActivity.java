package com.openclassrooms.realestatemanager.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.utils.Utils;

public class OldMainActivity extends AppCompatActivity {

    private TextView textViewMain;
    private TextView textViewQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);

        //wrong layout id
        textViewMain = findViewById(R.id.activity_main_activity_text_view_main);
        textViewQuantity = findViewById(R.id.activity_main_activity_text_view_quantity);

        configureTextViewMain();
        configureTextViewQuantity();
    }

    private void configureTextViewMain(){
        textViewMain.setTextSize(15);
        textViewMain.setText("Le premier bien immobilier enregistré vaut ");
    }

    private void configureTextViewQuantity(){
        int quantity = Utils.convertDollarToEuro(100);
        textViewQuantity.setTextSize(20);
        //parameter int changed into String
        textViewQuantity.setText(String.valueOf(quantity));
    }
}
