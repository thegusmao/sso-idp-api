package br.com.redhat.sso.processor;

import java.util.Base64;

import javax.ws.rs.core.HttpHeaders;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.redhat.sso.service.LevelService;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrquestradorExceptionProcessor tem o objetivo de centralizar todo o
 * tratamento de exceções lançadas pelas rotas do Orquestrador. Ela deve ser
 * chamada através de uma cláusula onException ou doTry()
 * 
 * @author rosantos
 *
 */
@Service
public class LevelProcessor implements Processor {

	private static final Logger logger = Logger.getLogger(LevelProcessor.class);

	private static final String USERNAME_PARAM = "preferred_username";
	private static final String CPF_PARAM = "cpf";

	@Autowired
	LevelService service;

	Gson gson = new Gson();
	Base64.Decoder decoder = Base64.getUrlDecoder();

	@Override
	public void process(Exchange exchange) throws Exception {

		Object authorizationHeader = exchange.getIn().getHeader(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader != null) {
			String authToken = authorizationHeader.toString();
			String cpf = null;
			logger.info("Processing levels for " + authorizationHeader);
			if (authToken.indexOf(LevelService.TOKEN_PREFIX) != -1) {
				authToken = authToken
						.substring(authToken.indexOf(LevelService.TOKEN_PREFIX) + LevelService.TOKEN_PREFIX.length());
			}
			String[] chunks = authToken.split("\\.");
			if (chunks.length > 1) {
				String payload = new String(decoder.decode(chunks[1]));
				JsonObject tokenParams = gson.fromJson(payload, JsonObject.class);
				if (tokenParams.has(USERNAME_PARAM)) {
					cpf = tokenParams.get(USERNAME_PARAM).getAsString();
				}
				if (tokenParams.has(CPF_PARAM)) {
					cpf = tokenParams.get(CPF_PARAM).getAsString();
				}
				logger.debug("Processing levels for CPF " + cpf);
				String levels = service.getLevel(authToken, cpf);
				if (levels != null) {
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
					exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
					exchange.getIn().setBody(levels);
					logger.debug("Levels for CPF " + cpf + " -> " + levels);
					return;
				}
			}

		}

		JsonObject jsonobj = new JsonObject();
		jsonobj.addProperty("error", "invalid token");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
		exchange.getIn().setBody(gson.toJson(jsonobj));

	}

}
