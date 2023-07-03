package ee.kemit.aks.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "county")
@Getter
@Setter
public class County extends DomainBase {

	private String name;

	@OneToMany(mappedBy = "county")
	private Set<Address> addresses = new HashSet<>();
}