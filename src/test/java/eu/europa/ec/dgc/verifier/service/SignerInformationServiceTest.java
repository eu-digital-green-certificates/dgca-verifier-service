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
import eu.europa.ec.dgc.verifier.entity.SignerInformationEntity;
import eu.europa.ec.dgc.verifier.repository.SignerInformationRepository;
import eu.europa.ec.dgc.verifier.testdata.SignerInformationTestHelper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class SignerInformationServiceTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    SignerInformationRepository signerInformationRepository;

    @Autowired
    SignerInformationService signerInformationService;

    @Autowired
    SignerInformationTestHelper signerInformationTestHelper;

    @BeforeEach
    void clearRepositoryData()  {
        signerInformationRepository.deleteAll();
    }


    @Test
    void updateEmptyRepositoryWithEmptyCertList()  {
        ArrayList<TrustListItem> trustList = new ArrayList<>();

        signerInformationService.updateTrustedCertsList(trustList);

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();

        Assertions.assertEquals(0, repositoryItems.size());

    }

    @Test
    void updateEmptyRepositoryWithOneCert()  {
        ArrayList<TrustListItem> trustList = new ArrayList<>();
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_1_STR));

        signerInformationService.updateTrustedCertsList(trustList);

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();

        Assertions.assertEquals(1, repositoryItems.size());

        SignerInformationEntity repositoryItem = repositoryItems.get(0);

        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_1_KID, repositoryItem.getKid());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_1_STR, repositoryItem.getRawData());

    }

    @Test
    void updateEmptyRepositoryWithCerts()  {
        ArrayList<TrustListItem> trustList = new ArrayList<>();
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_1_STR));
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_2_STR));
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_3_STR));

        signerInformationService.updateTrustedCertsList(trustList);

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();

        Assertions.assertEquals(3, repositoryItems.size());

        SignerInformationEntity repositoryItem = repositoryItems.get(0);
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_1_KID, repositoryItem.getKid());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_1_STR, repositoryItem.getRawData());
        repositoryItem = repositoryItems.get(1);
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_2_KID, repositoryItem.getKid());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_2_STR, repositoryItem.getRawData());
        repositoryItem = repositoryItems.get(2);
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_3_KID, repositoryItem.getKid());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_3_STR, repositoryItem.getRawData());

    }

    @Test
    void updateEmptyRepositoryWithSameCertsTwice() {
        ArrayList<TrustListItem> trustList = new ArrayList<>();
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_1_STR));
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_2_STR));
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_3_STR));

        signerInformationService.updateTrustedCertsList(trustList);

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();

        Assertions.assertEquals(3, repositoryItems.size());

        SignerInformationEntity repositoryItem0 = repositoryItems.get(0);
        SignerInformationEntity repositoryItem1 = repositoryItems.get(1);
        SignerInformationEntity repositoryItem2 = repositoryItems.get(2);

        signerInformationService.updateTrustedCertsList(trustList);

        repositoryItems = signerInformationRepository.findAll();

        Assertions.assertEquals(3, repositoryItems.size());

        SignerInformationEntity repositoryItem = repositoryItems.get(0);
        Assertions.assertEquals(repositoryItem0, repositoryItem);
        repositoryItem = repositoryItems.get(1);
        Assertions.assertEquals(repositoryItem1, repositoryItem);
        repositoryItem = repositoryItems.get(2);
        Assertions.assertEquals(repositoryItem2, repositoryItem);
    }

    @Test
    void updateRepositoryWithOneNewCertAndOneRevoked() {
        signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_1_STR);
        signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_2_STR);


        ArrayList<TrustListItem> trustList = new ArrayList<>();
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_2_STR));
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_3_STR));

        signerInformationService.updateTrustedCertsList(trustList);

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();

        Assertions.assertEquals(2, repositoryItems.size());

        SignerInformationEntity repositoryItem0 = repositoryItems.get(0);
        SignerInformationEntity repositoryItem1 = repositoryItems.get(1);

        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_2_KID, repositoryItem0.getKid());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_2_STR, repositoryItem0.getRawData());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_3_KID, repositoryItem1.getKid());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_3_STR, repositoryItem1.getRawData());

    }

    @Test
    void updateRepositoryWithEmptyCertList() {
        signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_1_STR);
        signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_2_STR);
        signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_3_STR);

        ArrayList<TrustListItem> trustList = new ArrayList<>();

        signerInformationService.updateTrustedCertsList(trustList);

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();

        Assertions.assertEquals(0, repositoryItems.size());

    }

}
