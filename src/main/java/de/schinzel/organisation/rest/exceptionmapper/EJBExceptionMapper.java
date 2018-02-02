package de.schinzel.organisation.rest.exceptionmapper;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import de.schinzel.organisation.metrics.Metrics;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

	private final Counter exceptionCounter = Metrics.REGISTRY.counter(
			MetricRegistry.name(EJBExceptionMapper.class, "EJBExceptions"));
	
	@Override
	public Response toResponse(EJBException exception) {
		this.exceptionCounter.inc();
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage()).build();
	}
}
