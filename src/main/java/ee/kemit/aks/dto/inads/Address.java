package ee.kemit.aks.dto.inads;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

	private String taisaadress;
	@JsonAlias("ads_oid")
	private String adsOid;
	private String maakond;
	private String omavalitsus;
	@JsonAlias("viitepunkt_x")
	private String viitepunktX;
	@JsonAlias("viitepunkt_y")
	private String viitepunktY;
}
