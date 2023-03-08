package br.com.redhat.sso.config;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class RestApiConfiguration extends RouteBuilder {

	@Value("${camel.component.servlet.mapping.context-path}")
	private String contextPath;

	@Value("${context.api.version}")
	private String apiVersion;

	@Override
	public void configure() {
		restConfiguration().component("servlet").contextPath(contextPath.concat("/").concat(apiVersion))
				.apiContextPath("/api-doc").apiProperty("api.title", "SSO GOVBR API")
				.apiProperty("api.version", apiVersion).apiProperty("cors", "true").apiContextRouteId("doc-api")
				.port("{{server.port}}").bindingMode(RestBindingMode.off);

	}
}
