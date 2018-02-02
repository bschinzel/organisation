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

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import de.schinzel.organisation.metrics.Metrics;
import de.schinzel.organisation.model.Gruppe;
import de.schinzel.organisation.model.Organisation;

@Path("/v1")
@Stateless
@LocalBean
public class OrganisationEndpoint {
	
	private static final Logger LOGGER = Logger.getLogger(OrganisationEndpoint.class); 
	private final Timer requestsGetOrganisationen = Metrics.REGISTRY.timer(
			MetricRegistry.name(OrganisationEndpoint.class, "GET /organisationen/v1/"));
	private final Timer requestsDeleteOrganisation = Metrics.REGISTRY.timer(
			MetricRegistry.name(OrganisationEndpoint.class, "DELETE /organisationen/v1/{uuid}"));
	private final Timer requestsPostOrganisation = Metrics.REGISTRY.timer(
			MetricRegistry.name(OrganisationEndpoint.class, "POST /organisationen/v1/"));
	private final Timer requestsPutGruppeZuOrganisation = Metrics.REGISTRY.timer(
			MetricRegistry.name(OrganisationEndpoint.class, "PUT /organisationen/v1/{organisationuuid}/gruppe/{gruppeuuid}"));
	
	@PersistenceContext private EntityManager em;
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@NoCache
	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Organisation> alle() {
		final Timer.Context timercontext = requestsGetOrganisationen.time();
		List<Organisation> organisationen = null;
		LOGGER.trace("GET alle Organisationen");
		try {
			organisationen = this.em.createNamedQuery("alleOrganisationen", Organisation.class)
				.getResultList();
			return organisationen;
		} catch (Exception e) {
			LOGGER.error("GET alle Organisationen fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void registrieren(Organisation organisation) {
		final Timer.Context timercontext = requestsPostOrganisation.time();
		LOGGER.trace("POST Organisation (Create)");
		try {
			this.em.persist(organisation);
		} catch (Exception e) {
			LOGGER.error("POST Organisation (Create) uuid=" + organisation.getUuid() + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@PUT
	@Path("/{uuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void aktualisieren(@PathParam("uuid") String uuid, Organisation organisation) {
		final Timer.Context timercontext = requestsPostOrganisation.time();
		LOGGER.trace("PUT Organisation (Update)");
		
		try {
			if (!uuid.equals(organisation.getUuid())) {
				throw new IllegalArgumentException("Die UUID im Pfad stimmt nicht mit der UUID der Organisation überein");
			}
			
			// Hinweis: Die @Version wird hierbei nicht inkrementiert, was kein Problem darstellt
			int count = this.em.createNamedQuery("aktualisiereOrganisation")
					.setParameter("uuid", organisation.getUuid())
					.setParameter("name", organisation.getName())
					.executeUpdate();
				
				if (count == 0) {
					LOGGER.warn("PUT Organisation (Update) uuid=" + organisation.getUuid() + " hat keinen Datensatz betroffen");
				}
		} catch (Exception e) {
			LOGGER.error("PUT Organisation (Update) uuid=" + organisation.getUuid() + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@PUT
	@Path("/{organisationuuid}/gruppe/{gruppeuuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.REQUIRED)
	public void fuegeGruppeHinzu(
			@PathParam("gruppeuuid") String gruppeUuid, 
			@PathParam("organisationuuid") String organisationUuid) {
		
		final Timer.Context timercontext = requestsPutGruppeZuOrganisation.time();
		LOGGER.trace("PUT Gruppe zu Organisation");
		try {
				Organisation organisation = this.em.find(Organisation.class, organisationUuid);
				if (organisation == null) {
					LOGGER.error("Organisation nicht gefunden: " + organisationUuid);
					throw new IllegalArgumentException();
				}
				
				Gruppe gruppe = this.em.find(Gruppe.class, gruppeUuid);
				if (gruppe == null) {
					LOGGER.error("Gruppe nicht gefunden: " + gruppeUuid);
					throw new IllegalArgumentException();
				}
				
				organisation.getGruppen().add(gruppe);
				this.em.merge(organisation);
				
		} catch (Exception e) {
			LOGGER.error("PUT Gruppe zu Organisation organisationuuid=" + organisationUuid 
					+ ", gruppeuuid=" + gruppeUuid + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
	
	@DELETE
	@Path("/{uuid}")
	@Transactional(Transactional.TxType.REQUIRED)
	public void loeschen(
			@PathParam("uuid") String uuid) {
		
		final Timer.Context timercontext = requestsDeleteOrganisation.time();
		LOGGER.trace("DELETE Organisation uuid=" + uuid);
		try {
			int count = this.em.createNamedQuery("loescheOrganisation")
				.setParameter("uuid", uuid)
				.executeUpdate();
			
			if (count == 0) {
				LOGGER.warn("DELETE Organisation uuid=" + uuid + " hat keinen Datensatz betroffen");
			}
		} catch (Exception e) {
			LOGGER.error("DELETE Organisation uuid=" + uuid + " fehlgeschlagen");
			throw e;
		} finally {
			timercontext.stop();
		}
	}
}
