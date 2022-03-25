package eu.europa.ec.dgc.verifier.service;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;

public interface TrustedIssuerDownloadService {

    /**
     * Synchronises the trusted issuers with the gateway.
     */
    void downloadTrustedIssuers();
}
