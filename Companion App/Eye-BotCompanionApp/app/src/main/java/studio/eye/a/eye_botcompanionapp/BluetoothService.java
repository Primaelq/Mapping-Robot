package studio.eye.a.eye_botcompanionapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService extends Service
{
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;

    Handler mHandler;

    BluetoothAdapter btAdapter;

    static ConnectedThread connectedThread;

    @Override
    public void onCreate ()
    {
        super.onCreate();
        mHandler = new CustomHandler(mHandler);
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {
        Toast.makeText(BluetoothService.this, "Bluetooth service has started", Toast.LENGTH_SHORT).show();
        startConnectThread();
        return START_STICKY;
    }

    @Override
    public void onDestroy ()
    {

        Toast.makeText(this, "Bluetooth service has Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind (Intent intent)
    {
        return null;
    }

    public void startConnectThread ()
    {
        btAdapter = SplashScreen.btAdapter;
        ConnectThread connect = new ConnectThread(SplashScreen.selectedDevice);
        connect.start();
    }

    public void startConnectedThread (Message msg)
    {
        SplashScreen.isConnected = true;
        connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
    }

    public static void SendOverBluetooth (String message)
    {
        connectedThread.write(message.getBytes());
    }

    private class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device)
        {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try
            {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e)
            {
                Toast.makeText(getApplicationContext(), "Unable to get UUID", Toast.LENGTH_SHORT).show();
            }
            mmSocket = tmp;
        }

        public void run()
        {
            Looper.prepare();

            btAdapter.cancelDiscovery();

            try
            {
                Toast.makeText(getApplicationContext(), "Connecting to " + SplashScreen.selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                mmSocket.connect();
            }
            catch (IOException connectException)
            {
                try
                {
                    mmSocket.close();
                }
                catch (IOException closeException)
                {
                    cancel ();
                }
            }
            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();

        }

        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                Toast.makeText(getApplicationContext(), "Unable to close socket", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e)
            {
                Toast.makeText(getApplicationContext(), "Unable to get I/O Streams", Toast.LENGTH_SHORT).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer; // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true)
            {
                try
                {
                    buffer = new byte[1024];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e)
                {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes)
        {
            try
            {
                mmOutStream.write(bytes);
            }
            catch (IOException e)
            {
                cancel ();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                Toast.makeText(getApplicationContext(), "Unable to close socket", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class CustomHandler extends Handler
    {
        //Activity activity;
        Handler mHandler;
        BluetoothService bs = new BluetoothService();
        String s = "Connected to ";

        public CustomHandler (Handler tmpHandler)//, Handler tmpHandler)
        {
            //activity = a;
            mHandler = tmpHandler;
        }

        @Override
        public void handleMessage (Message msg)
        {
            switch (msg.what)
            {
                case SUCCESS_CONNECT:
                    s += SplashScreen.selectedDevice.getName();
                    //Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                    SplashScreen.stateTextView.setText(s);
                    bs.startConnectedThread(msg);
                    break;

                case MESSAGE_READ:
                    byte[] readBuf = (byte[])msg.obj;
                    String receivedMessage = new String(readBuf);
                    //Toast.makeText(activity, receivedMessage, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
