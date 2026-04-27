package com.example.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AutomaticZenRule;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bluetooth.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Mar";
    ActivityMainBinding b;
    private BluetoothAdapter bluetoothAdapter;
    private final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
    };

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Uprawnienie przyznane
                } else {
                    // Uprawnienie odrzucone
                    finish();
                }
            });

    @SuppressLint("MissingPermission")
    private final ActivityResultLauncher<Intent> enableBluetoothLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Użytkownik kliknął "Zezwól" – Bluetooth jest teraz włączony
                    updateBluetooth();
                } else {
                    // Użytkownik odmówił włączenia
                }
            });

    private ArrayList<String> listPD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());

        EdgeToEdge.enable(this);
        setContentView(b.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(b.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
            }
        }

        ListView listViewPairedDevices = b.listPairedDevices;

        listPD = new ArrayList<>();
        ArrayAdapter<String> arrayAdapterPD = new ArrayAdapter<String>(this, R.layout.list_view_items, R.id.row, listPD);
        listViewPairedDevices.setAdapter(arrayAdapterPD);


        ListView listViewNewDevices = b.listNewDevices;

        ArrayList<String> listND = new ArrayList<>();
        ArrayAdapter<String> arrayAdapterND = new ArrayAdapter<String>(this, R.layout.list_view_items, R.id.row, listND);
        listViewNewDevices.setAdapter(arrayAdapterND);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        updateBluetooth();
    }

    private void listAllPairedDevices() {

        checkAllPermissions();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return;
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.d(TAG, "listAllPairedDevices: ");
                listPD.add(deviceName + " " + deviceHardwareAddress);
            }
        } else {
            Log.d(TAG, "listAllPairedDevices: " + pairedDevices.size());
            listPD.add("no devices found");
        }
    }

    private void checkAllPermissions() {
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void updateBluetooth() {
        checkBluetoothAvailable();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            finish();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth nie jest wspierany na tym urządzeniu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "onCreate: Bluetooth is OFF");
            b.textBluetooth.setText("BLUETOOTH OFF");
            b.textBluetooth.setBackgroundColor(getColor(R.color.red));

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        } else {
            Log.d(TAG, "onCreate: Bluetooth is ON");
            b.textBluetooth.setText("BLUETOOTH ON");
            b.textBluetooth.setBackgroundColor(getColor(R.color.green));
        }
    }

    private void checkBluetoothAvailable() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
//        unregisterReceiver();
//        unregisterReceiver();
//        unregisterReceiver();

        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN);
                }
                return;
            }
            bluetoothAdapter.cancelDiscovery();
        }
    }
}