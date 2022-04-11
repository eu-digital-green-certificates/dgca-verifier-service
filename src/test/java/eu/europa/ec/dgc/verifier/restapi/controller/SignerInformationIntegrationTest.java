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
import java.time.ZonedDateTime;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void requestDeltaNoHeader() throws Exception {
        ZonedDateTime date1 = ZonedDateTime.parse("2022-04-13T02:21:00Z");
        Long certId_1 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_1_STR,
            "de", "thumbp1", date1, false);

        Long certId_2 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_2_STR,
            "de", "thumbp2", date1, true);

        mockMvc.perform(get("/signercertificateStatus/delta"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("{\"updated\":[\"8xYtW2837ac=\"],\"deleted\":[\"EzVuT0kOpJc=\"]}"));

    }

    @Test
    void requestDeltaWithHeader() throws Exception {
        ZonedDateTime date1 = ZonedDateTime.parse("2022-04-04T02:21:00Z");
        ZonedDateTime date2 = ZonedDateTime.parse("2022-04-13T02:21:00Z");
        Long certId_1 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_1_STR,
            "de", "thumbp1", date1, false);

        Long certId_2 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_2_STR,
            "de", "thumbp2", date2, false);

        Long certId_3 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_3_STR,
            "de", "thumbp3", date2, true);

        mockMvc.perform(get("/signercertificateStatus/delta"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().
                json("{\"updated\":[\"8xYtW2837ac=\",\"EzVuT0kOpJc=\"],\"deleted\":[\"zoQi+KTb8LM=\"]}"));

        mockMvc.perform(get("/signercertificateStatus/delta")
                .header("if-modified-since","2022-04-04T02:20:00Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().
                json("{\"updated\":[\"8xYtW2837ac=\",\"EzVuT0kOpJc=\"],\"deleted\":[\"zoQi+KTb8LM=\"]}"));

        mockMvc.perform(get("/signercertificateStatus/delta")
                .header("if-modified-since","2022-04-04T02:21:00Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().
                json("{\"updated\":[\"EzVuT0kOpJc=\"],\"deleted\":[\"zoQi+KTb8LM=\"]}"));

        mockMvc.perform(get("/signercertificateStatus/delta")
                .header("if-modified-since","2022-04-13T02:21:00Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().
                json("{\"updated\":[],\"deleted\":[]}"));

        mockMvc.perform(get("/signercertificateStatus/delta")
                .header("if-modified-since","Mon, 04 Apr 2022 02:21:00 GMT"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().
                json("{\"updated\":[\"EzVuT0kOpJc=\"],\"deleted\":[\"zoQi+KTb8LM=\"]}"));

    }

    @Test
    void requestDeltaBadRequest() throws Exception {

        mockMvc.perform(get("/signercertificateStatus/delta")
                .header("if-modified-since","NotValid"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Can not parse if-modified-since header"));

    }

    @Test
    void requestCertificateData() throws Exception {
        ZonedDateTime date1 = ZonedDateTime.parse("2022-04-13T02:21:00Z");
        Long certId_1 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_1_STR,
            "de", "thumbp1", date1, false);

        Long certId_2 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_2_STR,
            "de", "thumbp2", date1, true);

        mockMvc.perform(post("/signercertificateUpdate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("[\"8xYtW2837ac=\",\"EzVuT0kOpJc=\"]")
            .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("{\"de\":[{\"kid\":\"8xYtW2837ac=\","
                + "\"rawData\":\"MIICrDCCAZSgAwIBAgIEYH+7ujANBgkqhkiG9w0BAQsFADAYMRYwFAYDVQQDDA1lZGdjX2Rldl90ZXN0MB4XDT"
                + "IxMDQyMTA1NDQyNloXDTIyMDQyMTA1NDQyNlowGDEWMBQGA1UEAwwNZWRnY19kZXZfdGVzdDCCASIwDQYJKoZIhvcNAQEBBQADgg"
                + "EPADCCAQoCggEBAOAlpphOE0TH2m+jU6prmP1W6N0ajaExs5X+sxxG58hIGnZchxFkLkeYSZqyC2bPQtPiYIDgVFcPJPgfRO4r5e"
                + "x3W7OxQCFS0TJmYhRkLiVQHQDNHeXFmOpu834x2ErPJ8AK2D9KhVyFKl5OX1euU25IXzXs67vQf30eStArvWFlZGX4E+JUy8yIwr"
                + "R6WLRe+kgtBdFmJZJywbnnffg/5WT+TEcky8ugBlsEcyTxI5rt6iW5ptNUphui8ZGaE2KtjcnZVaPCvn1IjEv6sdWS/DNDlFySuJ"
                + "6LQD1OnKsjCXrNVZFVZS5ae9snPu4Y/gapzdgeSDioRk6BWwZ02E9BE+8CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEApE8H9uGtB6"
                + "DuDL3LEqGslyJKyc6EBqJ+4hDlFtPe+13xEDomJsNwq1Uk3p9F1aHgqqXc1MjJfDWn0l7ZDGh02tfi+EgHyV2vrfqZwXm6vuK/P7"
                + "fzdb5blLJpKt0NoMCzY+lHhkCxcRGX1R8QOGuuGtnepDrtyeTuoQqsh0mdcMuFgKuTr3c3kKpoQwBWquG/eZ0PhKSkqXy5aEaFAz"
                + "dXBLq/dh4zn8FVx+STSpKK1WNmoqjtL7EEFcNgxLTjWJFjusTEZL0Yxa4Ot4Gb6+VK7P34olH7pFcBFYfh6DyOESV9uglrE4kdOQ"
                + "7+x+yS5zR/UTeEfM4mW4I2QIEreUN8Jg==\"},"
                + "{\"kid\":\"EzVuT0kOpJc=\",\"rawData\":\"MIIBGzCBwqADAgECAgRggU"
                + "ObMAoGCCqGSM49BAMCMBYxFDASBgNVBAMMC2VkZ2NfZGV2X2VjMB4XDTIxMDQyMjA5MzYyN1oXDTIyMDQyMjA5MzYyN1owFjEUMB"
                + "IGA1UEAwwLZWRnY19kZXZfZWMwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQVQc9JY190s/Jn0CBSq/AWuxmqUzRVu+AsCe6gfb"
                + "qk3s0e4jonzp5v/5IMW/9t7v5Fu2ITMmOTVfKL1TuM+aixMAoGCCqGSM49BAMCA0gAMEUCIQCGWIk6ZET3afRxdpFVuXdrEYtFiR"
                + "1MGDx4HweZfspjSgIgBdCJsT746/FI3euIbzKDoeY65m+Qx2/4Cd/vOayNbuw=\"}]}"));

    }

    @Test
    void requestCertificateDataNotExist() throws Exception {
        ZonedDateTime date1 = ZonedDateTime.parse("2022-04-13T02:21:00Z");
        Long certId_1 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_1_STR,
            "de", "thumbp1", date1, false);

        Long certId_2 = signerInformationTestHelper.insertCertString(
            SignerInformationTestHelper.TEST_CERT_2_STR,
            "de", "thumbp2", date1, true);

        mockMvc.perform(post("/signercertificateUpdate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"NotAvailable=\"]")
                .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("{}"));

    }




    private void assertCertStrEqual(MvcResult result, String certStr) throws  UnsupportedEncodingException {
        String resultCert = result.getResponse().getContentAsString();

        Assertions.assertEquals(certStr, resultCert);

    }



}
