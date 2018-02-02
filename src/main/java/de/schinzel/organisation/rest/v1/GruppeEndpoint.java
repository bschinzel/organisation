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
import de.schinzel.organisation.model.Gruppe;
import de.schinzel.organisation.model.GruppeDTO;
import de.schinzel.organisation.model.Organisation;
import de.schinzel.organisation.model.User;

@Path("/v1/gruppen")
@Stateless
@LocalBean
public class GruppeEndpoint {
	
	private static final Logger LOGGER = Logger.getLogger(GruppeEndpoint.class); 
	private final Timer requestsGetGruppen = Metrics.REGISTRY.timer(
			MetricRegistry.name(GruppeEndpoint.class, "GET /organisationen/v1/gruppen/ ?organisationUuid"));
	private final Timer requestsPostGruppe = Metrics.REGISTRY.timer(
			MetricRegistry.name(GruppeEndpoint.class, "POST /organisationen/v1/gruppen/"));
	private final Timer requestsDeleteGruppe = Metrics.REGISTRY.timer(
			MetricRegistry.name(GruppeEndpoint.class, "DELETE /organisationen/v1/gruppen/{uuid}"));
	private final Timer requestsPutGruppe = Metrics.REGISTRY.timer(
			MetricRegistry.name(GruppeEndpoint.class, "PUT /organisationen/v1/gruppen/{uuid}"));
	private final Timer requestsPutUserZuGruppe = Metrics.REGISTRY.timer(
			MetricRegistry.name(GruppeEndpoint.class, "PUT /organisationen/v1/gruppen/{gruppeuuid}/user/{useruuid}"));
	
	@PersistenceContext private EntityManager em;
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@NoCache
	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Gruppe> alleGruppenEinerOrganisation(@QueryParam("organisationUuid") String organisationUuid) {
		final Timer.Context timercontext = requestsGetGruppen.time();
		List<Gruppe> gruppen = null;
		LOGGER.trace("GET alle Gruppen einer Organisation");
		try {
			gruppen = this.em.createNamedQuery("alleObergruppen", Gruppe.class)
				.setParameter("organisationUuid", organisationUuid)
				.getResultList();
			return gruppen;
		} catch (Exception e) {
			LOGGER.error("GET alle Gruppen einer Organisation fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void erstellen(GruppeDTO gruppe) {
		final Timer.Context timercontext = requestsPostGruppe.time();
		LOGGER.trace("POST neue Gruppe");
		try {
			Organisation o = this.em.find(Organisation.class, gruppe.getOrganisationuuid());
			
			if (o == null) {
				LOGGER.error("POST neue Gruppe fehlgeschlagen: Ungültige organisationuuid=" + gruppe.getOrganisationuuid());
				throw new IllegalArgumentException();
			}
			
			Gruppe g = gruppe.toEntity(o);
			this.em.persist(g);
		} catch (Exception e) {
			LOGGER.error("POST neue Gruppe fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@PUT
	@Path("/{uuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void aktualisieren(@PathParam("uuid") String uuid, GruppeDTO gruppe) {
		final Timer.Context timercontext = requestsPutGruppe.time();
		LOGGER.trace("PUT Gruppe (Update)");
		try {
			if (!uuid.equals(gruppe.getUuid())) {
				throw new IllegalArgumentException("Die UUID im Pfad stimmt nicht mit der UUID der Gruppe überein");
			}
			
			// Hinweis: Die @Version wird hierbei nicht inkrementiert, was kein Problem darstellt
			int count = this.em.createNamedQuery("aktualisiereGruppe")
					.setParameter("uuid", gruppe.getUuid())
					.setParameter("parentuuid", gruppe.getParentuuid())
					.setParameter("name", gruppe.getName())
					.executeUpdate();
				
				if (count == 0) {
					LOGGER.warn("PUT Gruppe (Update) uuid=" + gruppe.getUuid() + " hat keinen Datensatz betroffen");
				}
		} catch (Exception e) {
			LOGGER.error("PUT Gruppe (Update) uuid=" + gruppe.getUuid() + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@PUT
	@Path("/{gruppeuuid}/user/{useruuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void fuegeUserHinzu(
			@PathParam("gruppeuuid") String gruppeUuid, 
			@PathParam("useruuid") String userUuid) {
		
		final Timer.Context timercontext = requestsPutUserZuGruppe.time();
		LOGGER.trace("PUT User zu Gruppe");
		try {
				User user = this.em.find(User.class, userUuid);
				if (user == null) {
					LOGGER.error("User nicht gefunden: " + userUuid);
					throw new IllegalArgumentException();
				}
				
				Gruppe gruppe = this.em.find(Gruppe.class, gruppeUuid);
				if (gruppe == null) {
					LOGGER.error("Gruppe nicht gefunden: " + gruppeUuid);
					throw new IllegalArgumentException();
				}
				
				user.getGruppen().add(gruppe);
				this.em.merge(user);
				
		} catch (Exception e) {
			LOGGER.error("PUT User zu Gruppe useruuid=" + userUuid + ", gruppeuuid=" + gruppeUuid + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@DELETE
	@Path("/{uuid}")
	@Transactional(Transactional.TxType.REQUIRED)
	public void loeschen(@PathParam("uuid") String uuid) {
		final Timer.Context timercontext = requestsDeleteGruppe.time();
		LOGGER.trace("DELETE Gruppe uuid=" + uuid);
		try {
			int count = this.em.createNamedQuery("loescheGruppe")
				.setParameter("uuid", uuid)
				.executeUpdate();
			
			if (count == 0) {
				LOGGER.warn("DELETE Gruppe uuid=" + uuid + " hat keinen Datensatz betroffen");
			}
		} catch (Exception e) {
			LOGGER.error("DELETE Gruppe uuid=" + uuid + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
}
