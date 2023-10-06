package PacchettoFlotta.resource.gps;

import PacchettoFlotta.model.models.DataListener;
import PacchettoFlotta.model.models.SmartObject;
import PacchettoFlotta.model.sensors.Fleet.GPSCar1;
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

public class CoapResourceGps1 extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapResourceGps1.class);
    private static final String OBJECT_TITLE = "GPS1";

    private Gson gson;
    private GPSCar1 gps1;
    private String deviceId;
    private ObjectMapper objectMapper;

    private Object updatedGpsCoordinates = 0.0;
    //doppio valore ?


    public CoapResourceGps1(String deviceId, String name, GPSCar1 gps1) {
        super(name);
        if (gps1 != null && deviceId != null) {
            this.gson = new Gson();

            this.gps1 = gps1;
            this.deviceId = deviceId;

            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            setObservable(true);
            setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", gps1.getType());
            getAttributes().addAttribute("if", CoreInterfaces.CORE_S.getValue());
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
            //getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

        } else {
            logger.error("Error occured in gps1");
        }

        this.gps1.addDataListener(new DataListener() {
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
            baseRecord.setT(this.gps1.getTimestamp());

            SenMLRecord measureRecordX = new SenMLRecord();
            measureRecordX.setN("Latitude");
            measureRecordX.setV(this.gps1.UpdateValue().getLatitude());

            SenMLRecord measureRecordY = new SenMLRecord();
            measureRecordY.setN("Longitude");
            measureRecordY.setV(this.gps1.UpdateValue().getLongitude());

            SenMLRecord measureRecordZ = new SenMLRecord();
            measureRecordZ.setN("Altitude");
            measureRecordZ.setV(this.gps1.UpdateValue().getElevation());

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
            exchange.setMaxAge(GPSCar1.UPDATE_PERIOD);

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