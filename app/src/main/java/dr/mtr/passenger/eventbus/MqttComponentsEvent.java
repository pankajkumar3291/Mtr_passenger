package dr.mtr.passenger.eventbus;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;

import dr.mtr.passenger.mqtt.MQTTServiceClass;

public class MqttComponentsEvent {

    private AWSIotMqttManager mqttManager;
    private AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus status;
    private MQTTServiceClass.AWSIotMqttForException clientstatus;

//    private static MqttComponentsEvent mqttComponentsEvent;
//    //private constructor.
//    private MqttComponentsEvent() {
//        //Prevent form the reflection api.
//        if (mqttComponentsEvent != null) {
//            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
//        }
//    }
//    public synchronized static MqttComponentsEvent getInstance() {
//        if (mqttComponentsEvent == null) { //if there is no instance available... create new one
//            mqttComponentsEvent = new MqttComponentsEvent();
//        }
//        return mqttComponentsEvent;
//    }

    public MqttComponentsEvent(AWSIotMqttManager mqttManager, AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus status, MQTTServiceClass.AWSIotMqttForException clientstatus) {
        this.mqttManager = mqttManager;
        this.status = status;
        this.clientstatus = clientstatus;
    }

    public AWSIotMqttManager getMqttManager() {
        return mqttManager;
    }

    public void setMqttManager(AWSIotMqttManager mqttManager) {
        this.mqttManager = mqttManager;
    }

    public AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus getStatus() {
        return status;
    }

    public void setStatus(AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus status) {
        this.status = status;
    }

    public MQTTServiceClass.AWSIotMqttForException getClientstatus() {
        return clientstatus;
    }

    public void setClientstatus(MQTTServiceClass.AWSIotMqttForException clientstatus) {
        this.clientstatus = clientstatus;
    }

}
