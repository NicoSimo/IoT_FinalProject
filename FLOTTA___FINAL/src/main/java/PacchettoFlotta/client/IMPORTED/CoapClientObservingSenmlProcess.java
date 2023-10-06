package PacchettoFlotta.client.IMPORTED;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CoapClientObservingSenmlProcess {

    private final static Logger logger = LoggerFactory.getLogger(CoapClientObservingSenmlProcess.class);

    public static void main(String[] args) {
        String targetCoapResourceURL = "coap://127.0.0.1:5681/Battery1";

        CoapClient client = new CoapClient(targetCoapResourceURL);

        logger.info("OBSERVING... {}", targetCoapResourceURL);

        Request request = Request.newGet().setURI(targetCoapResourceURL).setObserve();
        request.setConfirmable(true);
        request.setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_SENML_JSON));

        CoapObserveRelation relation = client.observe(request, new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                logger.info("Notification Response Pretty Print: \n{}", Utils.prettyPrint(response));
                logger.info("NOTIFICATION Body: " + content);
            }

            @Override
            public void onError() {
                logger.error("OBSERVING FAILED");
            }
        });

        // Observes the CoAP resource for 30 seconds, then the observing relation is deleted
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("CANCELLATION.....");
        relation.proactiveCancel();
    }
}

