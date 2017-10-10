package com.example.viper2.blogg_app;

/*
https://wingoodharry.wordpress.com/2014/04/15/android-sendreceive-data-with-arduino-using-bluetooth-part-2/

 */
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    ImageView imagen;
    BluetoothAdapter adaptador;
    Button bBluetooth;

    Button tlbutton;
    TextView textView1;

    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bBluetooth = (Button) findViewById(R.id.bBluetooth);
       // imagen=(ImageView)findViewById(R.id.blue);
        adaptador=BluetoothAdapter.getDefaultAdapter();
        if (adaptador==null){
            imagen.setVisibility(View.GONE);
        }
        else{
          //  setimageblue(adaptador.isEnabled());}
            adaptador.isEnabled();}
        /*
        bBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setestadoblue();


            if(eUsername.getText().toString().equals("") || ePassword.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "Error \n Alguno o ambos espacios estan vacios", Toast.LENGTH_SHORT).show();
            }
            else {
                //Validar los datos digitados con los del registro
                if (eUsername.getText().toString().equals(username) && ePassword.getText().toString().equals(password)){

                    editor.putInt("login",1);//sobre escribimos con 1 (alguien ya esta loggeado)//practica 5
                    editor.commit();//practica 5

                    intent = new Intent (LoginActivity.this, MainActivityD.class);
                    intent.putExtra("username", username);
                    intent.putExtra("correo", correo);
                    startActivity(intent);
                    setResult(RESULT_OK, intent);
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(), "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }

            }

            }

        });
        */
    }

    /*
    public void click_imagen(View v){
        setestadoblue();}
    public void setimageblue(boolean valor)
    {
        if (valor)imagen.setImageResource(R.drawable.blue);
        else imagen.setImageResource(R.drawable.blue2);
    }
    */
    @Override
    public void onResume()
    {
        super.onResume();
        //***************
        checkBTState();

        textView1 = (TextView) findViewById(R.id.connecting);
        textView1.setTextSize(40);
        textView1.setText(" ");

        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices and append to 'pairedDevices'
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Add previosuly paired devices to the array
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }



    // Set up on-click listener for the list (nicked this - unsure)
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            textView1.setText("Conectando...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);


            // Make an intent to start next activity while taking an extra which is the MAC address.
            Intent i = new Intent(MainActivity.this, OptionActivity.class);
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);
            finish();

        }
    };
    //
    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth Activado...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }
    /*
    //para saber si el bth esta activo o no?
    public void setestadoblue()
    {
        if(adaptador.isEnabled()){
            // setimageblue(false);
            adaptador.disable();
            Toast.makeText(getApplicationContext(), "Bluetooth Disable", Toast.LENGTH_SHORT).show();
        }
        else{
            //setimageblue(true);
            adaptador.enable();
            Toast.makeText(getApplicationContext(), "Bluetooth Enable", Toast.LENGTH_SHORT).show();
        }
    }
    */
    //fin de las funciones
}
