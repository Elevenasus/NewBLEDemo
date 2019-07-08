package com.seeingvoice.www.newbledemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final UUID UUID_SERVICE = UUID.fromString("5052494D-2DAB-0141-6972-6F6861424C45");  //主Service的UUID
//    public static final UUID UUID_NOTIFY = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb"); //具有通知属性的UUID
    public static final UUID UUID_READ   = UUID.fromString("43484152-2DAB-1441-6972-6F6861424C45"); //具有读取属性的UUID
//    public static final UUID UUID_WRITE  = UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb"); //具有写入属性的UUID

    private BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = MainActivity.class.getName();
    private BluetoothEnableStateReceiver mBluetoothStateReceiver;
    private boolean mIsScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //判断该设备是否支持Ble并获取BluetoothAdapter
        ensureBLEExists();
    }

    /**
     * 判断该设备是否支持Ble并获取BluetoothAdapter
     */
    public Boolean ensureBLEExists() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        //获取BluetoothAdapter
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bm!=null) mBluetoothAdapter = bm.getAdapter();
        return true;
    }

    /**
     * 注册蓝牙状态改变的监听广播
     */
    private void registerBlueToothReceiver(){
        if (mBluetoothStateReceiver==null)
        mBluetoothStateReceiver = new BluetoothEnableStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothStateReceiver,filter);
    }

    //蓝牙开关状态的广播接收者，可以通过设置接口回调进行监听，
    //以方便在蓝牙状态变化的时候做出相应操作或提示
    public class BluetoothEnableStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.i(TAG, "BluetoothOnOffStateReceiver: state: " + state);
                if(state == BluetoothAdapter.STATE_ON) {
                    //蓝牙打开

                } else if(state == BluetoothAdapter.STATE_TURNING_OFF){
                    //蓝牙正在关闭

                } else if(state == BluetoothAdapter.STATE_OFF){
                    //蓝牙已关闭
                }
            }
        }
    }


    /**
     * 开启蓝牙
     */
    public void enableBluetooth() {
        if (mBluetoothAdapter!=null){
            if (!mBluetoothAdapter.isEnabled()) { //蓝牙未开启，通过隐式意图请求开启蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 0);
            }
        }
    }


    public BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) { //device是设备对象，rssi是信号强度，scanRecord是扫描记录
            if (device != null) {
                //接口回调扫描到的设备
//                synchronized (mCallBacks) {
//                    for (BleAdapterCallBack callBack : mCallBacks) {
//                        callBack.onDeviceFound(device, rssi);
//                    }
//                }
            }
        }

        /**
         * 开始扫描 10秒后自动停止
         */
        private void startScan() {
            UUID[] uuid = {UUID_SERVICE};
            if (mIsScanning) { //如果当前正在扫描则先停止扫描
                mBluetoothAdapter.stopLeScan(mScanCallback);
            }
            //mBluetoothAdapter.startLeScan(scanCallback);//不进行特定设备过滤，扫描所有设备
            //进行特定uuid过滤，只扫描具有指定Service UUID的设备
            mBluetoothAdapter.startLeScan(uuid, mScanCallback);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //结束扫描
                    mBluetoothAdapter.stopLeScan(mScanCallback);
                }
            }, 10000);
        }
    };


    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };
}
