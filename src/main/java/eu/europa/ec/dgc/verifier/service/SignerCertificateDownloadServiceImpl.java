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

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
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
public class SignerCertificateDownloadServiceImpl implements SignerCertificateDownloadService {

    private final DgcGatewayDownloadConnector dgcGatewayConnector;
    private final SignerInformationService signerInformationService;

    @Override
    @Scheduled(fixedDelayString = "${dgc.certificatesDownloader.timeInterval}")
    @SchedulerLock(name = "SignerCertificateDownloadService_downloadCertificates", lockAtLeastFor = "PT0S",
        lockAtMostFor = "${dgc.certificatesDownloader.lockLimit}")
    public void downloadCertificates() {
        log.info("Certificates download started");

        List<TrustListItem> trustedCerts = dgcGatewayConnector.getTrustedCertificates();

        signerInformationService.updateTrustedCertsList(trustedCerts);

        log.info("Certificates download finished");
    }

}
