package de.schinzel.organisation.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "gruppe")
@NamedQueries({
	@NamedQuery(name = "alleObergruppen", 
			query = "SELECT g FROM Gruppe AS g WHERE g.organisation.uuid = :organisationUuid AND g.parentuuid IS NULL"),
	@NamedQuery(name = "loescheGruppe", 
			query = "DELETE FROM Gruppe AS g WHERE g.uuid = :uuid"),
	@NamedQuery(name = "aktualisiereGruppe", 
			query = "UPDATE Gruppe AS g SET g.name = :name, g.parentuuid = :parentuuid WHERE g.uuid = :uuid")
})
public class Gruppe implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(columnDefinition = "CHAR")
	private String uuid;
	
	@JsonIgnore
	@Column(columnDefinition = "CHAR")
	private String parentuuid;
	
	private String name;
	
	@OneToMany(mappedBy = "parentuuid", fetch = FetchType.EAGER)
	private Collection<Gruppe> untergruppen;
	
	@ManyToOne
	@JoinColumn(name = "organisationuuid")
	private Organisation organisation;
	
	@ManyToMany(mappedBy = "gruppen")
	private List<User> user;
	
	@Version
	@JsonIgnore
	private int version;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getParentuuid() {
		return parentuuid;
	}

	public void setParentuuid(String parentuuid) {
		this.parentuuid = parentuuid;
	}

	public Collection<Gruppe> getUntergruppen() {
		return untergruppen;
	}

	public void setUntergruppen(Collection<Gruppe> untergruppen) {
		this.untergruppen = untergruppen;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public GruppeDTO toDTO() {
		return new GruppeDTO(this.uuid, this.organisation.getUuid(), this.parentuuid, this.name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((organisation == null) ? 0 : organisation.hashCode());
		result = prime * result
				+ ((parentuuid == null) ? 0 : parentuuid.hashCode());
		result = prime * result
				+ ((untergruppen == null) ? 0 : untergruppen.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + version;
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
		Gruppe other = (Gruppe) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organisation == null) {
			if (other.organisation != null)
				return false;
		} else if (!organisation.equals(other.organisation))
			return false;
		if (parentuuid == null) {
			if (other.parentuuid != null)
				return false;
		} else if (!parentuuid.equals(other.parentuuid))
			return false;
		if (untergruppen == null) {
			if (other.untergruppen != null)
				return false;
		} else if (!untergruppen.equals(other.untergruppen))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
}
