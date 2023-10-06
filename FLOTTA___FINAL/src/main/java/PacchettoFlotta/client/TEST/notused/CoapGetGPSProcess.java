package PacchettoFlotta.client.TEST.notused;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CoapGetGPSProcess {
    private static final Logger logger = LoggerFactory.getLogger(CoapGetGPSProcess.class);
    private static final String COAP_ENDPOINT = "coap://127.0.0.1:5683/GpsTestName";

    public static void main(String[] args) {
        CoapClient coapClient = new CoapClient(COAP_ENDPOINT);

        Request request = new Request(CoAP.Code.GET);

        request.setConfirmable(true);

        logger.info("Request pretty print : \n{}", Utils.prettyPrint(request));

        CoapResponse coapResp = null;

        try{
            coapResp = coapClient.advanced(request);
            logger.info("Request pretty print : \n{}", Utils.prettyPrint(coapResp));

            String text = coapResp.getResponseText();
            logger.info("Payload : {}",text);
            logger.info("Message ID : " + coapResp.advanced().getMID());
            logger.info("Token " + coapResp.advanced().getTokenString());

        }catch (ConnectorException | IOException e){
            e.printStackTrace();
        }
    }
}
