package ee.kemit.aks.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ee.kemit.aks.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findAllByAccommodation_IdOrderByCreatedAtDesc(Long id);

	@Query("SELECT a.adsOid from Address a WHERE a.active = true")
	List<String> findAllActiveAdsOidAddresses();

	List<Address> findByAdsOidAndActiveTrue(String adsOid);

	@Query("from Address adr WHERE adr.active = TRUE "
			+ "AND (:searchString IS NULL OR lower(adr.accommodation.name) LIKE %:searchString% OR lower(adr.address) LIKE %:searchString%) "
			+ "AND (:countyId IS NULL OR adr.county.id = :countyId) "
			+ "AND (:municipalityId IS NULL OR adr.municipality.id = :municipalityId)"
			+ "AND (cast(:date as localdate) IS NULL OR cast(adr.createdAt as localdate) >= :date)")
	List<Address> findAddressesByFilter(String searchString, Long countyId, Long municipalityId,
										LocalDate date);

	@Query("from Address adr WHERE adr.active = TRUE "
			+ "AND (:searchString IS NULL OR lower(adr.address) LIKE %:searchString%) ")
	List<Address> findAddressesBySearchString(String searchString);
}
