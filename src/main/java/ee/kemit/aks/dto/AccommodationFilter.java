package ee.kemit.aks.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AccommodationFilter {

	private String searchString;
	private Long countyId;
	private Long municipalityId;
	private LocalDate createdAt;
	private String searchString;
}
