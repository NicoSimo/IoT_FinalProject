package PacchettoFlotta.client;

import PacchettoFlotta.server.Car1CoapProcess;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class CoapPostSwitchProcess {

	private final static Logger logger = LoggerFactory.getLogger(CoapPostSwitchProcess.class);

	private static final String COAP_ENDPOINT = "coap://127.0.0.1:5681/Switch1";

	public static void main(String[] args) {
		
		//Initialize coapClient
		CoapClient coapClient = new CoapClient(COAP_ENDPOINT);

		//"Message ID", "Token" and other header's fields can be set
		Request request = new Request(Code.POST);

		request.setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_JSON));

		// effettivamente funziona anche... però la colonnina dovrebbe darla il server, non il client...
		// il client al massimo potrebbe dare la posizione
		// request.setPayload(String.valueOf(Chargers.chargerMN.getLatitude()+" , "+Chargers.chargerMN.getLongitude()));

		//request.setPayload(String.valueOf(Car1CoapProcess.gps1.UpdateValue()));
		//request.setPayload("Piccolo test ... il valore è settato qua in CoapPostSwitchProcess? ");

		// per 0.0 il body ritorna [48 46 48]...
		// codice ASCII per i diversi simboli... --> man ascii (decimal)

		logger.info("Request Pretty Print:\n{}", Utils.prettyPrint(request));

		//Synchronously send the POST request (blocking call)
		CoapResponse coapResp = null;

		try {

			coapResp = coapClient.advanced(request);

			//Pretty print for the received response
			logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

			//The "CoapResponse" message contains the response.
			String text = coapResp.getResponseText();
			logger.info("Payload: {}", text);
			logger.info("Message ID: " + coapResp.advanced().getMID());
			logger.info("Token: " + coapResp.advanced().getTokenString());

		} catch (ConnectorException | IOException e) {
			e.printStackTrace();
		}
	}
}