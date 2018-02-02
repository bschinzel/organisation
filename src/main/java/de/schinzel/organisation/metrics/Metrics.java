package de.schinzel.organisation.metrics;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

public class Metrics {
	public static final MetricRegistry REGISTRY = new MetricRegistry();
	
	static {
		Metrics.startReport();
	}
	
	static void startReport() {
		ConsoleReporter reporter = ConsoleReporter.forRegistry(REGISTRY)
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS).build();
		reporter.start(5, TimeUnit.MINUTES);
	}
}
