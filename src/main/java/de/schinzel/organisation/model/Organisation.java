package de.schinzel.organisation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "organisation")
@NamedQueries({
	@NamedQuery(name = "alleOrganisationen", 
			query = "SELECT o FROM Organisation AS o"),
	@NamedQuery(name = "loescheOrganisation", 
			query = "DELETE FROM Organisation AS o WHERE o.uuid = :uuid"),
	@NamedQuery(name = "aktualisiereOrganisation", 
			query = "UPDATE Organisation AS o SET o.name = :name WHERE o.uuid = :uuid")
})
public class Organisation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(columnDefinition = "CHAR")
	private String uuid;
	
	private String name;
	
	@JsonIgnore
	@OneToMany(mappedBy = "organisation")
	private List<Gruppe> gruppen;
	
	@Version
	@JsonIgnore
	private int version;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Organisation other = (Organisation) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	public List<Gruppe> getGruppen() {
		return gruppen;
	}

	public void setGruppen(List<Gruppe> gruppen) {
		this.gruppen = gruppen;
	}
}
