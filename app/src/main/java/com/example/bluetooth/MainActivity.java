package com.example.bluetooth;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listViewPairedDevices = findViewById(R.id.listPairedDevices);

        ArrayList<String> listPD = new ArrayList<>();
        ArrayAdapter<String> arrayAdapterPD = new ArrayAdapter<String>(this, R.layout.list_view_items, R.id.row, listPD);
        listViewPairedDevices.setAdapter(arrayAdapterPD);


        ListView listViewNewDevices = findViewById(R.id.listNewDevices);

        ArrayList<String> listND = new ArrayList<>();
        ArrayAdapter<String> arrayAdapterND = new ArrayAdapter<String>(this, R.layout.list_view_items, R.id.row, listND);
        listViewNewDevices.setAdapter(arrayAdapterPD);
    }
}