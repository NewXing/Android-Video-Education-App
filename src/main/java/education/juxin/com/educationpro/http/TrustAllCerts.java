package education.juxin.com.educationpro.http;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * X509 协议
 * The type Trust all certs.
 */
public class TrustAllCerts implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}