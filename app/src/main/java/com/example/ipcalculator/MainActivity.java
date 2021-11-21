package com.example.ipcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Button calculate;
    EditText net_address, net_mask;
    TextView address, mask, ip_class, validity, network_id, broadcast_id, host_min, host_max, host_count;
    String net_id, subnet, bin;
    String[] parts;
    int flag1 = 0, flag2 = 0, num;
    String ip_address, sub_mask;
    Pattern valid_ip = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    String formatInput(String input){
        String format = input.replaceAll("([01]{8})", "$1.");
        String[] parts = format.split("[.]", 5);
        String out = "";

        for(int i=0; i<3; i++){
            out = out + Integer.parseInt(parts[i], 2) + " . ";
        }
        out = out + Integer.parseInt(parts[3], 2);
        return out;
    }

    int isValid(String ip_address){
        //TODO: Check restricted ip addresses and compare with input. If restricted, return 1 else 0
        return 1;
    }

    void setFields(String ip_address, String sub_mask){
        long ip = Long.parseLong(ip_address, 2);
        long subnet = Long.parseLong(sub_mask, 2);
        long sub_invert = (0xffffffffL)^subnet;

        String net = String.format("%32s", Long.toBinaryString(ip & subnet)).replaceAll(" ", "0");
        String broadcast = String.format("%32s", Long.toBinaryString(ip | sub_invert)).replaceAll(" ", "0");

        address.setText(formatInput(ip_address));
        mask.setText(formatInput(sub_mask));
        network_id.setText(formatInput(net));
        broadcast_id.setText(formatInput(broadcast));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculate = findViewById(R.id.calculate);
        net_address = findViewById(R.id.net_address);
        net_mask = findViewById(R.id.netmask);
        address = findViewById(R.id.address);
        mask = findViewById(R.id.mask);
        ip_class = findViewById(R.id.ip_class);
        validity = findViewById(R.id.validity);
        network_id = findViewById(R.id.net_id);
        broadcast_id = findViewById(R.id.broadcast_id);
        host_min = findViewById(R.id.host_min);
        host_max = findViewById(R.id.host_max);
        host_count = findViewById(R.id.host_count);

        calculate.setOnClickListener(view -> {
            net_id = net_address.getText().toString();
            subnet = net_mask.getText().toString();

            Matcher match = valid_ip.matcher(net_id);

            if(match.find()){
                flag1 = 1;
            }else{
                flag1 = 0;
                net_address.setError("Enter valid input");
            }

            if(subnet.isEmpty() || Integer.parseInt(subnet)>32 || Integer.parseInt(subnet)<0) {
                net_mask.setError("Enter valid input");
                flag2 = 0;
            }else{
                flag2 = 1;
            }

            if(flag1==1 && flag2==1){
                ip_address = "";
                parts = net_id.split("[.]", 5);

                for (int i=0; i<4; i++){
                    num = Integer.parseInt(parts[i]);
                    bin = String.format("%8s", Integer.toBinaryString(num)).replaceAll(" ", "0");
                    ip_address = ip_address + bin;
                }
                sub_mask = String.format("%32s", Integer.toBinaryString(0xffffffff << (32-Integer.parseInt(subnet))));

                setFields(ip_address, sub_mask);
            }
        });
    }
}