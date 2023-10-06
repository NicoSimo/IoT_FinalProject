package PacchettoFlotta.resource.Switch;

import PacchettoFlotta.model.models.DataListener;
import PacchettoFlotta.model.models.SmartObject;
import PacchettoFlotta.model.sensors.Switch.Switch1;
import PacchettoFlotta.utils.CoreInterfaces;
import PacchettoFlotta.utils.SenMLPack;
import PacchettoFlotta.utils.SenMLRecord;
import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CoapResourceSwitch1 extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapResourceSwitch1.class);
    private static final String OBJECT_TITLE = "Switch1";
    private Gson gson;
    private final Switch1 switchRawActuator;

    public CoapResourceSwitch1(String name, Switch1 switchRawActuator) {
        super(name);
        this.switchRawActuator = switchRawActuator;

        if (switchRawActuator != null && switchRawActuator.getId() != null) {
            init();
        } else {
            logger.error("Error -> NULL Raw Reference !");
        }
    }

    public void init() {
        this.gson = new Gson();

        getAttributes().setTitle(OBJECT_TITLE);
        getAttributes().addAttribute("rt", "PacchettoFlotta.switchSecure");
        getAttributes().addAttribute("if", CoreInterfaces.CORE_A.getValue());
        getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
        getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

        switchRawActuator.addDataListener(new DataListener<Boolean>() {
            @Override
            public void onDataChanged(SmartObject<Boolean> resource, Boolean updatedValue) {
                //logger.info("Stato dello switch modificato ! New Value: {}", updatedValue);

                changed();
            }
        });
    }

    private Optional<String> getJsonSenmlResponse(){

        try{

            SenMLPack senMLPack = new SenMLPack();
            SenMLRecord senMLRecord = new SenMLRecord();

            senMLRecord.setBn(this.switchRawActuator.getId());
            senMLRecord.setT(this.switchRawActuator.getTimestamp());
            senMLRecord.setVb(this.switchRawActuator.isValue());

            senMLPack.add(senMLRecord);

            return Optional.of(this.gson.toJson(senMLPack));

        }catch (Exception e){
            logger.error("Exception occurred while constructing SenML response:", e);
            return Optional.empty();
        }
    }

    @Override
    public void handlePOST(CoapExchange exchange) {

        if (this.switchRawActuator.isValue() == false)
            this.switchRawActuator.switchStatusOn();
        else if(this.switchRawActuator.isValue() == true)
            this.switchRawActuator.switchStatusOff();

        if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_SENML_JSON || exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON )
        {
            Optional<String> senmlPayload = getJsonSenmlResponse();

            if (senmlPayload.isPresent())
                exchange.respond(CoAP.ResponseCode.CHANGED, senmlPayload.get(), exchange.getRequestOptions().getAccept());
            else
                exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
        else
            exchange.respond(CoAP.ResponseCode.CHANGED, String.valueOf(switchRawActuator.isValue()), MediaTypeRegistry.TEXT_PLAIN);
    }
}
/*
        try {
            logger.info("Request pretty print:\n{}", Utils.prettyPrint(exchange.advanced().getRequest()));
            logger.info("Received POST request with body {}",exchange.getRequestText());
            if (!this.switchRawActuator.isValue()) {
                this.switchRawActuator.switchStatusOn();
            }
            logger.info("Switch status : {}", this.switchRawActuator.isValue());
            exchange.respond(CoAP.ResponseCode.CHANGED, String.valueOf(getJsonSenmlResponse()));

            changed();

        } catch (Exception e) {
            logger.error("Error Handling POST -> {}", e.getLocalizedMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
 */
