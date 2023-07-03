package ee.kemit.aks.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class AccommodationDto {

	private Long id;
	private String name;
	private Set<AddressDto> addresses = new HashSet<>();
}