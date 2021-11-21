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
    
    void setFields(String ip_address, String sub_mask){
        long ip = Long.parseLong(ip_address, 2);
        long subnet = Long.parseLong(sub_mask, 2);
        long sub_invert = (0xffffffffL)^subnet;

        long net_id = ip & subnet;
        long b_id = ip | sub_invert;
        long h_min = net_id + 1;
        long h_max = b_id - 1;
        String flag = "";
        String valid = "Valid";

        int class_def = Integer.parseInt(ip_address.substring(0, 8), 2);
        int next_byte;

        if(class_def >= 0 && class_def <= 127){
            if(class_def == 0){
                valid = "Invalid (Default route address)";
            } else if(class_def == 10){
                valid = "Invalid (Private IP)";
            } else if(class_def == 127){
                valid = "Invalid (Loopback address)";
            }
            flag = "A";
        } else if(class_def >= 128 && class_def <= 191){
            if(class_def == 172){
                next_byte = Integer.parseInt(ip_address.substring(8, 16), 2);
                if (next_byte >= 16 && next_byte <= 31)
                    valid = "Invalid (Private IP)";
            }
            flag = "B";
        } else if(class_def >= 192 && class_def <= 223){
            if(class_def == 192){
                next_byte = Integer.parseInt(ip_address.substring(8, 16), 2);
                if (next_byte == 168)
                    valid = "Invalid (Private IP)";
            }
            flag = "C";
        } else if(class_def >= 224 && class_def <= 239){
            valid = "Invalid (Multicast address)";
            flag = "D";
        } else if(class_def >= 240 && class_def <= 255){
            valid = "Invalid (Reserved/Research/Experimental)";
            flag = "E";
        }

        String net = String.format("%32s", Long.toBinaryString(net_id)).replaceAll(" ", "0");
        String broadcast = String.format("%32s", Long.toBinaryString(b_id)).replaceAll(" ", "0");
        String min = String.format("%32s", Long.toBinaryString(h_min)).replaceAll(" ", "0");
        String max = String.format("%32s", Long.toBinaryString(h_max)).replaceAll(" ", "0");

        address.setText(formatInput(ip_address));
        mask.setText(formatInput(sub_mask));
        network_id.setText(formatInput(net));
        broadcast_id.setText(formatInput(broadcast));
        if(h_max <= net_id){
            host_max.setText("N/A");
        }else{
            host_max.setText(formatInput(max));
        }
        if(h_min >= b_id){
            host_min.setText("N/A");
        }else{
            host_min.setText(formatInput(min));
        }
        ip_class.setText(flag);
        validity.setText(valid);
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

            if(subnet.isEmpty() || Integer.parseInt(subnet)>32 || Integer.parseInt(subnet)<=0) {
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

                host_count.setText(String.valueOf(Math.round(Math.pow(2, (32 - Integer.parseInt(subnet))))));
                setFields(ip_address, sub_mask);
            }
        });
    }
}
