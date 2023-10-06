package PacchettoFlotta.model.sensors.Fleet;

import PacchettoFlotta.model.models.DataListener;
import PacchettoFlotta.model.models.GPSModel;
import PacchettoFlotta.model.models.SmartObject;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class GPSCar4 extends SmartObject<GPSModel> {

    private static final Logger logger = LoggerFactory.getLogger(GPSCar4.class);

    public static final String RESOURCE_TYPE = "gps.sensor";
    public static final String SENSOR_ID = "iot.sensor.gps4";

    private static final String GPX_FILE_NAME = "tracks/HomeMirandola.gpx";

    public static final long UPDATE_PERIOD = 5000; // MilliSeconds

    private static final long TASK_DELAY_TIME = 5000; //Seconds before starting the periodic update task

    private Timer updateTimer = null;

    private GPSModel updatedGPSModel = null;

    private List<WayPoint> wayPointList = null;

    private ListIterator<WayPoint> wayPointListIterator;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public GPSCar4() {
        super(GPSCar4.RESOURCE_TYPE, GPSCar4.SENSOR_ID);
        init();
    }

    public GPSCar4(String type, String id) {
        super(type, id);
        init();
    }

    private void init(){

        try{
            this.timestamp = System.currentTimeMillis();

            this.updatedGPSModel = new GPSModel();

            this.wayPointList = GPX.read(GPX_FILE_NAME).tracks()
                    .flatMap(Track::segments)
                    .flatMap(TrackSegment::points)
                    .collect(Collectors.toList());

            logger.info("GPX File WayPoint correctly loaded ! Size: {}", this.wayPointList.size());

            this.wayPointListIterator = this.wayPointList.listIterator();

            startPeriodicEventValueUpdateTask();

        }catch(Exception e){
            logger.error("Error init Resource Object ! Msg: {}", e.getLocalizedMessage());
        }

    }

    private void startPeriodicEventValueUpdateTask(){

        try{

            logger.info("Starting periodic Update Task with Period: {} ms", UPDATE_PERIOD);

            this.updateTimer = new Timer();
            this.updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    if(wayPointListIterator.hasNext()){

                        //logger.info("macchina in movimento...");

                        WayPoint currentWayPoint = wayPointListIterator.next();

                        updatedGPSModel = new GPSModel(
                                currentWayPoint.getLatitude().doubleValue(),
                                currentWayPoint.getLongitude().doubleValue(),
                                (currentWayPoint.getElevation().isPresent() ? currentWayPoint.getElevation().get().doubleValue() : 0.0));

                        notifyUpdate(updatedGPSModel);
                        if (currentWayPoint.getLatitude().doubleValue() ==  44.97169 && currentWayPoint.getLongitude().doubleValue() == 11.12772) {
                            try {
                                Thread.sleep(10000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    //At the end of the WayPoint List
                    else{
                        logger.info("{} got to Destination safely", SENSOR_ID);
                        updateTimer.cancel();
                    }

                }
            }, TASK_DELAY_TIME, UPDATE_PERIOD);

        }catch (Exception e){
            logger.error("Error executing periodic resource value ! Msg: {}", e.getLocalizedMessage());
        }
    }

    @Override
    public GPSModel UpdateValue() {
        return updatedGPSModel;
    }

    public static void main(String[] args) {
        GPSCar4 GPS1Resource = new GPSCar4();

        logger.info("New {} Resource Created with Id: {} ! ",
                GPS1Resource.getType(),
                GPS1Resource.getId());

        GPS1Resource.addDataListener(new DataListener<>() {
            @Override
            public void onDataChanged(SmartObject<GPSModel> resource, GPSModel updatedValue) {
                if(resource != null && updatedValue != null) {
                    logger.info("Device: {} -> New Value Received: {}", resource.getId(), updatedValue);
                }
                else {
                    logger.error("onDataChanged Callback -> Null Resource or Updated Value !");
                }
            }
        });
    }
}
