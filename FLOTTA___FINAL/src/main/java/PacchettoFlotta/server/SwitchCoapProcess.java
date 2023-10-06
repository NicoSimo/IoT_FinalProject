package PacchettoFlotta.server;

import PacchettoFlotta.model.sensors.Switch.Switch1;
import PacchettoFlotta.model.sensors.Switch.Switch2;
import PacchettoFlotta.model.sensors.Switch.Switch3;
import PacchettoFlotta.model.sensors.Switch.Switch4;
import PacchettoFlotta.resource.Switch.CoapResourceSwitch1;
import PacchettoFlotta.resource.Switch.CoapResourceSwitch2;
import PacchettoFlotta.resource.Switch.CoapResourceSwitch3;
import PacchettoFlotta.resource.Switch.CoapResourceSwitch4;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchCoapProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(SwitchCoapProcess.class);
    Switch1 switch1 = new Switch1();
    Switch2 switch2 = new Switch2();
    Switch3 switch3 = new Switch3();
    Switch4 switch4 = new Switch4();

    public SwitchCoapProcess() {
        super(5685);

        CoapResourceSwitch1 coapResourceSwitch1 = new CoapResourceSwitch1(
                switch1.SENSOR_ID,
                switch1);
        CoapResourceSwitch2 coapResourceSwitch2 = new CoapResourceSwitch2(
                Switch2.SENSOR_ID,
                switch2);
        CoapResourceSwitch3 coapResourceSwitch3 = new CoapResourceSwitch3(
                switch3.SENSOR_ID,
                switch3);

        CoapResourceSwitch4 coapResourceSwitch4 = new CoapResourceSwitch4(
                switch4.SENSOR_ID,
                switch4);

        this.add(coapResourceSwitch1);
        this.add(coapResourceSwitch2);
        this.add(coapResourceSwitch3);
        this.add(coapResourceSwitch4);

    }

    public static void main(String[] args){

        SwitchCoapProcess switchCoapProcess = new SwitchCoapProcess();

        logger.info("Starting CoAP Server...");

        switchCoapProcess.start();

        logger.info("Switch CoAP server Started! Available resources: ");
        switchCoapProcess.getRoot().getChildren().stream().forEach(resource ->{
            logger.info("Resource : {} URI : {} (Observable :{})",resource.getName(),resource.getURI(),resource.isObservable());
        });

    }
}
