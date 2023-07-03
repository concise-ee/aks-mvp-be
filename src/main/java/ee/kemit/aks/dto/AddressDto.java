package ee.kemit.aks.dto;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class AddressDto {

	private String address;
	private boolean active;
	private String adsOid;
	private String centroidX;
	private String centroidY;
	private OffsetDateTime createdAt;
}