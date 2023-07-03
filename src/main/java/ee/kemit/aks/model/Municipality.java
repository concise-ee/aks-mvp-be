package ee.kemit.aks.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "municipality")
@Getter
@Setter
public class Municipality extends DomainBase {

	private String name;

	@OneToMany(mappedBy = "municipality")
	private Set<Address> addresses = new HashSet<>();
}
