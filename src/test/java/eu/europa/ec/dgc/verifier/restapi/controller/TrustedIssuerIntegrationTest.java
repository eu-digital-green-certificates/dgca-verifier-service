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

package eu.europa.ec.dgc.verifier.restapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.verifier.repository.TrustedIssuerRepository;
import eu.europa.ec.dgc.verifier.service.InfoService;
import eu.europa.ec.dgc.verifier.testdata.TrustedIssuerTestHelper;

@SpringBootTest
@AutoConfigureMockMvc
class TrustedIssuerIntegrationTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    TrustedIssuerTestHelper trustedIssuerTestHelper;

    @Autowired
    TrustedIssuerRepository trustedIssuerRepository;

    @Autowired
    InfoService infoService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearRepositoryData()  {

        trustedIssuerRepository.deleteAll();
        infoService.setValueForKey(InfoService.CURRENT_ETAG,"TestEtag");
    }

    @Test
    void requestTrustedIssuersIsEmpty() throws Exception {
        mockMvc.perform(get("/trustedissuers"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

    }


    @Test
    void requestTrustedIssuers() throws Exception {
        trustedIssuerTestHelper.insertTrustedIssuer(trustedIssuerTestHelper.getIssuer(1));


        mockMvc.perform(get("/trustedissuers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].url").value("https://TestUrl.de"))
            .andExpect(jsonPath("$[0].type").value("HTTP"))
            .andExpect(jsonPath("$[0].country").value("DE"))
            .andExpect(jsonPath("$[0].thumbprint").value("thumbprint1"))
            .andExpect(jsonPath("$[0].sslPublicKey").value("PublicKey1"))
            .andExpect(jsonPath("$[0].keyStorageType").value("JWKS"))
            .andExpect(jsonPath("$[0].signature").value("Signature1"))
            .andExpect(jsonPath("$[0].name").value("example1.de"));

    }

    @Test
    void requestTrustedIssuersWithHeader() throws Exception {
        trustedIssuerTestHelper.insertTrustedIssuer(trustedIssuerTestHelper.getIssuer(1));


        mockMvc.perform(get("/trustedissuers").header(HttpHeaders.IF_NONE_MATCH, "NoMatchEtag"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].url").value("https://TestUrl.de"))
            .andExpect(jsonPath("$[0].type").value("HTTP"))
            .andExpect(jsonPath("$[0].country").value("DE"))
            .andExpect(jsonPath("$[0].thumbprint").value("thumbprint1"))
            .andExpect(jsonPath("$[0].sslPublicKey").value("PublicKey1"))
            .andExpect(jsonPath("$[0].keyStorageType").value("JWKS"))
            .andExpect(jsonPath("$[0].signature").value("Signature1"))
            .andExpect(jsonPath("$[0].name").value("example1.de"));

    }

    @Test
    void requestTrustedIssuersWithHeaderMatchEtag() throws Exception {
        trustedIssuerTestHelper.insertTrustedIssuer(trustedIssuerTestHelper.getIssuer(1));
        trustedIssuerTestHelper.insertTrustedIssuer(trustedIssuerTestHelper.getIssuer(2));

        mockMvc.perform(get("/trustedissuers").header(HttpHeaders.IF_NONE_MATCH, "TestEtag"))
            .andExpect(status().isNotModified());

    }






}
