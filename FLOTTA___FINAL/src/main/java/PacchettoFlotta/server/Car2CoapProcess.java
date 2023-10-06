package PacchettoFlotta.server;

import PacchettoFlotta.model.sensors.Batteries.Battery2;
import PacchettoFlotta.model.sensors.Fleet.GPSCar2;
import PacchettoFlotta.resource.batteries.CoapResourceBattery2;
import PacchettoFlotta.resource.gps.CoapResourceGps2;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Car2CoapProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(Car2CoapProcess.class);
    public static Battery2 battery2 = new Battery2();
    public static GPSCar2 gps2 = new GPSCar2();

    public Car2CoapProcess() {
        super(5682);

        CoapResourceBattery2 coapResourceBattery2 = new CoapResourceBattery2(
                battery2.SENSOR_ID,
                "Battery2",
                battery2);

        CoapResourceGps2 coapResourceGps2 = new CoapResourceGps2(
                gps2.SENSOR_ID,
                "Gps2",
                gps2);

        this.add(coapResourceBattery2);
        this.add(coapResourceGps2);
    }

    public static void main(String[] args){
        Car2CoapProcess car2CoapProcess = new Car2CoapProcess();

        logger.info("Starting CoAP Server...");

        car2CoapProcess.start();

        logger.info("Car2 CoAP server Started! Available resources: ");
        car2CoapProcess.getRoot().getChildren().stream().forEach(resource ->{
            logger.info("Resource : {} URI : {} (Observable :{})",resource.getName(),resource.getURI(),resource.isObservable());
        });
    }
}
