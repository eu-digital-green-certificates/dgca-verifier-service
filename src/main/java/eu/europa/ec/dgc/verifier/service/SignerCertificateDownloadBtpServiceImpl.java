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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import eu.europa.ec.dgc.gateway.connector.dto.TrustListItemDto;
import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>Demo implementation to connect to the Digital Green Certificate Gateway on SAP's Business Technology Plattform.
 * This implementation serves as a reference where you already have an endpoint to the gateway provided by your runtime
 * environment and using the connector from the dgc-lib on top would be superfluous.</p>
 * <p>In this case the endpoint is fully configured via the destination API available on BTP.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Profile("btp")
public class SignerCertificateDownloadBtpServiceImpl implements SignerCertificateDownloadService {

    private static final String DGCG_DESTINATION = "dgcg-destination";
    private static final String DGCG_TRUST_LIST_CSCA_ENDPOINT = "/trustList/CSCA";
    private static final String DGCG_TRUST_LIST_DSC_ENDPOINT = "/trustList/DSC";

    private final SignerInformationService signerInformationService;

    @PostConstruct
    private void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    @Scheduled(fixedDelayString = "${dgc.certificatesDownloader.timeInterval}")
    @SchedulerLock(name = "SignerCertificateDownloadService_downloadCertificates", lockAtLeastFor = "PT0S",
        lockAtMostFor = "${dgc.certificatesDownloader.lockLimit}")
    public void downloadCertificates() {
        HttpDestination httpDestination = DestinationAccessor.getDestination(DGCG_DESTINATION).asHttp();
        HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        List<X509CertificateHolder> cscas = downloadCsca(httpClient);
        List<TrustListItem> dscs = downloadDsc(httpClient, cscas);

        signerInformationService.updateTrustedCertsList(dscs);
    }

    private List<X509CertificateHolder> downloadCsca(HttpClient httpClient) {
        List<X509CertificateHolder> listOfCsca = new ArrayList<>();

        try {
            HttpResponse response = httpClient.execute(RequestBuilder.get(DGCG_TRUST_LIST_CSCA_ENDPOINT).build());
            List<TrustListItemDto> trustListItems = gson().fromJson(toJsonString(response.getEntity()),
                new TypeToken<List<TrustListItemDto>>() {}.getType());

            listOfCsca = trustListItems.stream().map(this::getCertificateFromTrustListItem)
                .filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Fetching signer information from gateway failed: {}", e.getMessage(), e);
        }

        return listOfCsca;
    }

    private List<TrustListItem> downloadDsc(HttpClient httpClient, List<X509CertificateHolder> cscas) {
        List<TrustListItem> listOfDsc = new ArrayList<>();

        try {
            HttpResponse response = httpClient.execute(RequestBuilder.get(DGCG_TRUST_LIST_DSC_ENDPOINT).build());
            List<TrustListItemDto> trustListItems = gson().fromJson(toJsonString(response.getEntity()),
                new TypeToken<List<TrustListItemDto>>() {}.getType());

            listOfDsc = trustListItems.stream().filter(dsc -> cscas.stream().anyMatch(
                ca -> trustListItemSignedByCa(dsc, ca))).map(this::map).filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Fetching signer information from gateway failed: {}", e.getMessage(), e);
        }

        return listOfDsc;
    }

    private String toJsonString(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity);
    }

    private Gson gson() {
        return new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
            @Override
            public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                out.value(value.toString());
            }

            @Override
            public ZonedDateTime read(JsonReader in) throws IOException {
                return ZonedDateTime.parse(in.nextString());
            }
        })
            .enableComplexMapKeySerialization()
            .create();
    }

    private X509CertificateHolder getCertificateFromTrustListItem(TrustListItemDto trustListItem) {
        byte[] decodedBytes = Base64.getDecoder().decode(trustListItem.getRawData());
        try {
            return new X509CertificateHolder(decodedBytes);
        } catch (IOException e) {
            log.error("Failed to parse Certificate Raw Data. KID: {}, Country: {}", trustListItem.getKid(),
                trustListItem.getCountry());
            return null;
        }
    }

    private TrustListItem map(TrustListItemDto trustListItemDto) {
        if (trustListItemDto == null) {
            return null;
        } else {
            TrustListItem trustListItem = new TrustListItem();
            trustListItem.setKid(trustListItemDto.getKid());
            trustListItem.setTimestamp(trustListItemDto.getTimestamp());
            trustListItem.setRawData(trustListItemDto.getRawData());
            return trustListItem;
        }
    }

    private boolean trustListItemSignedByCa(TrustListItemDto certificate, X509CertificateHolder ca) {
        ContentVerifierProvider verifier;
        try {
            verifier = (new JcaContentVerifierProviderBuilder()).build(ca);
        } catch (CertificateException | OperatorCreationException e) {
            log.error("Failed to instantiate JcaContentVerifierProvider from cert. KID: {}, Country: {}",
                certificate.getKid(), certificate.getCountry());
            return false;
        }

        X509CertificateHolder dcs;
        try {
            dcs = new X509CertificateHolder(Base64.getDecoder().decode(certificate.getRawData()));
        } catch (IOException e) {
            log.error("Could not parse certificate. KID: {}, Country: {}", certificate.getKid(),
                certificate.getCountry());
            return false;
        }

        try {
            return dcs.isSignatureValid(verifier);
        } catch (RuntimeOperatorException | CertException e) {
            log.trace("Could not verify that certificate was issued by ca. Certificate: {}, CA: {}",
                dcs.getSubject().toString(), ca.getSubject().toString());
            return false;
        }
    }

}
