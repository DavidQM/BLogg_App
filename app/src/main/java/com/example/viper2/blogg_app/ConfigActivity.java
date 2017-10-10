package com.example.viper2.blogg_app;
/*
https://neurobin.org/docs/android/android-time-picker-example/
http://www.technotalkative.com/android-get-current-date-and-time/

https://danielme.com/2013/04/25/diseno-android-spinner/
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

public class ConfigActivity extends AppCompatActivity {

    private static final String TAG = "Read";
    Handler bluetoothIn;

    Date currentTime = Calendar.getInstance().getTime();

    Button btnSend;
    TextView txt1,txt2,txt3;
    Spinner sPack;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private OptionActivity.ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address = null;

    public String box=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("\n");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                       if (recDataString.charAt(0) == '+')                             //if it starts with # we know it is what we are looking for
                        {
                            //YY/MY/DD/W/HA/MA/HI/MI/FF/N/MS
                            String DD = recDataString.substring(1, 3);
                            String MY = recDataString.substring(4, 6);
                            String YY = recDataString.substring(7, 11);
                            String HA = recDataString.substring(12, 14);
                            String MA = recDataString.substring(15, 17);
                            String HI = recDataString.substring(18, 20);
                            String MI = recDataString.substring(21, 23);
                            String N = recDataString.substring(24, 28);
                            String FF = recDataString.substring(29, 31);
                            String MS = recDataString.substring(32, 34);
                            //hacer calculo para N
                            /*
                            txt1.setText(" Date = " + DD + "/"+ MY + "/"+ YY + " Time = "+ HA + ":" + MA);
                            txt2.setText(" Start Time = " + HI + ":" + MI );
                            txt3.setText(" N(Pack) = " + N + " F(Hz) = " + FF+ " Break Time =" + MS);
                            */

                        }

                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        dataInPrint = " ";
                    }
                }
            }
        };
        ////
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        btnSend = (Button) findViewById(R.id.btnSend);
        sPack = (Spinner) findViewById(R.id.sPack);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Pack, R.layout.spinner_item);
        sPack.setAdapter(adapter);

        sPack.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                Toast.makeText(getBaseContext(),"Data N = "+Integer.toString(i), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mConnectedThread.write("1");    // Send "Config" via Bluetooth
                /*
                Intent i = new Intent(ConfigActivity.this, OptionActivity.class);
                i.putExtra(EXTRA_DEVICE_ADDRESS,address);
                startActivity(i);
                finish();
                */
            }
        });

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    public void onResume() {
        super.onResume();
        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();
        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(OptionActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }

       /// mConnectedThread = new OptionActivity.ConnectedThread(btSocket);
        //mConnectedThread.start();
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            // Keep looping to listen for received messages
            while (true) {

                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                //Toast.makeText(getBaseContext(), "Send", Toast.LENGTH_LONG).show();
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                Intent i = new Intent(ConfigActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        }
    }
}
