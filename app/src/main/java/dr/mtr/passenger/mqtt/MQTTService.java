package dr.mtr.passenger.mqtt;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

import dr.mtr.passenger.model.driverLocation.EODriverCurrentLocation;

import static dr.mtr.passenger.utils.Constants.DOMAIN_ID_Value;

public class MQTTService {

    private static final String TAG = MQTTService.class.getSimpleName();
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
    private AWSIotMqttManager mqttManager;
    private Context mContext;
    private OnLocationReceived onLocationReceived;
    private static MQTTService instance = null;


    public MQTTService(Context context, String passengerId, OnLocationReceived onLocationReceived) {
        this.mContext = context;
        this.onLocationReceived = onLocationReceived;

        String clientId = UUID.randomUUID().toString();
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);
        mqttManager.setKeepAlive(10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("goingOffline/" + passengerId, "Android client lost connection", AWSIotMqttQos.QOS0);
        mqttManager.setMqttLastWillAndTestament(lwt);

        String keystorePath = mContext.getFilesDir().getPath();
        String keystoreName = KEYSTORE_NAME;
        String keystorePassword = KEYSTORE_PASSWORD;
        String certificateId = CERTIFICATE_ID;

        File file = new File(mContext.getFilesDir(), "iot_keystore");
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

    @NonNull
    public AWSIotMqttManager getAWSIotMqttManager() {
        return mqttManager;
    }

    private void copyAssets() {
        AssetManager assetManager = mContext.getAssets();
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
                        File outFile = new File(mContext.getFilesDir(), filename);
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

    public void disconnectMqtt() {
        Log.d(MQTTService.TAG, TAG);
        if (mqttManager != null) {
            mqttManager.disconnect();
        }
    }

    public AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus mqttstatus;

    private void connectAWSMqtt(KeyStore clientKeyStore) {
        mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
            @Override
            public void onStatusChanged(final AWSIotMqttClientStatus status, final Throwable throwable) {
                mqttstatus = status;
                if (status == AWSIotMqttClientStatus.Connecting) {
                    Log.d(TAG, "Connecting...");
                } else if (status == AWSIotMqttClientStatus.Connected) {
                    Log.d(TAG, "Connected");
                    Gson gson = new Gson();

                    mqttManager.subscribeToTopic(DOMAIN_ID_Value + "/driverslocation/#", AWSIotMqttQos.QOS0, new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(String topic, byte[] data) {
                            try {
                                //TODO from here parse the json response in object received from mqtt service
                                EODriverCurrentLocation driverCurrentLocation = gson.fromJson(new String(data, "UTF-8"), EODriverCurrentLocation.class);
                                if (onLocationReceived != null)
                                    onLocationReceived.onDriverLocationUpdate(driverCurrentLocation);

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
    }

}