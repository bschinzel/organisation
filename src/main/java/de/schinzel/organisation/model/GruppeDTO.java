package de.schinzel.organisation.model;

import java.io.Serializable;

public class GruppeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String uuid;
	private String organisationuuid;
	private String parentuuid;
	private String name;
	
	public GruppeDTO() {}
	
	public GruppeDTO(String uuid, String organisationuuid, String parentuuid, String name) {
		this.name = name;
		this.uuid = uuid;
		this.organisationuuid = organisationuuid;
		this.parentuuid = parentuuid;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getOrganisationuuid() {
		return organisationuuid;
	}
	
	public void setOrganisationuuid(String organisationuuid) {
		this.organisationuuid = organisationuuid;
	}
	
	public String getParentuuid() {
		return parentuuid;
	}
	
	public void setParentuuid(String parentuuid) {
		this.parentuuid = parentuuid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((organisationuuid == null) ? 0 : organisationuuid.hashCode());
		result = prime * result
				+ ((parentuuid == null) ? 0 : parentuuid.hashCode());
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
		GruppeDTO other = (GruppeDTO) obj;
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
		if (parentuuid == null) {
			if (other.parentuuid != null)
				return false;
		} else if (!parentuuid.equals(other.parentuuid))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	

	public Gruppe toEntity(Organisation o) {
		Gruppe g = new Gruppe();
		
		g.setName(this.name);
		g.setUuid(this.uuid);
		g.setParentuuid(this.parentuuid);
		g.setOrganisation(o);
		
		return g;
	}
}
