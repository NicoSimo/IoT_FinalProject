package PacchettoFlotta.resource.batteries;

import PacchettoFlotta.model.models.DataListener;
import PacchettoFlotta.model.models.SmartObject;
import PacchettoFlotta.model.sensors.Batteries.Battery2;
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

public class CoapResourceBattery2 extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapResourceBattery2.class);
    private static final String OBJECT_TITLE = "Battery2";
    private static final Double SENSOR_VERSION = 0.1;

    private final String UNIT = "%";
    private Battery2 battery2;
    private String deviceId;
    private ObjectMapper objectMapper;

    private double updatedBatteryLevel = 99;

    public CoapResourceBattery2(String deviceId, String name, Battery2 battery2){
        super(name);

        if (battery2!=null && deviceId!=null){

            this.deviceId = deviceId;
            this.battery2 = battery2;

            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            setObservable(true);
            setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", battery2.getType());
            getAttributes().addAttribute("if", CoreInterfaces.CORE_S.getValue());
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
            //getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

        }else{
            logger.error("Error!");
        }

        this.battery2.addDataListener(new DataListener() {
            @Override
            public void onDataChanged(SmartObject resource, Object updatedValue) {
                updatedBatteryLevel = (double) updatedValue;
                changed(); // ---> metodo di californium che notifica a tutti che il valore è cambiato.
            }
        });
    }

    private Optional<String> getJsonSenmlResponse(){
        try {
            SenMLPack senMLPack = new SenMLPack();

            SenMLRecord senMLRecord = new SenMLRecord();
            senMLRecord.setBn(this.deviceId);
            senMLRecord.setV(updatedBatteryLevel);
            senMLRecord.setU(UNIT);
            senMLRecord.setT(this.battery2.getTimestamp());

            senMLPack.add(senMLRecord);

            return Optional.of(this.objectMapper.writeValueAsString(senMLPack));

        }catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        /*
        exchange.setMaxAge(10);

        logger.info("\nBATTERY 2 : handleGET...");

        logger.info("Request Code : {}",exchange.getRequestCode());
        logger.info("Request Text : {}",exchange.getRequestText());
        logger.info("Request Pretty Print : \n{}", Utils.prettyPrint(exchange.advanced().getRequest()));

        exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(this.updatedBatteryLevel));
        */
        try {
            // the Max-Age value should match the update interval
            exchange.setMaxAge(Battery2.UPDATE_TIME);

            // if the request specify the MediaType as JSON+SenML
            if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_SENML_JSON){

                Optional<String> senmlPayload = getJsonSenmlResponse();

                if (senmlPayload.isPresent())
                    exchange.respond(CoAP.ResponseCode.CONTENT, senmlPayload.get(), exchange.getRequestOptions().getAccept());
                else
                    exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            } else
                exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(updatedBatteryLevel), MediaTypeRegistry.TEXT_PLAIN);

        } catch (Exception e){
            logger.error("Error Handling GET -> {}", e.getLocalizedMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
