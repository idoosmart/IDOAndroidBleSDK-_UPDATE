package test.com.ido.logoutput.bluetooth;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author: zhouzj
 * @date: 2017/11/16 14:53
 */

public class BluetoothLogoutManager {
    private static BluetoothLogoutManager manager;
    private BluetoothChatService bluetoothChatService;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case BluetoothConstants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case BluetoothConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case BluetoothConstants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
                    break;
                case BluetoothConstants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
                    break;
            }
        }
    };
    private BluetoothLogoutManager(){
        bluetoothChatService = new BluetoothChatService(handler);
    }
    public static BluetoothLogoutManager getManager(){
        if (manager == null){
            manager = new BluetoothLogoutManager();
        }
        return manager;
    }



    public void start(){
        if (bluetoothChatService.getState() == BluetoothChatService.STATE_NONE){
            bluetoothChatService.start();
        }
    }

    public void stop(){
        bluetoothChatService.stop();
    }

    public static void write(String log){
        if (manager == null || manager.bluetoothChatService == null){
            return;
        }

        if (manager.bluetoothChatService.getState() != BluetoothChatService.STATE_CONNECTED){
            return;
        }
        if (log.length() > 0){
            byte[] send = log.getBytes();
            manager.bluetoothChatService.write(send);
        }
    }

}
