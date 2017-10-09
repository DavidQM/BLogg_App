package com.example.viper2.blogg_app;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class OptionActivity extends AppCompatActivity {


    private static final String TAG = "Read";
    Handler bluetoothIn;

    Button btn1,btn2,btn3,btn4;
    TextView txt1;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address = null;

    public String box=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("\n");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txt1.setText("D_Rx = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                        //txtStringLength.setText("String Length = " + String.valueOf(dataLength));
                        /*
                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);

                            sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");    //update the textviews with sensor values
                            sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
                            sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
                            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                        }
                        */
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        txt1 = (TextView) findViewById(R.id.txt1);


        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "boton 1", Toast.LENGTH_SHORT).show();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("+");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "boton 2", Toast.LENGTH_SHORT).show();

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("-");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "boton 3", Toast.LENGTH_SHORT).show();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mConnectedThread.write("2");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "boton 4", Toast.LENGTH_SHORT).show();
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
        address = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);
        //create device and set the MAC address
        //Log.i("ramiro", "adress : " + address);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        //Toast.makeText(getBaseContext(), address, Toast.LENGTH_LONG).show();

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

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write("-");

    }

    /*
    @Override
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }
    */

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

    //create new class for connect thread

    private class ConnectedThread extends Thread {
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
                    //box= bluetoothIn.toString();
                    //Toast.makeText(getBaseContext(), readMessage, Toast.LENGTH_LONG).show();
                    //readMessage.replace('\n',' ');
                   // box= readMessage;
                   //Log.d(TAG, box);
                   //Toast.makeText(getBaseContext(), box, Toast.LENGTH_LONG).show();
                    //box=null;
                    //txt1.setText(readMessage);
                } catch (IOException e) {
                    break;
                }

                //Toast.makeText(getBaseContext(), "Read", Toast.LENGTH_LONG).show();
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                Toast.makeText(getBaseContext(), "Send", Toast.LENGTH_LONG).show();
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }

}
