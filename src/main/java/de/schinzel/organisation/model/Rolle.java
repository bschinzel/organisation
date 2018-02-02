package de.schinzel.organisation.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "rolle")
@NamedQueries({
	@NamedQuery(name = "alleRollen", 
			query = "SELECT r FROM Rolle AS r WHERE r.organisation.uuid = :organisationUuid"),
	@NamedQuery(name = "loescheRolle", 
			query = "DELETE FROM Rolle AS r WHERE r.uuid = :uuid"),
	@NamedQuery(name = "aktualisiereRolle", 
			query = "UPDATE Rolle AS r SET r.name = :name WHERE r.uuid = :uuid")
})
public class Rolle {

	@Id
	@Column(columnDefinition = "CHAR")
	private String uuid;
	
	@ManyToOne
	@JoinColumn(name = "organisationuuid")
	private Organisation organisation;
	
	private String name;
	
	@ManyToMany(mappedBy = "rollen")
	private List<User> user;
	
	@Version
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

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((organisation == null) ? 0 : organisation.hashCode());
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
		Rolle other = (Rolle) obj;
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
