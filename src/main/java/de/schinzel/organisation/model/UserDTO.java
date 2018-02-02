package de.schinzel.organisation.model;

public class UserDTO {

	private String uuid;
	
	private String organisationuuid;
	
	private String name;
	
	private String passwort;
	
	private String email;
	
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

	public String getOrganisationuuid() {
		return organisationuuid;
	}

	public void setOrganisationuuid(String organisationuuid) {
		this.organisationuuid = organisationuuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((organisationuuid == null) ? 0 : organisationuuid.hashCode());
		result = prime * result
				+ ((passwort == null) ? 0 : passwort.hashCode());
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
		UserDTO other = (UserDTO) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
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
		if (passwort == null) {
			if (other.passwort != null)
				return false;
		} else if (!passwort.equals(other.passwort))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	public User toEntity(Organisation o) {
		User u = new User();
		
		u.setEmail(this.email);
		u.setPasswort(this.passwort);
		u.setName(this.name);
		u.setUuid(this.uuid);
		u.setOrganisation(o);
		
		return u;
	}
}
