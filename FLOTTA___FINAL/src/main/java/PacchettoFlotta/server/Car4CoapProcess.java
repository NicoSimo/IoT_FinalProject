package PacchettoFlotta.server;

import PacchettoFlotta.model.sensors.Batteries.Battery4;
import PacchettoFlotta.model.sensors.Fleet.GPSCar4;
import PacchettoFlotta.resource.batteries.CoapResourceBattery4;
import PacchettoFlotta.resource.gps.CoapResourceGps4;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Car4CoapProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(Car4CoapProcess.class);
    public static Battery4 battery4 = new Battery4();
    public static GPSCar4 gps4 = new GPSCar4();

    public Car4CoapProcess() {
        super(5684);

        CoapResourceBattery4 coapResourceBattery4 = new CoapResourceBattery4(
                battery4.SENSOR_ID,
                "Battery4",
                battery4);

        CoapResourceGps4 coapResourceGps4 = new CoapResourceGps4(
                gps4.SENSOR_ID,
                "Gps4",
                gps4);

        this.add(coapResourceBattery4);
        this.add(coapResourceGps4);

    }

    public static void main(String[] args){
        Car4CoapProcess car4CoapProcess = new Car4CoapProcess();
        logger.info("Starting CoAP Server...");

        car4CoapProcess.start();

        logger.info("Car4 CoAP server Started! Available resources: ");
        car4CoapProcess.getRoot().getChildren().stream().forEach(resource ->{
            logger.info("Resource : {} URI : {} (Observable :{})",resource.getName(),resource.getURI(),resource.isObservable());
        });
    }
}
