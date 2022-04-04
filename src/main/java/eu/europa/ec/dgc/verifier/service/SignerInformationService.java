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


import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import eu.europa.ec.dgc.verifier.entity.SignerInformationEntity;
import eu.europa.ec.dgc.verifier.repository.SignerInformationRepository;
import eu.europa.ec.dgc.verifier.restapi.dto.CertificatesLookupResponseItemDto;
import eu.europa.ec.dgc.verifier.restapi.dto.DeltaListDto;
import eu.europa.ec.dgc.verifier.restapi.dto.KidDto;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Component
@RequiredArgsConstructor
public class SignerInformationService {

    private final SignerInformationRepository signerInformationRepository;


    /**
     * Method to query the db for a certificate with a resume token.
     *
     * @param resumeToken defines which certificate should be returned.
     * @return Optional holding the certificate if found.
     */
    public Optional<SignerInformationEntity> getCertificate(Long resumeToken) {
        if (resumeToken == null) {
            return signerInformationRepository.findFirstByIdIsNotNullOrderByIdAsc();
        } else {
            return signerInformationRepository.findFirstByIdGreaterThanOrderByIdAsc(resumeToken);
        }
    }


    /**
     * Method to query the db for a list of kid from all certificates.
     *
     * @return A list of kids of all certificates found. If no certificate was found an empty list is returned.
     */
    public List<String> getListOfValidKids() {

        List<SignerInformationEntity> certsList = signerInformationRepository.findAllByDeletedOrderByIdAsc(false);

        return certsList.stream().map(c -> c.getKid()).collect(Collectors.toList());

    }

    /**
     * Method to get all deleted certificates stored in a map.
     * @return A map with all deleted certificates.
     */
    public Map<String, SignerInformationEntity> getDeletedCertificates() {

        List<SignerInformationEntity> deletedCertsList = signerInformationRepository.findAllByDeletedOrderByIdAsc(true);

        return deletedCertsList.stream().collect(Collectors.toMap(SignerInformationEntity::getKid, c -> c));

    }



    /**
     * Method to synchronise the certificates in the db with the given List of trusted certificates.
     *
     * @param trustedCerts defines the list of trusted certificates.
     *
     */
    @Transactional
    public void updateTrustedCertsList(List<TrustListItem> trustedCerts) {

        List<String> trustedCertsKids = trustedCerts.stream().map(TrustListItem::getKid).collect(Collectors.toList());
        List<String> alreadyStoredCerts = getListOfValidKids();
        List<String> certsToDelete = new ArrayList<>();


        if (trustedCertsKids.isEmpty()) {
            signerInformationRepository.setAllDeleted();
            return;
        } else {
            signerInformationRepository.setDeletedByKidsNotIn(trustedCertsKids);
        }


        List<SignerInformationEntity> signerInformationEntities = new ArrayList<>();

        for (TrustListItem cert : trustedCerts) {
            if (!alreadyStoredCerts.contains(cert.getKid())) {
                signerInformationEntities.add(getSingerInformationEntity(cert));
                certsToDelete.add(cert.getKid());
            }
        }

        //Delete all certificates that got updated, so that they get a new id.
        signerInformationRepository.deleteByKidIn(certsToDelete);
        signerInformationRepository.saveAllAndFlush(signerInformationEntities);
    }


    private SignerInformationEntity getSingerInformationEntity(TrustListItem cert) {
        SignerInformationEntity signerEntity = new SignerInformationEntity();
        signerEntity.setKid(cert.getKid());
        signerEntity.setCreatedAt(cert.getTimestamp());
        signerEntity.setCountry(cert.getCountry());
        signerEntity.setThumbprint((cert.getThumbprint()));
        signerEntity.setRawData(cert.getRawData());

        return signerEntity;
    }

    /**
     * Gets the deleted/updated state of the certificates.
     * @return state of the certificates represented by their kids
     */
    public DeltaListDto getDeltaList() {

        List<SignerInformationEntity> certs =
            signerInformationRepository.findAllByOrderByIdAsc();

        Map<Boolean,List<String>> partitioned =
            certs.stream().collect(Collectors.partitioningBy(SignerInformationEntity::isDeleted,
                Collectors.mapping(c -> c.getKid(), Collectors.toList())));

        return new DeltaListDto(partitioned.get(Boolean.FALSE),partitioned.get(Boolean.TRUE));

    }

    /**
     * Gets the deleted/updated state of the certificates after the given value.
     * @return state of the certificates represented by their kids
     */
    public DeltaListDto getDeltaList(ZonedDateTime ifModifiedDateTime) {

        List<SignerInformationEntity> certs =
            signerInformationRepository.findAllByUpdatedAtAfterOrderByIdAsc(ifModifiedDateTime);

        Map<Boolean,List<String>> partitioned =
            certs.stream().collect(Collectors.partitioningBy(SignerInformationEntity::isDeleted,
                Collectors.mapping(c -> c.getKid(), Collectors.toList())));

        return new DeltaListDto(partitioned.get(Boolean.FALSE),partitioned.get(Boolean.TRUE));

    }

    /**
     * Gets the raw data of the certificates for a given kid list.
     * @param requestedCertList list of kids
     * @return raw data of certificates
     */
    public Map<String, List<CertificatesLookupResponseItemDto>> getCertificatesData(List<String> requestedCertList) {

        List<SignerInformationEntity> certs =
            signerInformationRepository.findAllByKidIn(requestedCertList);

        return certs.stream().collect(Collectors.groupingBy(SignerInformationEntity::getCountry,
            Collectors.mapping(c -> map(c), Collectors.toList())));
    }

    private CertificatesLookupResponseItemDto map(SignerInformationEntity entity) {
        return new CertificatesLookupResponseItemDto(entity.getKid(), entity.getRawData());
    }

}
