package de.schinzel.organisation.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
@NamedQueries({
	@NamedQuery(name = "alleUser", 
			query = "SELECT u FROM User AS u WHERE u.organisation.uuid = :organisationUuid"),
	@NamedQuery(name = "loescheUser", 
			query = "DELETE FROM User AS u WHERE u.uuid = :uuid"),
	@NamedQuery(name = "aktualisiereUser", 
			query = "UPDATE User AS u SET u.name = :name, u.passwort = :passwort, u.email = :email WHERE u.uuid = :uuid")
})
public class User {

	@Id
	@Column(columnDefinition = "CHAR")
	private String uuid;
	
	@ManyToOne
	@JoinColumn(name = "organisationuuid")
	private Organisation organisation;
	
	private String name;
	
	@JsonIgnore
	@Column(columnDefinition = "CHAR")
	private String passwort;
	
	private String email;
	
	@ManyToMany
	@JoinTable(name = "userrolle", 
		joinColumns = @JoinColumn(columnDefinition = "CHAR", name = "useruuid", referencedColumnName = "uuid"),
		inverseJoinColumns = @JoinColumn(columnDefinition = "CHAR", name = "rolleuuid", referencedColumnName = "uuid"))
	private List<Rolle> rollen;
	
	@ManyToMany
	@JoinTable(name = "usergruppe", 
		joinColumns = @JoinColumn(columnDefinition = "CHAR", name = "useruuid", referencedColumnName = "uuid"),
		inverseJoinColumns = @JoinColumn(columnDefinition = "CHAR", name = "gruppeuuid", referencedColumnName = "uuid"))
	private List<Gruppe> gruppen;
	
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

	public String getPasswort() {
		return passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<Rolle> getRollen() {
		return rollen;
	}

	public void setRollen(List<Rolle> rollen) {
		this.rollen = rollen;
	}

	public List<Gruppe> getGruppen() {
		return gruppen;
	}

	public void setGruppen(List<Gruppe> gruppen) {
		this.gruppen = gruppen;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((gruppen == null) ? 0 : gruppen.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((organisation == null) ? 0 : organisation.hashCode());
		result = prime * result
				+ ((passwort == null) ? 0 : passwort.hashCode());
		result = prime * result + ((rollen == null) ? 0 : rollen.hashCode());
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
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (gruppen == null) {
			if (other.gruppen != null)
				return false;
		} else if (!gruppen.equals(other.gruppen))
			return false;
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
		if (passwort == null) {
			if (other.passwort != null)
				return false;
		} else if (!passwort.equals(other.passwort))
			return false;
		if (rollen == null) {
			if (other.rollen != null)
				return false;
		} else if (!rollen.equals(other.rollen))
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
