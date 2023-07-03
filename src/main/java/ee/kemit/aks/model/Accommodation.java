package ee.kemit.aks.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accommodation")
@Getter
@Setter
public class Accommodation extends DomainBase {

	private String name;

	@OneToMany(mappedBy = "accommodation", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = true)
	private Set<Address> addresses = new HashSet<>();
}
