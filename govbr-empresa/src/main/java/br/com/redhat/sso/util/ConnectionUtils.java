package br.com.redhat.sso.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpMessage;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

public class ConnectionUtils {

	public static CloseableHttpClient getHttpClient() {
		return getHttpClient(null, null);
	}

	public static CloseableHttpClient getHttpClient(String keystorePath, String keyPassphrase) {
		try {
			SSLContext sslContext = null;
			if (keystorePath != null) {
				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				keyStore.load(new FileInputStream(keystorePath), keyPassphrase.toCharArray());

				sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, null)
						.loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			} else {
				sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			}
			SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
			return HttpClients.custom().setSSLSocketFactory(scsf).build();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	public static HttpMessage configureDefaultHeaders(HttpMessage message) {
		message.addHeader("Cache-Control", "no-cache");
		message.addHeader("Pragma", "no-cache");
		message.addHeader("Connection", "Keep-Alive");
		message.addHeader("Keep-Alive", "timeout=30, max=1000");
		return message;
	}
}
