package ee.kemit.aks.repository;

import ee.kemit.aks.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Query("FROM Accommodation a LEFT JOIN FETCH a.addresses adr WHERE adr.active = TRUE")
    List<Accommodation> fetchAccommodationsWithActiveAddresses();
}