package com.example.ipcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Button calculate;
    EditText net_address, net_mask;
    String net_id, subnet, bin;
    String[] parts;
    int flag = 0, num;
    String ip_address, sub_mask;
    Pattern valid_ip = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculate = findViewById(R.id.calculate);
        net_address = findViewById(R.id.net_address);
        net_mask = findViewById(R.id.netmask);

        calculate.setOnClickListener(view -> {
            net_id = net_address.getText().toString();
            subnet = net_mask.getText().toString();

            if(subnet.isEmpty() || Integer.parseInt(subnet)>32 || Integer.parseInt(subnet)<0) {
                net_mask.setError("Enter valid input");
            }
            else{
                flag = 1;
            }

            Matcher match = valid_ip.matcher(net_id);

            if(match.find() && flag==1){
                ip_address = "";
                parts = net_id.split("[.]", 4);

                for (int i=0; i<4; i++){
                    num = Integer.parseInt(parts[i]);
                    bin = String.format("%8s", Integer.toBinaryString(num)).replaceAll(" ", "0");
                    ip_address = ip_address + bin;
                }
                sub_mask = String.format("%32s", Integer.toBinaryString(0xffffffff << (32-Integer.parseInt(subnet))));

//                Toast.makeText(getApplicationContext(), "IP = "+ip_address, Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), "Mask = "+mask, Toast.LENGTH_SHORT).show();
            }
            else{
                net_address.setError("Enter valid input");
            }
        });
    }
}