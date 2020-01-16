package dr.mtr.passenger.mqtt;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.application.GlobalApplication;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.eventbus.DriverLocationEvent;
import dr.mtr.passenger.eventbus.MqttComponentsEvent;
import dr.mtr.passenger.model.driverLocation.EODriverCurrentLocation;

import static dr.mtr.passenger.utils.Constants.DOMAIN_ID_Value;
import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_INFO_ID;

public class MQTTServiceClass extends Service {

    private static final String TAG = MQTTServiceClass.class.getSimpleName();
    // --- Constants to modify per your configuration ---
    // IoT endpoint
    // AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a2n8sw3ph0d1w.iot.us-east-1.amazonaws.com";
    // Filename of KeyStore file on the filesystem
    private static final String KEYSTORE_NAME = "iot_keystore";
    // Password for the private key in the KeyStore
    private static final String KEYSTORE_PASSWORD = "password";
    // Certificate and key aliases in the KeyStore
    private static final String CERTIFICATE_ID = "default";
    public static AWSIotMqttManager mqttManager;
    private SessionSecuredPreferences loginPreferences;
    private String passengerId;

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        // run till explicitly stopped, restart when
        // process restarted
        // registerBroadcastReceivers();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.passengerId = loginPreferences.getString(PASSENGER_INFO_ID, "");

        String clientId = UUID.randomUUID().toString();
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);
        mqttManager.setKeepAlive(10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("goingOffline/" + passengerId, "Android client lost connection", AWSIotMqttQos.QOS0);
        mqttManager.setMqttLastWillAndTestament(lwt);

        String keystorePath = getFilesDir().getPath();
        String keystoreName = KEYSTORE_NAME;
        String keystorePassword = KEYSTORE_PASSWORD;
        String certificateId = CERTIFICATE_ID;

        File file = new File(getFilesDir(), "iot_keystore");
        if (!file.exists()) {
            //Do something
            Log.d(TAG, "Certificate doesnt ..Copying certificate");
            copyAssets();
        } else {
            // Do something else.
            Log.d(TAG, "File Aready Exists inside the File Directory");
        }

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath, keystoreName, keystorePassword)) {
                    Log.i(TAG, "Certificate " + certificateId + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    KeyStore clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId, keystorePath, keystoreName, keystorePassword);
                    connectAWSMqtt(clientKeyStore);
                } else {
                    Log.i(TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(TAG, "An error occurred retrieving cert/key from keystore.", e);
        }
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null)
            for (String filename : files) {
                if (filename.equalsIgnoreCase("iot_keystore")) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open(filename);
                        File outFile = new File(getFilesDir(), filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("tag", "Failed to copy asset file: " + filename, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("DESTROY_SERVICE", TAG);
//        if (mqttManager != null) {
//            mqttManager.disconnect();
//        }
    }

    public static AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus mqttClientStatus;

    private void connectAWSMqtt(KeyStore clientKeyStore) {

        try {
            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status, final Throwable throwable) {
                    ((GlobalApplication) getApplicationContext()).getBus().sendMqttComponents(new MqttComponentsEvent(mqttManager, status, AWSIotMqttForException.MayBeConnected));
                    if (status == AWSIotMqttClientStatus.Connecting) {
                        System.out.println("MQTTService connecting...");
                    } else if (status == AWSIotMqttClientStatus.Connected) {
                        mqttClientStatus = status;

                        System.out.println("MQTTService connected...");

                        //EventBus.getDefault().post(new MqttComponentsEvent(clientKeyStore, mqttManager, status, AWSIotMqttForException.MayBeConnected));
//                        MqttComponentsEvent mqttComponentsEvent = new MqttComponentsEvent(clientKeyStore, mqttManager, status, AWSIotMqttForException.MayBeConnected);
//                        EventBus.getDefault().post(new GetMqttEvent(mqttComponentsEvent));

                        Gson gson = new Gson();
                        mqttManager.subscribeToTopic(DOMAIN_ID_Value + "/driverslocation/#", AWSIotMqttQos.QOS0, new AWSIotMqttNewMessageCallback() {
                            @Override
                            public void onMessageArrived(String topic, byte[] data) {
                                try {
                                    //TODO from here parse the json response in object received from mqtt service
                                    EODriverCurrentLocation driverCurrentLocation = gson.fromJson(new String(data, "UTF-8"), EODriverCurrentLocation.class);
                                    EventBus.getDefault().post(new DriverLocationEvent(driverCurrentLocation));

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                        if (throwable != null) {
                            Log.e(TAG, "Connection error.", throwable);
                        }
                        Log.d(TAG, "Reconnecting");
                    } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                        if (throwable != null) {
                            Log.e(TAG, "Connection error.", throwable);
                        }
                        Log.d(TAG, "Disconnected");
                    } else {
                        Log.d(TAG, "Disconnected");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Connection error.", e);
            ((GlobalApplication) getApplicationContext()).getBus().sendMqttComponents(new MqttComponentsEvent(mqttManager, AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost, AWSIotMqttForException.ExceptionOccured));
            //EventBus.getDefault().post(new MqttComponentsEvent(clientKeyStore, mqttManager, AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost, AWSIotMqttForException.ExceptionOccured));
        }
    }

    /**
     * Possible connection statuses.
     */
    public enum AWSIotMqttForException {
        /**
         * Client will not be started.
         */
        ExceptionOccured,
        /**
         * Client trying.
         */
        MayBeConnected,
    }

}
