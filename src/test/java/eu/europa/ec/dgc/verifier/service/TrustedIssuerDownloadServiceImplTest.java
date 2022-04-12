package eu.europa.ec.dgc.verifier.service;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.DgcGatewayTrustedIssuerDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import eu.europa.ec.dgc.gateway.connector.model.TrustedIssuer;
import eu.europa.ec.dgc.verifier.entity.SignerInformationEntity;
import eu.europa.ec.dgc.verifier.entity.TrustedIssuerEntity;
import eu.europa.ec.dgc.verifier.repository.SignerInformationRepository;
import eu.europa.ec.dgc.verifier.repository.TrustedIssuerRepository;
import eu.europa.ec.dgc.verifier.testdata.SignerInformationTestHelper;
import eu.europa.ec.dgc.verifier.testdata.TrustedIssuerTestHelper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = {"dgc.trustedIssuerDownloader.enabled=true"})
class TrustedIssuerDownloadServiceImplTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnectorMock;

    @MockBean
    DgcGatewayTrustedIssuerDownloadConnector dgcGatewayDownloadConnector;


    @Autowired
    TrustedIssuerDownloadServiceImpl trustedIssuerDownloadService;

    @Autowired
    TrustedIssuerRepository trustedIssuerRepository;

    @Autowired
    TrustedIssuerTestHelper trustedIssuerTestHelper;

    @Test
    void downloadEmptyIssuerList() {
        ArrayList<TrustedIssuer> trustList = new ArrayList<>();
        Mockito.when(dgcGatewayDownloadConnector.getTrustedIssuers()).thenReturn(trustList);

        trustedIssuerDownloadService.downloadTrustedIssuers();

        List<TrustedIssuerEntity> repositoryItems = trustedIssuerRepository.findAll();
        Assertions.assertEquals(0, repositoryItems.size());
    }

    @Test
    void downloadIssuers() {
        List<TrustedIssuer> trustedIssuers = trustedIssuerTestHelper.getTrustedIssuerList();

        Mockito.when(dgcGatewayDownloadConnector.getTrustedIssuers()).thenReturn(trustedIssuers);

        trustedIssuerDownloadService.downloadTrustedIssuers();

        List<TrustedIssuerEntity> repositoryItems = trustedIssuerRepository.findAll();
        Assertions.assertEquals(1, repositoryItems.size());

        TrustedIssuer trustedIssuer = trustedIssuers.get(0);

        TrustedIssuerEntity repositoryItem = repositoryItems.get(0);
        Assertions.assertEquals(trustedIssuer.getCountry(), repositoryItem.getCountry());
        Assertions.assertEquals(trustedIssuer.getKeyStorageType(), repositoryItem.getKeyStorageType());
        Assertions.assertEquals(trustedIssuer.getName(), repositoryItem.getName());
        Assertions.assertEquals(trustedIssuer.getSignature(), repositoryItem.getSignature());
        Assertions.assertEquals(trustedIssuer.getThumbprint(), repositoryItem.getThumbprint());
        Assertions.assertEquals(trustedIssuer.getSslPublicKey(), repositoryItem.getSslPublicKey());
        Assertions.assertEquals(trustedIssuer.getUrl(), repositoryItem.getUrl());
        Assertions.assertEquals(trustedIssuer.getType().toString(), repositoryItem.getUrlType().toString());
    }
}
