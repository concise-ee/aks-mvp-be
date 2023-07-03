package ee.kemit.aks.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.kemit.aks.model.County;

public interface CountyRepository extends JpaRepository<County, Long> {

	Optional<County> findByNameEqualsIgnoreCase(String county);
}
