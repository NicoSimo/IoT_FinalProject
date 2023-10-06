package PacchettoFlotta.main;

import PacchettoFlotta.client.TargetCoapResourceDescriptor;
import PacchettoFlotta.model.Chargers;
import PacchettoFlotta.model.sensors.Switch.Switch1;
import PacchettoFlotta.model.sensors.Switch.Switch2;
import PacchettoFlotta.model.sensors.Switch.Switch3;
import PacchettoFlotta.model.sensors.Switch.Switch4;
import PacchettoFlotta.utils.CoreInterfaces;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


public class DataManager {

    private final static Logger logger = LoggerFactory.getLogger(DataManager.class);

    private static Switch1 switch1 = new Switch1();
    private static Switch2 switch2 = new Switch2();
    private static Switch3 switch3 = new Switch3();
    private static Switch4 switch4 = new Switch4();

    private static final String SWITCH_PATH1 = "coap://127.0.0.1:5685/Switch1";
    private static final String SWITCH_PATH2 = "coap://127.0.0.1:5685/Switch2";
    private static final String SWITCH_PATH3 = "coap://127.0.0.1:5685/Switch3";
    private static final String SWITCH_PATH4 = "coap://127.0.0.1:5685/Switch4";

    private static final String TARGET_SMART_OBJECT_ADDRESS = "127.0.0.1";

    private static final String WELL_KNOWN_CORE_URI = "/.well-known/core";

    private static final String OBSERVABLE_CORE_ATTRIBUTE = "obs";

    private static final String INTERFACE_CORE_ATTRIBUTE = "if";

    private static final String CONTENT_TYPE_ATTRIBUTE = "ct";

    private static List<TargetCoapResourceDescriptor> targetObservableResourceList = null;

    private static Map<String, CoapObserveRelation> observingRelationMap = null;

    private static void discoverTargetObservableResources(CoapClient coapClient, int smartObjPort){

        Request request = new Request(CoAP.Code.GET);

        request.setURI(String.format("coap://%s:%d%s",
                TARGET_SMART_OBJECT_ADDRESS,
                smartObjPort,
                WELL_KNOWN_CORE_URI));

        request.setConfirmable(true);

        logger.info("Request Pretty Print: \n{}", Utils.prettyPrint(request));

        CoapResponse coapResp = null;

        try {
            coapResp = coapClient.advanced(request);

            if(coapResp != null) {

                logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

                if (coapResp.getOptions().getContentFormat() == MediaTypeRegistry.APPLICATION_LINK_FORMAT) {

                    Set<WebLink> links = LinkFormat.parse(coapResp.getResponseText());

                    links.forEach(link -> {

                        if(link.getURI() != null
                                && !link.getURI().equals(WELL_KNOWN_CORE_URI)
                                && link.getAttributes() != null
                                && link.getAttributes().getCount() > 0){

                            //If the resource is a core.s or core.a and it is observable save the target url reference
                            if(link.getAttributes().containsAttribute(OBSERVABLE_CORE_ATTRIBUTE) &&
                                    link.getAttributes().containsAttribute(INTERFACE_CORE_ATTRIBUTE) &&
                                    (link.getAttributes().getAttributeValues(INTERFACE_CORE_ATTRIBUTE).get(0).equals(CoreInterfaces.CORE_S.getValue()) || link.getAttributes().getAttributeValues(INTERFACE_CORE_ATTRIBUTE).get(0).equals(CoreInterfaces.CORE_A.getValue()))){

                                boolean supportSenml = false;

                                if(link.getAttributes().containsAttribute(CONTENT_TYPE_ATTRIBUTE))
                                    supportSenml = link.getAttributes().getAttributeValues(CONTENT_TYPE_ATTRIBUTE).contains("110");

                                logger.info("Target resource found ! URI: {}} (Senml: {})", link.getURI(), supportSenml);

                                String targetResourceUrl = String.format("coap://%s:%d%s",
                                        TARGET_SMART_OBJECT_ADDRESS,
                                        smartObjPort,
                                        link.getURI());

                                targetObservableResourceList.add(new TargetCoapResourceDescriptor(targetResourceUrl, supportSenml));

                                logger.info("Target Resource URL: {} correctly saved !", targetResourceUrl);
                            }
                            else
                                logger.info("Resource {} does not match filtering parameters ....", link.getURI());
                        }
                    });
                } else {
                    logger.error("CoRE Link Format Response not found !");
                }
            }
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void startObservingTargetResource(CoapClient coapClient, String url, boolean useSenml) {

        logger.info("OBSERVING ... {}", url);
        Request request = Request.newGet();

        if(useSenml)
            request.setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_SENML_JSON));

        request.setObserve();
        request.setConfirmable(true);
        request.setURI(url);

        CoapObserveRelation relation = coapClient.observe(request, new CoapHandler() {

            public void onLoad(CoapResponse response) {
                logger.info("Notification Response pretty print: \n{}", Utils.prettyPrint(response));

                // Parse della stringa per ottenere il valore, cioè "v"
                // per fare un check sul valore

                String responseText = response.getResponseText();
                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(responseText, JsonArray.class);

                // This works for the batteries
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                JsonElement elementBattery = jsonObject.get("v");

                // check on the Url, without this check the code was producing warning caused by the
                // batteries JSON not having the .get(1) --> (OutOfBounds)

                if (url.contains("/Gps")) {
                    double latitude = jsonArray.get(1).getAsJsonObject().get("v").getAsDouble();
                    double longitude = jsonArray.get(2).getAsJsonObject().get("v").getAsDouble();

                    if (latitude == 45.1599164 && longitude == 10.7884993) {
                        CoapObserveRelation relationBattery = observingRelationMap.get("coap://127.0.0.1:5681/Battery1");
                        CoapObserveRelation relationGps = observingRelationMap.get("coap://127.0.0.1:5681/Gps1");

                        if (relationBattery != null && relationGps != null) {
                            logger.info("Cancelling Observation for URL: {}", url);
                            relationBattery.proactiveCancel();
                            relationGps.proactiveCancel();
                        }
                    }
                    else if (latitude == 45.0686584 && longitude == 11.0217822) {
                        CoapObserveRelation relationBattery = observingRelationMap.get("coap://127.0.0.1:5682/Battery2");
                        CoapObserveRelation relationGps = observingRelationMap.get("coap://127.0.0.1:5682/Gps2");

                        if (relationBattery != null && relationGps != null) {
                            logger.info("Cancelling Observation for URL: {}", url);
                            relationBattery.proactiveCancel();
                            relationGps.proactiveCancel();
                        }
                    }
                    else if (latitude == 45.1873435 && longitude == 11.2718372) {
                        CoapObserveRelation relationBattery = observingRelationMap.get("coap://127.0.0.1:5683/Battery3");
                        CoapObserveRelation relationGps = observingRelationMap.get("coap://127.0.0.1:5683/Gps3");

                        if (relationBattery != null && relationGps != null) {
                            logger.info("Cancelling Observation for URL: {}", url);
                            relationBattery.proactiveCancel();
                            relationGps.proactiveCancel();
                        }
                    }
                    else if (latitude == 44.8863708 && longitude == 11.0634731) {
                        CoapObserveRelation relationBattery = observingRelationMap.get("coap://127.0.0.1:5684/Battery4");
                        CoapObserveRelation relationGps = observingRelationMap.get("coap://127.0.0.1:5684/Gps4");

                        if (relationBattery != null && relationGps != null) {
                            logger.info("Cancelling Observation for URL: {}", url);
                            relationBattery.proactiveCancel();
                            relationGps.proactiveCancel();
                        }
                    }
                }

                if (elementBattery != null && !elementBattery.isJsonNull()){
                    double value = jsonObject.get("v").getAsDouble();

                    // OFF --> ON
                    if (value <= 20) {
                        Thread thread = new Thread(() -> {

                            if (url.contains("Battery1")) {
                                if (!switch1.isValue()) {

                                    logger.info("Batteria scarica, dirigersi verso la colonnina più vicina.\nCoordinate : {}", Chargers.chargerMN.getPosition());
                                    CoapClient postclient = new CoapClient(SWITCH_PATH1);
                                    PostSwitch(postclient);
                                    //switch1.switchStatusOn();
                                }
                            }

                            if (url.contains("Battery2")) {
                                if (!switch2.isValue()) {

                                    logger.info("Batteria scarica, dirigersi verso la colonnina più vicina.\nCoordinate : {}", Chargers.chargerSUSTI.getPosition());
                                    CoapClient postclient = new CoapClient(SWITCH_PATH2);
                                    PostSwitch(postclient);
                                    //switch2.switchStatusOn();
                                }
                            }

                            if (url.contains("Battery3")) {
                                if (!switch3.isValue()) {

                                    logger.info("Batteria scarica, dirigersi verso la colonnina più vicina.\nCoordinate : {}", Chargers.chargerCEREA.getPosition());
                                    CoapClient postclient = new CoapClient(SWITCH_PATH3);
                                    PostSwitch(postclient);
                                    //switch3.switchStatusOn();
                                }
                            }

                            if (url.contains("Battery4")) {
                                if (!switch4.isValue()) {

                                    logger.info("Batteria scarica, dirigersi verso la colonnina più vicina.\nCoordinate : {}", Chargers.chargerMIRA.getPosition());
                                    CoapClient postclient = new CoapClient(SWITCH_PATH4);
                                    PostSwitch(postclient);
                                    //switch4.switchStatusOn();
                                }
                            }

                            // Kill the thread
                            Thread.currentThread().interrupt();
                        });
                        thread.start();
                    }

                    // ON --> OFF
                    if (value >= 100) {
                        Thread thread = new Thread(() -> {

                            if (url.contains("Battery1")) {
                                logger.info("Ricarica Terminata");
                                CoapClient postclient = new CoapClient(SWITCH_PATH1);
                                PostSwitch(postclient);
                                //switch1.switchStatusOff();
                            }

                            if (url.contains("Battery2")) {
                                logger.info("Ricarica Terminata");
                                CoapClient postclient = new CoapClient(SWITCH_PATH2);
                                PostSwitch(postclient);
                                //switch2.switchStatusOff();
                            }

                            if (url.contains("Battery3")) {
                                logger.info("Ricarica Terminata");
                                CoapClient postclient = new CoapClient(SWITCH_PATH3);
                                PostSwitch(postclient);
                                //switch3.switchStatusOff();

                            }

                            if (url.contains("Battery4")) {
                                logger.info("Ricarica Terminata");
                                CoapClient postclient = new CoapClient(SWITCH_PATH4);
                                PostSwitch(postclient);
                                //switch4.switchStatusOff();

                            }

                            // Kill the thread
                            Thread.currentThread().interrupt();
                        });
                        thread.start();
                    }
                }
            }
            public void onError() { logger.error("OBSERVING {} FAILED", url);}
        });
        observingRelationMap.put(url, relation);
    }

    private static void PostSwitch(CoapClient coapClient) {

        Request request = new Request(CoAP.Code.POST);
        request.setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_SENML_JSON));

        logger.info("Request Pretty Print:\n{}", Utils.prettyPrint(request));
        CoapResponse coapResp;

        try {
            coapResp = coapClient.advanced(request);
            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        CoapClient client = new CoapClient();
        CoapClient switchclient = new CoapClient();

        targetObservableResourceList = new ArrayList<>();
        observingRelationMap = new HashMap<>();

        discoverTargetObservableResources(client, 5681);
        discoverTargetObservableResources(client, 5682);
        discoverTargetObservableResources(client, 5683);
        discoverTargetObservableResources(client, 5684);
        discoverTargetObservableResources(switchclient, 5685);

        targetObservableResourceList.forEach(targetResource -> {
            startObservingTargetResource(client, targetResource.getTargetUrl(), targetResource.getSenmlSupport());
        });

        /*

        try {
            Thread.sleep(60*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        observingRelationMap.forEach((key, value) -> {
            logger.info("Canceling Observation for target Url: {}", key);
            value.proactiveCancel();
        });
                 */

    }
}