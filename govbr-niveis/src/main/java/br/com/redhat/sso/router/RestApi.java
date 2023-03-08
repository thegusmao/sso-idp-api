package br.com.redhat.sso.router;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.redhat.sso.processor.LevelProcessor;

@Component
class RestApi extends RouteBuilder {

	@Value("${context.api.version}")
	private String apiVersion;

	@BeanInject
	LevelProcessor processor;

	@Override
	public void configure() {

		rest(apiVersion.concat("/govbr"))
				.description("APIs que disponibilizam os serviços GOVBR para usuarios autenticados")
				.id("govbr-services").get("/nivel")
				.description("Este serviço retorna o selo de confiabilidade baseado no token atual")
				.id("confiabilidade-govbr").apiDocs(true).produces(MediaType.APPLICATION_JSON).param()
				.type(RestParamType.header).name(HttpHeaders.AUTHORIZATION).endParam().responseMessage().code(200)
				.endResponseMessage().responseMessage().code(500).message("Sistema indisponível").endResponseMessage()
				.to("direct:fetch-level");

		from("direct:fetch-level").routeId("direct-route-fetch-level").tracing().process(processor);
	}
}
