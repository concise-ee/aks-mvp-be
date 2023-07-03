package ee.kemit.aks.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.kemit.aks.model.Municipality;

public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {

	Optional<Municipality> findByNameContainingIgnoreCase(String ov);
}
