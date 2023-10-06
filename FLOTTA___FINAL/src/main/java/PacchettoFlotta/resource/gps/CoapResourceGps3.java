package PacchettoFlotta.resource.gps;

import PacchettoFlotta.model.models.DataListener;
import PacchettoFlotta.model.models.SmartObject;
import PacchettoFlotta.model.sensors.Fleet.GPSCar3;
import PacchettoFlotta.utils.CoreInterfaces;
import PacchettoFlotta.utils.SenMLPack;
import PacchettoFlotta.utils.SenMLRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

//Problema da risolvere. Battery aveva 1 solo numero da restituire, qui 2/3 (x,y,z).
//UPDATE AL 16/03. ----- Parrebbe fixato.

public class CoapResourceGps3 extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapResourceGps3.class);
    private static final String OBJECT_TITLE = "GPS3";
    private static final Double SENSOR_VERSION = 0.1;

    private Gson gson;
    private GPSCar3 gps3;
    private String deviceId;
    private ObjectMapper objectMapper;

    private Object updatedGpsCoordinates = 0.0;
    //doppio valore ?

    public CoapResourceGps3(String deviceId, String name, GPSCar3 gps3) {
        super(name);
        if (gps3 != null && deviceId != null) {
            this.gson = new Gson();

            this.gps3 = gps3;
            this.deviceId = deviceId;

            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            setObservable(true);
            setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", gps3.getType());
            getAttributes().addAttribute("if", CoreInterfaces.CORE_S.getValue());
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
            //getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

        } else {
            logger.error("Error occured in gps3");
        }

        this.gps3.addDataListener(new DataListener() {
            @Override
            public void onDataChanged(SmartObject resource, Object updatedValue) {
                updatedGpsCoordinates = updatedValue;
                changed(); // ---> metodo di californium che notifica a tutti che il valore è cambiato.
            }
        });
    }

    private Optional<String> getJsonSenmlResponse() {

        try {

            SenMLPack senMLPack = new SenMLPack();

            SenMLRecord baseRecord = new SenMLRecord();
            baseRecord.setBn(deviceId);
            baseRecord.setT(this.gps3.getTimestamp());

            SenMLRecord measureRecordX = new SenMLRecord();
            measureRecordX.setN("Latitude");
            measureRecordX.setV(this.gps3.UpdateValue().getLatitude());

            SenMLRecord measureRecordY = new SenMLRecord();
            measureRecordY.setN("Longitude");
            measureRecordY.setV(this.gps3.UpdateValue().getLongitude());

            SenMLRecord measureRecordZ = new SenMLRecord();
            measureRecordZ.setN("Altitude");
            measureRecordZ.setV(this.gps3.UpdateValue().getElevation());

            senMLPack.add(baseRecord);
            senMLPack.add(measureRecordX);
            senMLPack.add(measureRecordY);

            return Optional.of(this.gson.toJson(senMLPack));

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        try {
            // the Max-Age value should match the update interval
            exchange.setMaxAge(GPSCar3.UPDATE_PERIOD);

            //If the request specify the MediaType as JSON+SenML
            if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_SENML_JSON){

                Optional<String> senmlPayload = getJsonSenmlResponse();

                if (senmlPayload.isPresent())
                    exchange.respond(CoAP.ResponseCode.CONTENT, senmlPayload.get(), exchange.getRequestOptions().getAccept());
                else
                    exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            } else
                exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(updatedGpsCoordinates), MediaTypeRegistry.TEXT_PLAIN);

        } catch (Exception e) {
            logger.error("Error Handling GET -> {}", e.getLocalizedMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}