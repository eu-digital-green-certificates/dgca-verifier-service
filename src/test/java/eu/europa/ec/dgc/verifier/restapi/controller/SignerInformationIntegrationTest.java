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

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.verifier.repository.SignerInformationRepository;
import eu.europa.ec.dgc.verifier.testdata.SignerInformationTestHelper;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SignerInformationIntegrationTest {

    private static final String X_RESUME_TOKEN_HEADER = "X-RESUME-TOKEN";
    private static final String X_KID_HEADER = "X-KID";

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    SignerInformationRepository signerInformationRepository;

    @Autowired
    SignerInformationTestHelper signerInformationTestHelper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearRepositoryData()  {
        signerInformationRepository.deleteAll();
    }

    @Test
    void requestCertificatesFromEmptyCertificateList() throws Exception {
        mockMvc.perform(get("/signercertificateUpdate"))
            .andExpect(status().isNoContent());

    }

    @Test
    void requestValidIdListFromEmptyCertificatesList() throws Exception {
        mockMvc.perform(get("/signercertificateStatus"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[]"));
    }

    @Test
    void requestOneCertificate() throws Exception {
        Long certId_1 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_1_STR);

        mockMvc.perform(get("/signercertificateUpdate"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(header().exists(X_KID_HEADER))
            .andExpect(header().exists(X_RESUME_TOKEN_HEADER))
            .andExpect(header().longValue(X_RESUME_TOKEN_HEADER, certId_1))
            .andExpect(header().stringValues(X_KID_HEADER, "8xYtW2837ac="))
            .andExpect(c -> assertCertStrEqual(c, SignerInformationTestHelper.TEST_CERT_1_STR));

        Long certId_2 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_2_STR);

        mockMvc.perform(get("/signercertificateUpdate"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(header().exists(X_KID_HEADER))
            .andExpect(header().exists(X_RESUME_TOKEN_HEADER))
            .andExpect(header().longValue(X_RESUME_TOKEN_HEADER, certId_1))
            .andExpect(header().stringValues(X_KID_HEADER, "8xYtW2837ac="))
            .andExpect(c -> assertCertStrEqual(c, SignerInformationTestHelper.TEST_CERT_1_STR));

    }

    @Test
    void requestValidIdList() throws Exception {
        signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_1_STR);
        mockMvc.perform(get("/signercertificateStatus"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[\"8xYtW2837ac=\"]"));

        signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_2_STR);
        mockMvc.perform(get("/signercertificateStatus"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[\"8xYtW2837ac=\",\"EzVuT0kOpJc=\"]"));

        signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_3_STR);
        mockMvc.perform(get("/signercertificateStatus"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[\"8xYtW2837ac=\",\"EzVuT0kOpJc=\",\"zoQi+KTb8LM=\"]"));
    }

    @Test
    void requestCertificatesWithResumeToken() throws Exception {
        Long certId_1 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_1_STR);

        mockMvc.perform(get("/signercertificateUpdate"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(header().exists(X_KID_HEADER))
            .andExpect(header().exists(X_RESUME_TOKEN_HEADER))
            .andExpect(header().longValue(X_RESUME_TOKEN_HEADER, certId_1))
            .andExpect(header().stringValues(X_KID_HEADER, "8xYtW2837ac="))
            .andExpect(c -> assertCertStrEqual(c, SignerInformationTestHelper.TEST_CERT_1_STR));

        mockMvc.perform(get("/signercertificateUpdate").header(X_RESUME_TOKEN_HEADER, certId_1))
            .andExpect(status().isNoContent());

        Long certId_2 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_2_STR);
        Long certId_3 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_3_STR);

        mockMvc.perform(get("/signercertificateUpdate").header(X_RESUME_TOKEN_HEADER, certId_1))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(header().exists(X_KID_HEADER))
            .andExpect(header().exists(X_RESUME_TOKEN_HEADER))
            .andExpect(header().longValue(X_RESUME_TOKEN_HEADER, certId_2))
            .andExpect(header().stringValues(X_KID_HEADER, "EzVuT0kOpJc="))
            .andExpect(c -> assertCertStrEqual(c, SignerInformationTestHelper.TEST_CERT_2_STR));

        mockMvc.perform(get("/signercertificateUpdate").header(X_RESUME_TOKEN_HEADER, certId_2))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(header().exists(X_KID_HEADER))
            .andExpect(header().exists(X_RESUME_TOKEN_HEADER))
            .andExpect(header().longValue(X_RESUME_TOKEN_HEADER, certId_3))
            .andExpect(header().stringValues(X_KID_HEADER, "zoQi+KTb8LM="))
            .andExpect(c -> assertCertStrEqual(c, SignerInformationTestHelper.TEST_CERT_3_STR));

        mockMvc.perform(get("/signercertificateUpdate").header(X_RESUME_TOKEN_HEADER, certId_3))
            .andExpect(status().isNoContent());

    }

    @Test
    void requestCertificatesFromCertListWithRevokedCerts() throws Exception {
        Long certId_1 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_1_STR);
        Long certId_2 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_2_STR);
        Long certId_3 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_3_STR);

        mockMvc.perform(get("/signercertificateUpdate").header(X_RESUME_TOKEN_HEADER, certId_1))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(header().exists(X_KID_HEADER))
            .andExpect(header().exists(X_RESUME_TOKEN_HEADER))
            .andExpect(header().longValue(X_RESUME_TOKEN_HEADER, certId_2))
            .andExpect(header().stringValues(X_KID_HEADER, "EzVuT0kOpJc="))
            .andExpect(c -> assertCertStrEqual(c, SignerInformationTestHelper.TEST_CERT_2_STR));

        signerInformationRepository.deleteById(certId_2);

        mockMvc.perform(get("/signercertificateUpdate").header(X_RESUME_TOKEN_HEADER, certId_1))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(header().exists(X_KID_HEADER))
            .andExpect(header().exists(X_RESUME_TOKEN_HEADER))
            .andExpect(header().longValue(X_RESUME_TOKEN_HEADER, certId_3))
            .andExpect(header().stringValues(X_KID_HEADER, "zoQi+KTb8LM="))
            .andExpect(c -> assertCertStrEqual(c, SignerInformationTestHelper.TEST_CERT_3_STR));
    }

    @Test
    void requestValidIdListFromCertListWithRevokedCert() throws Exception {
        Long certId_1 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_1_STR);
        Long certId_2 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_2_STR);
        Long certId_3 = signerInformationTestHelper.insertCertString(SignerInformationTestHelper.TEST_CERT_3_STR);

        mockMvc.perform(get("/signercertificateStatus"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[\"8xYtW2837ac=\",\"EzVuT0kOpJc=\",\"zoQi+KTb8LM=\"]"));

        signerInformationRepository.deleteById(certId_2);

        mockMvc.perform(get("/signercertificateStatus"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[\"8xYtW2837ac=\",\"zoQi+KTb8LM=\"]"));

    }

    private void assertCertStrEqual(MvcResult result, String certStr) throws  UnsupportedEncodingException {
        String resultCert = result.getResponse().getContentAsString();

        Assertions.assertEquals(certStr, resultCert);

    }



}