/*-
 * ---license-start
 * eu-digital-green-certificates / dgca-verifier-service
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

package eu.europa.ec.dgc.verifier.service;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayTrustedIssuerDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.model.TrustedIssuer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A service to download the signer certificates from the digital green certificate gateway.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!btp")
@ConditionalOnProperty("dgc.trustedIssuerDownloader.enabled")
public class TrustedIssuerDownloadServiceImpl implements TrustedIssuerDownloadService {

    private final DgcGatewayTrustedIssuerDownloadConnector downloadConnector;

    private final TrustedIssuerService trustedIssuerService;


    @Override
    @Scheduled(fixedDelayString = "${dgc.trustedIssuerDownloader.timeInterval}")
    @SchedulerLock(name = "TrustedIssuerDownloadService_downloadTrustedIssuers", lockAtLeastFor = "PT0S",
        lockAtMostFor = "${dgc.trustedIssuerDownloader.lockLimit}")
    public void downloadTrustedIssuers() {
        log.info("Trusted issuers download started");

        List<TrustedIssuer> trustedIssuers = downloadConnector.getTrustedIssuers();

        trustedIssuerService.updateTrustedIssuersList(trustedIssuers);

        log.info("Trusted issuers download finished. {} issuers downloaded.", trustedIssuers.size());
    }

}
