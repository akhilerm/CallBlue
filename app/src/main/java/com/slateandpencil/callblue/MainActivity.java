package com.slateandpencil.callblue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private int REQUEST_ENABLE_BT=20;




    /*private CallStateListener callStateListener;
    TelephonyManager tm;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button start = (Button) findViewById(R.id.start);
        Button stop = (Button) findViewById(R.id.stop);
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                else {
                    startService();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), MyService.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), MyService.class));
    }

    //@Override
    /*public void onResume() {
        super.onResume();

        Log.e(TAG, "...In onResume - Attempting client connect...");

        //Set up a pointer to the remote node using it's address.
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.e(TAG,"CLient Connected");
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        mBluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.e(TAG, "...Connecting to Remote...");
        try {
            btSocket.connect();
            Log.e(TAG, "...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.e(TAG, "...Creating Socket...");

        try {
            outStream = btSocket.getOutputStream();
            Log.e(TAG,"Ouput stream created");
            sendData("1");
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }*/

    /*@Override
    public void onPause() {
        super.onPause();

        Log.e(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }*/

   /* private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(mBluetoothAdapter==null) {
            errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                Log.e(TAG, "...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }*/



    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        try {
            super.onActivityResult(requestcode, resultcode, data);
            if (requestcode == REQUEST_ENABLE_BT) {
                if(resultcode==RESULT_OK) {
                    startService();
                }
                else{
                    Toast.makeText(MainActivity.this,"Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}