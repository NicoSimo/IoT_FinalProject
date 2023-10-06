package PacchettoFlotta.client;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoapObserveGPS {

    private static final Logger logger = LoggerFactory.getLogger(CoapObserveGPS.class);
    private static final String COAP_ENDPOINT = "coap://127.0.0.1:5683/GpsTestName";


    public static void main(String[] args) {
        CoapClient client = new CoapClient(COAP_ENDPOINT);

        logger.info("OBSERVING... {}",COAP_ENDPOINT);

        Request request = Request.newGet().setURI(COAP_ENDPOINT).setObserve();
        request.setConfirmable(true);

        CoapObserveRelation relation = client.observe(request, new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                logger.info("Notification Response pretty print: \n{}", Utils.prettyPrint(response));
                logger.info("Current location "+content);
            }

            @Override
            public void onError() {
                logger.error("Observation failed");
            }
        });
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("Cancellation...");
            relation.proactiveCancel();
        }
    }
}
