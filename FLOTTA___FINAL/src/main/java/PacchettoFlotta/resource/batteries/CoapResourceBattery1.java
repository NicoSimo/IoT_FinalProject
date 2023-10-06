package PacchettoFlotta.resource.batteries;

import PacchettoFlotta.model.models.DataListener;
import PacchettoFlotta.model.models.SmartObject;
import PacchettoFlotta.model.sensors.Batteries.Battery1;
import PacchettoFlotta.utils.CoreInterfaces;
import PacchettoFlotta.utils.SenMLPack;
import PacchettoFlotta.utils.SenMLRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CoapResourceBattery1 extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapResourceBattery1.class);
    private static final String OBJECT_TITLE = "Battery1";
    private ObjectMapper objectMapper;

    private final String UNIT = "%";
    private Battery1 battery1;
    private String deviceId;

    private double updatedBatteryLevel = 99;

    public CoapResourceBattery1(String deviceId, String name, Battery1 battery1) {
        super(name);

        if (battery1 != null && deviceId != null) {

            this.deviceId = deviceId;
            this.battery1 = battery1;

            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            setObservable(true);
            setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", battery1.getType());
            getAttributes().addAttribute("if", CoreInterfaces.CORE_S.getValue());
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
            //getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

        } else {
            logger.error("Error!");
        }

        this.battery1.addDataListener(new DataListener() {
            @Override
            public void onDataChanged(SmartObject resource, Object updatedValue) {
                updatedBatteryLevel = (double) updatedValue;
                changed(); // ---> metodo di californium che notifica a tutti che il valore è cambiato.
            }
        });
    }

    private Optional<String> getJsonSenmlResponse() {

        try {
            SenMLPack senMLPack = new SenMLPack();

            SenMLRecord senMLRecord = new SenMLRecord();
            senMLRecord.setBn(this.deviceId);
            senMLRecord.setV(updatedBatteryLevel);
            senMLRecord.setU(UNIT);
            senMLRecord.setT(this.battery1.getTimestamp());

            senMLPack.add(senMLRecord);

            return Optional.of(this.objectMapper.writeValueAsString(senMLPack));

        } catch (Exception e) {
            logger.error("Exception occurred while constructing SenML response:", e);

            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        // the Max-Age value should match the update interval
        exchange.setMaxAge(Battery1.UPDATE_TIME);

        //If the request specify the MediaType as JSON+SenML
        if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_SENML_JSON || exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON )
        {

            Optional<String> senmlPayload = getJsonSenmlResponse();

            if (senmlPayload.isPresent())
                exchange.respond(CoAP.ResponseCode.CONTENT, senmlPayload.get(), exchange.getRequestOptions().getAccept());
            else
                exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }

        //Otherwise respond with the default textplain payload
        else

            exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(updatedBatteryLevel), MediaTypeRegistry.TEXT_PLAIN);

    }
}

