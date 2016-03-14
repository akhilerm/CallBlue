package com.slateandpencil.callblue;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

public class MyService extends Service {
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String address = "20:14:05:08:13:38";
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    String TAG="Error";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        if (mBluetoothAdapter.isEnabled()) {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyMgr.listen(new TeleListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetConnection();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private void errorExit(String title, String message){
        Log.e("Error Exit", title + ':' + message);
    }
    private void resetConnection() {
        Log.e(TAG,"Going to reset");
        if (outStream != null) {
            try {outStream.close();} catch (Exception e) {errorExit("Fatal Error", "Unable to close stream during connection failure" + e.getMessage() + ".");}
            outStream = null;
        }
        if (btSocket != null) {
            try {btSocket.close();} catch (Exception e) {errorExit("Fatal Error", "Unable to close socket during connection failure" + e.getMessage() + ".");}
            btSocket = null;
        }
        Log.e(TAG,"Connection reset");

    }

    class TeleListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            String message="1";
            if(state==TelephonyManager.CALL_STATE_RINGING){
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                try {
                    btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    Log.e(TAG,"CLient Connected");
                } catch (IOException e) {
                    errorExit("Fatal Error", "Socket create failed: " + e.getMessage() + ".");
                }
                mBluetoothAdapter.cancelDiscovery();
                Log.e(TAG, "...Connecting to Remote...");
                try {
                    btSocket.connect();
                    Log.e(TAG, "...Connection established and data link opened...");
                } catch (IOException e) {
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        errorExit("Fatal Error", "Unable to close socket during connection failure" + e2.getMessage() + ".");
                    }
                }
                Log.e(TAG, "...Creating Socket...");

                try {
                    outStream = btSocket.getOutputStream();
                    Log.e(TAG,"Ouput stream created");
                    byte[] msgBuffer = message.getBytes();
                    Log.e(TAG, msgBuffer + "");

                    Log.e(TAG, "...Sending data: " + message + "...");

                    try {
                        outStream.write(msgBuffer);
                        Log.e(TAG,"Message send");
                    } catch (IOException e) {
                        errorExit("Fatal Error","Exception occurred during write: " + e.getMessage());
                    }
                } catch (IOException e) {
                    errorExit("Fatal Error", "Output stream creation failed:" + e.getMessage() + ".");
                }
                //trnasimiiosn ends here
                resetConnection();
                Toast.makeText(MyService.this,"Ringing", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
