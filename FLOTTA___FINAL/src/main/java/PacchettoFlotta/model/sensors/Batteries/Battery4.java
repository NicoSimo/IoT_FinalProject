package PacchettoFlotta.model.sensors.Batteries;

import PacchettoFlotta.model.models.DataListener;
import PacchettoFlotta.model.models.SmartObject;
import PacchettoFlotta.model.sensors.Switch.Switch4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class Battery4 extends SmartObject {

    private static final Logger logger = LoggerFactory.getLogger(Battery4.class);

    public static final String RESOURCE_TYPE = "battery.sensor";
    public static final String SENSOR_ID = "iot.sensor.battery4";

    //Valori di range Max e Min di batteria e valori di Max e Min variazione.

    private static final int MAX_BatteryLevel = 99;

    private boolean dest4 = false;

    Switch4 switch4 = new Switch4();
    //Variabili utili per inizializzare valori o metodi nelle funzioni
    private Timer UpdateTimer;

    public static final long UPDATE_TIME = 33850;  // t in ms

    private double BatteryLevel;

    public long getTimestamp() {
        return timestamp;
    }

    private long timestamp;

    public Battery4() {
        super(Battery4.RESOURCE_TYPE, Battery4.SENSOR_ID);
        init();
    }

    public Battery4(String type, String id) {
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

                    if(switch4.isValue()) {
                        if (dest4 == true){
                            UpdateTimer.cancel();
                        }
                        logger.info("Batteria in ricarica! ");

                        BatteryLevel = BatteryLevel + 5;
                        //BatteryLevel = BatteryLevel + (MIN_VARIATION + MAX_VARIATION * random.nextDouble());

                        if (BatteryLevel >= MAX_BatteryLevel) {

                            logger.info("Ricarica terminata! ");
                            switch4.switchStatusOff();
                            BatteryLevel = MAX_BatteryLevel;
                            dest4 = true;
                        }

                    }else {
                        BatteryLevel = BatteryLevel - 1;
                        //BatteryLevel = BatteryLevel - (MIN_VARIATION + MAX_VARIATION * random.nextDouble());

                        if (BatteryLevel < 20) {
                            logger.info("Batteria quasi scarica! ");
                            if (BatteryLevel <= 0) {
                                BatteryLevel = 0;

                                switch4.switchStatusOn();
                            }
                        }

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
        Battery4 batterySensor = new Battery4();
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
