package ee.kemit.aks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address extends DomainBase {

	private String adsOid;
	private String address;
	private boolean active;
	@Column(name = "centroid_x")
	private String centroidX;
	@Column(name = "centroid_y")
	private String centroidY;

	@ManyToOne
	private Accommodation accommodation;

	@ManyToOne
	private County county;

	@ManyToOne
	private Municipality municipality;
}