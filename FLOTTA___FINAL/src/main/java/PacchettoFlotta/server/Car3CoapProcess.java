package PacchettoFlotta.server;

import PacchettoFlotta.model.sensors.Batteries.Battery3;
import PacchettoFlotta.model.sensors.Fleet.GPSCar3;
import PacchettoFlotta.resource.batteries.CoapResourceBattery3;
import PacchettoFlotta.resource.gps.CoapResourceGps3;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Car3CoapProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(Car3CoapProcess.class);
    public static Battery3 battery3 = new Battery3();
    public static GPSCar3 gps3 = new GPSCar3();


    public Car3CoapProcess() {
        super(5683);

        CoapResourceBattery3 coapResourceBattery3 = new CoapResourceBattery3(
                battery3.SENSOR_ID,
                "Battery3",
                battery3);

        CoapResourceGps3 coapResourceGps3 = new CoapResourceGps3(
                gps3.SENSOR_ID,
                "Gps3",
                gps3);

        this.add(coapResourceBattery3);
        this.add(coapResourceGps3);
    }

    public static void main(String[] args){
        Car3CoapProcess car3CoapProcess = new Car3CoapProcess();
        logger.info("Starting CoAP Server...");

        car3CoapProcess.start();

        logger.info("Car3 CoAP server Started! Available resources: ");
        car3CoapProcess.getRoot().getChildren().stream().forEach(resource ->{
            logger.info("Resource : {} URI : {} (Observable :{})",resource.getName(),resource.getURI(),resource.isObservable());
        });
    }
}
