package PacchettoFlotta.model.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchSecureRawActuator extends SmartObject<Boolean> {

    // actuator's parameters
    private long timestamp;
    private boolean value;

    private static final Logger logger = LoggerFactory.getLogger(SwitchSecureRawActuator.class);

    public SwitchSecureRawActuator(String vehicle, String model) {
        super(vehicle, model);
        this.value = false;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void switchStatusOn(){
        this.value = true;
        notifyUpdate(isValue());
        this.timestamp = System.currentTimeMillis();
    }

    public void switchStatusOff(){
        this.value = false;
        notifyUpdate(isValue());
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public Boolean UpdateValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SwitchRawActuator{");
        sb.append("uuid='").append(getId()).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {

        SwitchSecureRawActuator rawResource = new SwitchSecureRawActuator("vehicle-01", "Mercedes");
        logger.info("New Resource Created with Id: {} ! {} New Value: {}",
                rawResource.getId(),
                "SwitchActuator",
                rawResource.UpdateValue());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i=0; i<100; i++){
                        if (rawResource.UpdateValue()) {
                            rawResource.switchStatusOff();
                        } else {
                            rawResource.switchStatusOn();
                        }
                        Thread.sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        rawResource.addDataListener(new DataListener<Boolean>() {
            @Override
            public void onDataChanged(SmartObject<Boolean> resource, Boolean updatedValue) {

                if(resource != null && updatedValue != null)
                    logger.info("Device: {} -> New Value Received: {}", resource.getId(), updatedValue);
                else
                    logger.error("onDataChanged Callback -> Null Resource or Updated Value !");
            }
        });
    }
}