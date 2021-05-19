package eu.europa.ec.dgc.verifier.service;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import eu.europa.ec.dgc.verifier.entity.SignerInformationEntity;
import eu.europa.ec.dgc.verifier.repository.SignerInformationRepository;
import eu.europa.ec.dgc.verifier.testdata.SignerInformationTestHelper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class SignerCertificateDownloadServiceImplTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    SignerCertificateDownloadServiceImpl signerCertificateDownloadService;

    @Autowired
    SignerInformationRepository signerInformationRepository;

    @Autowired
    SignerInformationTestHelper signerInformationTestHelper;

    @Test
    void downloadEmptyCertificatesList() {
        ArrayList<TrustListItem> trustList = new ArrayList<>();
        Mockito.when(dgcGatewayDownloadConnector.getTrustedCertificates()).thenReturn(trustList);

        signerCertificateDownloadService.downloadCertificates();

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();
        Assertions.assertEquals(0, repositoryItems.size());
    }

    @Test
    void downloadCertificates() {
        ArrayList<TrustListItem> trustList = new ArrayList<>();
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_1_STR));
        Mockito.when(dgcGatewayDownloadConnector.getTrustedCertificates()).thenReturn(trustList);

        signerCertificateDownloadService.downloadCertificates();

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();
        Assertions.assertEquals(1, repositoryItems.size());

        SignerInformationEntity repositoryItem = repositoryItems.get(0);
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_1_KID, repositoryItem.getKid());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_1_STR, repositoryItem.getRawData());
    }
}
