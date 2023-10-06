package PacchettoFlotta.model.sensors.Batteries;

import PacchettoFlotta.model.models.DataListener;
import PacchettoFlotta.model.models.SmartObject;
import PacchettoFlotta.model.sensors.Switch.Switch1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class Battery1 extends SmartObject {

    private static final Logger logger = LoggerFactory.getLogger(Battery1.class);

    private long timestamp;
    public static final String RESOURCE_TYPE = "battery.sensor";
    public static final String SENSOR_ID = "iot.sensor.battery1";

    //Valori di range Max e Min di batteria e valori di Max e Min variazione.

    public long getTimestamp() {
        return timestamp;
    }

    private static final int MAX_BatteryLevel = 99;

    private boolean dest1 = false;

    Switch1 switch1 = new Switch1();
    //Variabili utili per inizializzare valori o metodi nelle funzioni
    private Timer UpdateTimer;

    public static final long UPDATE_TIME = 59300;  // 18000 t in ms

    private double BatteryLevel;

    public Battery1() {
        super(Battery1.RESOURCE_TYPE, Battery1.SENSOR_ID);
        init();
    }

    public Battery1(String type, String id) {
        super(type, id);
        init();
    }

    //funzione per randomizzare valore della batteria, notare che ^^^^^ infatti la funzione è chiamata in entrambi i costruttori

    private void init(){
        try {
            this.timestamp = System.currentTimeMillis();
            this.BatteryLevel = MAX_BatteryLevel;

            StartTimer();

        }catch (Exception e){
            logger.error("Error init BatterySensor! Message {}", e.getLocalizedMessage());
        }
    }

    //viene chiamato nella init ^^^
    //Effettivamente è qua dove viene modificato il livello della batteria... riga 65
    private void StartTimer(){
        try {
            logger.info("BatterySensor Timer started. (Timer = {} ms )",UPDATE_TIME);

            this.UpdateTimer= new Timer();
            this.UpdateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(switch1.isValue()) {
                        if (dest1){
                            UpdateTimer.cancel();
                        }
                        logger.info("Batteria in ricarica! ");

                        BatteryLevel = BatteryLevel + 5;
                        //BatteryLevel = BatteryLevel + (MIN_VARIATION + MAX_VARIATION * random.nextDouble());

                        if (BatteryLevel >= MAX_BatteryLevel) {

                            logger.info("Ricarica terminata! ");
                            switch1.switchStatusOff();
                            BatteryLevel = MAX_BatteryLevel;
                            dest1 = true;
                        }

                    }else {

                        BatteryLevel = BatteryLevel - 1;
                        //BatteryLevel = BatteryLevel - (MIN_VARIATION + MAX_VARIATION * random.nextDouble());

                        if (BatteryLevel < 20) {
                            logger.info("Batteria quasi scarica! ");
                            if (BatteryLevel <= 0) {
                                BatteryLevel = 0;

                                switch1.switchStatusOn();
                            }
                        }
                        /*else if (BatteryLevel >= MAX_BatteryLevel) {

                            dest = true;
                            BatteryLevel = MAX_BatteryLevel;
                            switch1.switchStatusOff();
                        }
*/
                    }
                    notifyUpdate(BatteryLevel);

                }
            },UPDATE_TIME,UPDATE_TIME);
//UPDATE_TIME,
        }catch (Exception e){
            logger.error("BatterySensor Timer error");
        }
    }

    @Override
    public Double UpdateValue() {
        return this.BatteryLevel;
    }

    //Qui nel main chiamo il metodo UpdateValue dell'oggetto BatterySensor (che eredita da padre SmartObject) ^^^
    public static void main(String[] args){
        Battery1 batterySensor = new Battery1();
        logger.info("New BatterySensor created. Battery level: {}",batterySensor.UpdateValue());

        batterySensor.addDataListener(new DataListener() {
            @Override
            public void onDataChanged(SmartObject resource, Object updatedValue) {
                if(resource != null && updatedValue != null){
                    logger.info("Device {} -> new battery level : {}", resource.getId(), updatedValue);
                    if((Double) updatedValue <= 0){
                        logger.info("Batteria scarica...");
                    }
                }else{
                    logger.error("Null resource or null update value");
                }
            }
        });
    }
}
