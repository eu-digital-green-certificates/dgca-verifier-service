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


import eu.europa.ec.dgc.gateway.connector.model.TrustedIssuer;
import eu.europa.ec.dgc.verifier.entity.TrustedIssuerEntity;
import eu.europa.ec.dgc.verifier.mapper.IssuerMapper;
import eu.europa.ec.dgc.verifier.repository.TrustedIssuerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrustedIssuerService {

    private final InfoService infoService;

    private final IssuerMapper issuerMapper;

    private final TrustedIssuerRepository trustedIssuerRepository;

    /**
     * Get the current etag.
     * @return the current etag
     */

    public String getEtag() {
        String etag = infoService.getValueForKey(InfoService.CURRENT_ETAG);
        if (etag == null) {
            etag = "";
        }
        return etag;
    }


    /**
     * Method to query the db for all trusted issuers.
     *
     * @return List holding the found trusted issuers.
     */
    public List<TrustedIssuerEntity> getAllIssuers(String etag) {
        return trustedIssuerRepository.findAllByEtag(etag);
    }

    /**
     * Method to synchronise the issuers in the db with the given List of trusted issuers.
     *
     * @param trustedIssuers defines the list of trusted issuers.
     *
     */
    @Transactional
    public void updateTrustedIssuersList(List<TrustedIssuer> trustedIssuers) {
        String newEtag = UUID.randomUUID().toString();

        List<TrustedIssuerEntity> trustedIssuerEntities = new ArrayList<>();


        for (TrustedIssuer trustedIssuer : trustedIssuers) {
            trustedIssuerEntities.add(getTrustedIssuerEntity(newEtag, trustedIssuer));
        }

        trustedIssuerRepository.saveAll(trustedIssuerEntities);

        String oldEtag = getEtag();
        infoService.setValueForKey(InfoService.CURRENT_ETAG, newEtag);

        cleanupData(oldEtag);

    }

    private TrustedIssuerEntity getTrustedIssuerEntity(String etag, TrustedIssuer trustedIssuer) {
        TrustedIssuerEntity entity = issuerMapper.trustedIssuerToTrustedIssuerEntity(trustedIssuer);
        entity.setEtag(etag);
        return entity;
    }

    private void cleanupData(String etag) {
        trustedIssuerRepository.deleteAllByEtag(etag);
    }

}
