package de.schinzel.organisation.rest.exceptionmapper;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import de.schinzel.organisation.metrics.Metrics;

@Provider
public class EJBTransactionRolledbackExceptionMapper implements ExceptionMapper<EJBTransactionRolledbackException> {

	private final Counter exceptionCounter = Metrics.REGISTRY.counter(
			MetricRegistry.name(EJBTransactionRolledbackExceptionMapper.class, "EJBTransactionRolledbackExceptions"));
	
	@Override
	public Response toResponse(EJBTransactionRolledbackException exception) {
		this.exceptionCounter.inc();
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage()).build();
	}
}
