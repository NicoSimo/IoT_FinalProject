package PacchettoFlotta.server;

import PacchettoFlotta.model.sensors.Batteries.Battery1;
import PacchettoFlotta.model.sensors.Fleet.GPSCar1;
import PacchettoFlotta.resource.batteries.CoapResourceBattery1;
import PacchettoFlotta.resource.gps.CoapResourceGps1;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Car1CoapProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(Car1CoapProcess.class);
    public static Battery1 battery1 = new Battery1();
    public static GPSCar1 gps1 = new GPSCar1();

    public Car1CoapProcess() {
        super(5681);

        CoapResourceBattery1 coapResourceBattery1 = new CoapResourceBattery1(
                battery1.SENSOR_ID,
                "Battery1",
                battery1);


        CoapResourceGps1 coapResourceGps1 = new CoapResourceGps1(
                gps1.SENSOR_ID,
                "Gps1",
                gps1);

        this.add(coapResourceBattery1);
        this.add(coapResourceGps1);

    }

    public static void main(String[] args){

        Car1CoapProcess car1CoapProcess = new Car1CoapProcess();

        logger.info("Starting CoAP Server...");

        car1CoapProcess.start();

        logger.info("Car1 CoAP server Started! Available resources: ");
        car1CoapProcess.getRoot().getChildren().stream().forEach(resource ->{
            logger.info("Resource : {} URI : {} (Observable :{})",resource.getName(),resource.getURI(),resource.isObservable());
        });

    }
}
