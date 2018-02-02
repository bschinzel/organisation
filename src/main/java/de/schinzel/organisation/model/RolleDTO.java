package de.schinzel.organisation.model;

import java.io.Serializable;

public class RolleDTO implements Serializable {
	private static final long serialVersionUID = -5125298241832996007L;

	private String uuid;
	
	private String organisationuuid;
	
	private String name;
	
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

	public String getOrganisationuuid() {
		return organisationuuid;
	}

	public void setOrganisationuuid(String organisationuuid) {
		this.organisationuuid = organisationuuid;
	}
	
	public Rolle toEntity(Organisation o) {
		Rolle r = new Rolle();
		
		r.setName(this.name);
		r.setOrganisation(o);
		r.setUuid(this.uuid);
		
		return r;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((organisationuuid == null) ? 0 : organisationuuid.hashCode());
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
		RolleDTO other = (RolleDTO) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organisationuuid == null) {
			if (other.organisationuuid != null)
				return false;
		} else if (!organisationuuid.equals(other.organisationuuid))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
