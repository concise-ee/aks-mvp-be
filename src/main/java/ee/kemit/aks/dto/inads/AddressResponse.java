package ee.kemit.aks.dto.inads;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressResponse {

	private List<Address> addresses = new ArrayList<>();
}
