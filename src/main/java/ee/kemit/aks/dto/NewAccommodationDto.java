package ee.kemit.aks.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NewAccommodationDto extends NewAddressDto {

	@NotNull
	@Size(max = 500)
	private String name;
}