package ee.kemit.aks.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewAddressDto {

	@NotNull
	private String adsOid;
}
