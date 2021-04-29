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


import eu.europa.ec.dgc.verifier.entity.SignerInformationEntity;
import eu.europa.ec.dgc.verifier.repository.SignerInformationRepository;
import eu.europa.ec.dgc.verifier.restapi.dto.KidDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        ArrayList<String> responseArray = new ArrayList<>();

        List<KidDto> validIds = signerInformationRepository.findAllByOrderByIdAsc().orElse(null);

        if (validIds != null) {
            for (KidDto validId : validIds) {
                responseArray.add(validId.getKid());
            }
        }
        return responseArray;
    }

}
