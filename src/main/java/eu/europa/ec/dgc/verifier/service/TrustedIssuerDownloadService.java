package eu.europa.ec.dgc.verifier.service;


public interface TrustedIssuerDownloadService {

    /**
     * Synchronises the trusted issuers with the gateway.
     */
    void downloadTrustedIssuers();
}
