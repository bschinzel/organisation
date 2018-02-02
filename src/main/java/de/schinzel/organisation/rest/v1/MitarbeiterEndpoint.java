package de.schinzel.organisation.rest.v1;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import de.schinzel.organisation.metrics.Metrics;
import de.schinzel.organisation.model.Rolle;
import de.schinzel.organisation.model.User;
import de.schinzel.organisation.model.Organisation;
import de.schinzel.organisation.model.UserDTO;

@Path("/v1/mitarbeiter")
@Stateless
@LocalBean
public class MitarbeiterEndpoint {
	
	private static final Logger LOGGER = Logger.getLogger(MitarbeiterEndpoint.class); 
	private final Timer requestsGetAlleMitarbeiter = Metrics.REGISTRY.timer(
			MetricRegistry.name(MitarbeiterEndpoint.class, "GET /organisationen/v1/mitarbeiter/ ?organisationUuid"));
	private final Timer requestsPostMitarbeiter = Metrics.REGISTRY.timer(
			MetricRegistry.name(MitarbeiterEndpoint.class, "POST /organisationen/v1/mitarbeiter/"));
	private final Timer requestsDeleteMitarbeiter = Metrics.REGISTRY.timer(
			MetricRegistry.name(MitarbeiterEndpoint.class, "DELETE /organisationen/v1/mitarbeiter/{uuid}"));
	private final Timer requestsPutMitarbeiter = Metrics.REGISTRY.timer(
			MetricRegistry.name(MitarbeiterEndpoint.class, "PUT /organisationen/v1/mitarbeiter/{uuid}"));
	private final Timer requestsPutRolleZuMitarbeiter = Metrics.REGISTRY.timer(
			MetricRegistry.name(MitarbeiterEndpoint.class, "PUT /organisationen/v1/mitarbeiter/{useruuid}/rolle/{rolleuuid}"));
	
	@PersistenceContext private EntityManager em;
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@NoCache
	@Transactional(Transactional.TxType.SUPPORTS)
	public List<User> alleMitarbeiterEinerOrganisation(
			@QueryParam("organisationUuid") String organisationUuid) {
		
		final Timer.Context timercontext = requestsGetAlleMitarbeiter.time();
		List<User> user = null;
		LOGGER.trace("GET alle Usern einer Organisation");
		try {
			user = this.em.createNamedQuery("alleUser", User.class)
				.setParameter("organisationUuid", organisationUuid)
				.getResultList();
			
			return user;
		} catch (Exception e) {
			LOGGER.error("GET alle Usern einer Organisation fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void mitarbeiterErstellen(UserDTO user) {
		final Timer.Context timercontext = requestsPostMitarbeiter.time();
		LOGGER.trace("POST neuer Mitarbeiter");
		try {
			Organisation o = this.em.find(Organisation.class, user.getOrganisationuuid());
			
			if (o == null) {
				LOGGER.error("POST neuer Mitarbeiter fehlgeschlagen: Ungültige organisationuuid=" + user.getOrganisationuuid());
				throw new IllegalArgumentException();
			}
			
			User g = user.toEntity(o);
			this.em.persist(g);
		} catch (Exception e) {
			LOGGER.error("POST neuer Mitarbeiter fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@PUT
	@Path("/{uuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void mitarbeiterAktualisieren(
			@PathParam("uuid") String uuid, UserDTO user) {
		
		final Timer.Context timercontext = requestsPutMitarbeiter.time();
		LOGGER.trace("PUT Mitarbeiter (Update)");
		
		try {
			if (!uuid.equals(user.getUuid())) {
				throw new IllegalArgumentException("Die UUID im Pfad stimmt nicht mit der UUID des Mitarbeiters überein");
			}
			
			// Hinweis: Die @Version wird hierbei nicht inkrementiert, was kein Problem darstellt
			int count = this.em.createNamedQuery("aktualisiereUser")
					.setParameter("uuid", user.getUuid())
					.setParameter("name", user.getName())
					.setParameter("passwort", user.getPasswort())
					.setParameter("email", user.getEmail())
					.executeUpdate();
				
				if (count == 0) {
					LOGGER.warn("PUT Mitarbeiter (Update) uuid=" + user.getUuid() + " hat keinen Datensatz betroffen");
				}
		} catch (Exception e) {
			LOGGER.error("PUT Mitarbeiter (Update) uuid=" + user.getUuid() + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@PUT
	@Path("/{useruuid}/rolle/{rolleuuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void fuegeRolleHinzu(
			@PathParam("useruuid") String userUuid, 
			@PathParam("rolleuuid") String rolleUuid) {
		
		final Timer.Context timercontext = requestsPutRolleZuMitarbeiter.time();
		LOGGER.trace("PUT Rolle zu Mitarbeiter");
		try {
				User user = this.em.find(User.class, userUuid);
				if (user == null) {
					LOGGER.error("Mitarbeiter nicht gefunden: " + userUuid);
					throw new IllegalArgumentException();
				}
				
				Rolle rolle = this.em.find(Rolle.class, rolleUuid);
				if (rolle == null) {
					LOGGER.error("Rolle nicht gefunden: " + rolleUuid);
					throw new IllegalArgumentException();
				}
				
				user.getRollen().add(rolle);
				this.em.merge(user);
				
		} catch (Exception e) {
			LOGGER.error("PUT Rolle zu Mitarbeiter useruuid=" + userUuid + ", rolleuuid=" + rolleUuid + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@DELETE
	@Path("/{uuid}")
	@Transactional(Transactional.TxType.REQUIRED)
	public void loeschen(@PathParam("uuid") String uuid) {
		final Timer.Context timercontext = requestsDeleteMitarbeiter.time();
		LOGGER.trace("DELETE Mitarbeiter uuid=" + uuid);
		try {
			int count = this.em.createNamedQuery("loescheUser")
				.setParameter("uuid", uuid)
				.executeUpdate();
			
			if (count == 0) {
				LOGGER.warn("DELETE Mitarbeiter uuid=" + uuid + " hat keinen Datensatz betroffen");
			}
		} catch (Exception e) {
			LOGGER.error("DELETE Mitarbeiter uuid=" + uuid + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
}
