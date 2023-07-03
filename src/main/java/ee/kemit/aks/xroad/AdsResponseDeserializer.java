package ee.kemit.aks.xroad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.niis.xrd4j.client.deserializer.AbstractResponseDeserializer;

import ee.xroad.ads.ADSobjmuudatusedResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AdsResponseDeserializer
		extends AbstractResponseDeserializer<AdsObjektRequestDto, ADSobjmuudatusedResponse> {

	public SOAPMessage stringToMessage(String soap) throws IOException, SOAPException {
		InputStream inputStream = new ByteArrayInputStream(soap.getBytes());
		return MessageFactory.newInstance().createMessage(null, inputStream);
	}

	public String messageToString(SOAPMessage soap) throws SOAPException, IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soap.writeTo(stream);
		return stream.toString(StandardCharsets.UTF_8);
	}

	@Override
	protected AdsObjektRequestDto deserializeRequestData(Node node) {
		return null;
	}

	@Override
	protected ADSobjmuudatusedResponse deserializeResponseData(Node node, SOAPMessage soapMessage) {
		try {
			this.debug(soapMessage);
			JAXBContext jaxbContext = JAXBContext.newInstance(ADSobjmuudatusedResponse.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			SOAPMessage clonedMessage;
			clonedMessage = stringToMessage(messageToString(soapMessage));

			JAXBElement<ADSobjmuudatusedResponse> muudatused = unmarshaller.unmarshal(clonedMessage.getSOAPBody()
					.extractContentAsDocument(), ADSobjmuudatusedResponse.class);

			return muudatused.getValue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private void debug(SOAPMessage soapMessage) throws SOAPException, IOException {
		java.io.ByteArrayOutputStream baosHolder = new java.io.ByteArrayOutputStream();
		soapMessage.writeTo(baosHolder);
		String strSOAPMsg = baosHolder.toString().replaceAll("\n+", "").replaceAll("\r+", "").replaceAll("\t+", "");

		log.info(" ------ INCOMING XML --------");
		log.info(strSOAPMsg);
	}
}