package PacchettoFlotta.client.IMPORTED;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoapObservingClientProcess {

    private final static Logger logger = LoggerFactory.getLogger(CoapObservingClientProcess.class);

    public static void main(String[] args) {

        String targetCoapResourceURL = "coap://127.0.0.1:5681/Battery1";

        CoapClient client = new CoapClient(targetCoapResourceURL);

        logger.info("OBSERVING ... {}", targetCoapResourceURL);

        Request request = Request.newGet().setURI(targetCoapResourceURL).setObserve();
        request.setConfirmable(true);

        // NOTE:
        // The client.observe(Request, CoapHandler) method visibility has been changed from "private"
        // to "public" in order to get the ability to change the parameter of the observable GET
        //(e.g., to change token and MID).
        CoapObserveRelation relation = client.observe(request, new CoapHandler() {

            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                logger.info("Notification Response Pretty Print: \n{}", Utils.prettyPrint(response));
                logger.info("NOTIFICATION Body: " + content);
            }

            public void onError() {
                logger.error("OBSERVING FAILED");
            }
        });

        // Observes the coap resource for 30 seconds then the observing relation is deleted
        try {
            Thread.sleep(60*3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("CANCELLATION.....");
        relation.proactiveCancel();

    }

}
