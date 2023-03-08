package br.gov.pa.prodepa.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.HttpHeaders;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.gov.pa.prodepa.util.ConnectionUtils;

@Service
public class EnterpriseService {
	private static final Logger logger = Logger.getLogger(EnterpriseService.class);

	public static final String TOKEN_PREFIX = "Bearer ";

	@Value("${govbr.service-url}")
	private String govBrUrl;

	@Autowired
	KeycloakService keycloak;

	public String getEnterprise(String token, String cpf) {

		if (!token.startsWith(TOKEN_PREFIX)) {
			token = TOKEN_PREFIX + token;
		}

		try {
			logger.debug("Trying to obtain original token for " + cpf);
			String originalToken = keycloak.getOriginalToken(token);
			if (originalToken != null) {
				logger.debug("Original token for " + cpf + " -> " + originalToken);
				HttpGet get = new HttpGet(govBrUrl + "/empresas/v2/empresas?filtrar-por-participante=" + cpf);
				CloseableHttpClient httpClient = ConnectionUtils.getHttpClient();
				ConnectionUtils.configureDefaultHeaders(get);
				get.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + originalToken);
				CloseableHttpResponse response = httpClient.execute(get);
				String jsonResp = EntityUtils.toString(response.getEntity());
				logger.debug("Entreprise for " + cpf + " -> " + jsonResp);
				return jsonResp;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
