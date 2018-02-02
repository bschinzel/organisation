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
import de.schinzel.organisation.model.Organisation;
import de.schinzel.organisation.model.RolleDTO;

@Path("/v1/rollen")
@Stateless
@LocalBean
public class RolleEndpoint {
	
	private static final Logger LOGGER = Logger.getLogger(RolleEndpoint.class);
	
	private final Timer requestsGetAlleRollen = Metrics.REGISTRY.timer(
			MetricRegistry.name(RolleEndpoint.class, "GET /organisationen/v1/rollen/ ?organisationUuid"));
	private final Timer requestsPostRolle = Metrics.REGISTRY.timer(
			MetricRegistry.name(RolleEndpoint.class, "POST /organisationen/v1/rollen/"));
	private final Timer requestsDeleteRolle = Metrics.REGISTRY.timer(
			MetricRegistry.name(RolleEndpoint.class, "DELETE /organisationen/v1/rollen/{uuid}"));
	private final Timer requestsPutRolle = Metrics.REGISTRY.timer(
			MetricRegistry.name(RolleEndpoint.class, "PUT /organisationen/v1/rollen/{uuid}"));
	
	@PersistenceContext private EntityManager em;
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@NoCache
	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Rolle> alleRollenInEinerOrganisation(@QueryParam("organisationUuid") String organisationUuid) {
		final Timer.Context timercontext = requestsGetAlleRollen.time();
		List<Rolle> Rollen = null;
		LOGGER.trace("GET alle Rollen einer Organisation");
		try {
			Rollen = this.em.createNamedQuery("alleRollen", Rolle.class)
				.setParameter("organisationUuid", organisationUuid)
				.getResultList();
			return Rollen;
		} catch (Exception e) {
			LOGGER.error("GET alle Rollen einer Organisation fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void erstellen(RolleDTO rolle) {
		final Timer.Context timercontext = requestsPostRolle.time();
		LOGGER.trace("POST neue Rolle");
		try {
			Organisation o = this.em.find(Organisation.class, rolle.getOrganisationuuid());
			
			if (o == null) {
				LOGGER.error("POST neue Rolle fehlgeschlagen: Ungültige organisationuuid=" + rolle.getOrganisationuuid());
				throw new IllegalArgumentException();
			}
			
			Rolle g = rolle.toEntity(o);
			this.em.persist(g);
		} catch (Exception e) {
			LOGGER.error("POST neue Rolle fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@PUT
	@Path("/{uuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void aktualisieren(
			@PathParam("uuid") String uuid, 
			RolleDTO rolle) {
		
		final Timer.Context timercontext = requestsPutRolle.time();
		LOGGER.trace("PUT Rolle (Update)");
		
		try {
			if (!uuid.equals(rolle.getUuid())) {
				throw new IllegalArgumentException("Die UUID im Pfad stimmt nicht mit der UUID der Rolle überein");
			}
			
			// Hinweis: Die @Version wird hierbei nicht inkrementiert, was kein Problem darstellt
			int count = this.em.createNamedQuery("aktualisiereRolle")
					.setParameter("uuid", rolle.getUuid())
					.setParameter("name", rolle.getName())
					.executeUpdate();
				
				if (count == 0) {
					LOGGER.warn("PUT Rolle (Update) uuid=" + rolle.getUuid() + " hat keinen Datensatz betroffen");
				}
		} catch (Exception e) {
			LOGGER.error("PUT Rolle (Update) uuid=" + rolle.getUuid() + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@DELETE
	@Path("/{uuid}")
	@Transactional(Transactional.TxType.REQUIRED)
	public void loeschen(@PathParam("uuid") String uuid) {
		final Timer.Context timercontext = requestsDeleteRolle.time();
		LOGGER.trace("DELETE Rolle uuid=" + uuid);
		try {
			int count = this.em.createNamedQuery("loescheRolle")
				.setParameter("uuid", uuid)
				.executeUpdate();
			
			if (count == 0) {
				LOGGER.warn("DELETE Rolle uuid=" + uuid + " hat keinen Datensatz betroffen");
			}
		} catch (Exception e) {
			LOGGER.error("DELETE Rolle uuid=" + uuid + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
}
