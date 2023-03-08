package br.gov.pa.prodepa.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.HttpHeaders;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.gov.pa.prodepa.util.ConnectionUtils;

@Service
public class KeycloakService {

	@Value("${keycloak.auth-server-url}")
	private String authUrl;

	@Value("${keycloak.realm}")
	public String realm;

	@Value("${keycloak.identity-provider}")
	public String identityProvider;

	static Logger logger = Logger.getLogger(KeycloakService.class);

	Gson gson = new Gson();

	public String getOriginalToken(String token)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		HttpGet get = new HttpGet(authUrl + "/realms/" + realm + "/broker/" + identityProvider + "/token");
		try {
			logger.debug("Geting original token for " + token + " on " + get.getURI());
			CloseableHttpClient httpClient = ConnectionUtils.getHttpClient();
			ConnectionUtils.configureDefaultHeaders(get);
			get.addHeader(HttpHeaders.AUTHORIZATION, token);
			CloseableHttpResponse response = httpClient.execute(get);
			String xmlString = EntityUtils.toString(response.getEntity());
			JsonObject json = gson.fromJson(xmlString, JsonObject.class);
			if (json.has("access_token")) {
				String originalToken = json.get("access_token").getAsString();
				logger.debug("Original token for " + token + " -> " + originalToken);
				return originalToken;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;
	}

//	private static void extractErrorMsg(CloseableHttpResponse response) throws IOException, ErrorResponseException {
//		String result = EntityUtils.toString(response.getEntity());
//		logger.debug("extractErrorMsg: " + response.getStatusLine() + " params: " + result);
//		if (!result.startsWith("{") || !result.endsWith("}")) {
//			result = "{\"error\":" + result + "\"}";
//		}
//		throw new ErrorResponseException(Response.status(Status.fromStatusCode(400)).entity(result).build());
//	}
}
