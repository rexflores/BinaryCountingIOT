package com.example.binarycountingiot;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WifiActivity extends AppCompatActivity {

    private EditText etIpAddress;
    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        etIpAddress = findViewById(R.id.et_ip_address);
        etIpAddress.setFilters(new InputFilter[]{new InputFilterForIpAddress()});

        btnConnect = findViewById(R.id.btn_connect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = etIpAddress.getText().toString();
                if (TextUtils.isEmpty(ipAddress) || !isValidIpAddress(ipAddress)) {
                    Toast.makeText(WifiActivity.this, "Please enter a valid IP address", Toast.LENGTH_SHORT).show();
                    return;
                }
                connectToWifi(ipAddress);
            }
        });
    }

    private boolean isValidIpAddress(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private void connectToWifi(String ipAddress) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled. Enabling WiFi...", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        // Set the IP address for the MainActivity to use
        MainActivity.BASE_URL = "http://" + ipAddress + "/";

        Toast.makeText(this, "Connected to the WiFi", Toast.LENGTH_SHORT).show();

        // Go back to the main activity
        Intent intent = new Intent(WifiActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private static class InputFilterForIpAddress implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals("")) { // Allow deletion
                return source;
            }
            if (source.toString().matches("[0-9.]+")) {
                return source;
            }
            return "";
        }
    }
}
