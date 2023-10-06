package PacchettoFlotta.model;

import PacchettoFlotta.model.models.ChargingStations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;

public class Chargers extends ChargingStations {

    private static final Logger logger = LoggerFactory.getLogger(Chargers.class);
    public static ChargingStations chargerMN =
            new ChargingStations("Charger-01", "MantovaNord", new double[]{45.09097,10.8576});

    public static ChargingStations chargerSUSTI =
            new ChargingStations("Charger-02", "Sustinente", new double[]{45.06075,11.05413});

    public static ChargingStations chargerCEREA =
            new ChargingStations("Charger-03", "Cerea", new double[]{45.17261,11.1973});

    public static ChargingStations chargerMIRA =
            new ChargingStations("Charger-04", "Mirandola", new double[]{44.97169,11.12772});

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PositionRawSensor{");
        sb.append("uuid='").append(getID()).append('\'');
        sb.append(", position=").append(Arrays.toString(getPosition()));
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {

        logger.info("New Resource created with Id: {} ! {} Value : {}",
                chargerMN.getID(),
                "ChargingStationPositionSensor",
                chargerMN.getPosition());

        logger.info("New Resource created with Id: {} ! {} Value : {}",
                chargerSUSTI.getID(),
                "ChargingStationPositionSensor",
                chargerSUSTI.getPosition());

        logger.info("New Resource created with Id: {} ! {} Value : {}",
                chargerCEREA.getID(),
                "ChargingStationPositionSensor",
                chargerCEREA.getPosition());

        logger.info("New Resource created with Id: {} ! {} Value : {}",
                chargerMIRA.getID(),
                "ChargingStationPositionSensor",
                chargerMIRA.getPosition());

      }
}
