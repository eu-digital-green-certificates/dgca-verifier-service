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

package eu.europa.ec.dgc.verifier.testdata;

import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import eu.europa.ec.dgc.utils.CertificateUtils;
import eu.europa.ec.dgc.verifier.entity.SignerInformationEntity;
import eu.europa.ec.dgc.verifier.repository.SignerInformationRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignerInformationTestHelper {

    public static final String TEST_CERT_1_STR =
        "MIICrDCCAZSgAwIBAgIEYH+7ujANBgkqhkiG9w0BAQsFADAYMRYwFAYDVQQDDA1l"
            + "ZGdjX2Rldl90ZXN0MB4XDTIxMDQyMTA1NDQyNloXDTIyMDQyMTA1NDQyNlowGDEW"
            + "MBQGA1UEAwwNZWRnY19kZXZfdGVzdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC"
            + "AQoCggEBAOAlpphOE0TH2m+jU6prmP1W6N0ajaExs5X+sxxG58hIGnZchxFkLkeY"
            + "SZqyC2bPQtPiYIDgVFcPJPgfRO4r5ex3W7OxQCFS0TJmYhRkLiVQHQDNHeXFmOpu"
            + "834x2ErPJ8AK2D9KhVyFKl5OX1euU25IXzXs67vQf30eStArvWFlZGX4E+JUy8yI"
            + "wrR6WLRe+kgtBdFmJZJywbnnffg/5WT+TEcky8ugBlsEcyTxI5rt6iW5ptNUphui"
            + "8ZGaE2KtjcnZVaPCvn1IjEv6sdWS/DNDlFySuJ6LQD1OnKsjCXrNVZFVZS5ae9sn"
            + "Pu4Y/gapzdgeSDioRk6BWwZ02E9BE+8CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEA"
            + "pE8H9uGtB6DuDL3LEqGslyJKyc6EBqJ+4hDlFtPe+13xEDomJsNwq1Uk3p9F1aHg"
            + "qqXc1MjJfDWn0l7ZDGh02tfi+EgHyV2vrfqZwXm6vuK/P7fzdb5blLJpKt0NoMCz"
            + "Y+lHhkCxcRGX1R8QOGuuGtnepDrtyeTuoQqsh0mdcMuFgKuTr3c3kKpoQwBWquG/"
            + "eZ0PhKSkqXy5aEaFAzdXBLq/dh4zn8FVx+STSpKK1WNmoqjtL7EEFcNgxLTjWJFj"
            + "usTEZL0Yxa4Ot4Gb6+VK7P34olH7pFcBFYfh6DyOESV9uglrE4kdOQ7+x+yS5zR/"
            + "UTeEfM4mW4I2QIEreUN8Jg==";

    public static final String TEST_CERT_1_KID = "8xYtW2837ac=";

    public static final String TEST_CERT_2_STR =
        "MIIBGzCBwqADAgECAgRggUObMAoGCCqGSM49BAMCMBYxFDASBgNVBAMMC2VkZ2Nf"
            + "ZGV2X2VjMB4XDTIxMDQyMjA5MzYyN1oXDTIyMDQyMjA5MzYyN1owFjEUMBIGA1UE"
            + "AwwLZWRnY19kZXZfZWMwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQVQc9JY190"
            + "s/Jn0CBSq/AWuxmqUzRVu+AsCe6gfbqk3s0e4jonzp5v/5IMW/9t7v5Fu2ITMmOT"
            + "VfKL1TuM+aixMAoGCCqGSM49BAMCA0gAMEUCIQCGWIk6ZET3afRxdpFVuXdrEYtF"
            + "iR1MGDx4HweZfspjSgIgBdCJsT746/FI3euIbzKDoeY65m+Qx2/4Cd/vOayNbuw=";

    public static final String TEST_CERT_2_KID = "EzVuT0kOpJc=";

    public static final String TEST_CERT_3_STR =
        "MIIDqDCCAhCgAwIBAgIEYIFDEjANBgkqhkiG9w0BAQsFADAWMRQwEgYDVQQDDAtl"
            + "ZGdjX2Rldl9kZTAeFw0yMTA0MjIwOTM0MTBaFw0yMjA0MjIwOTM0MTBaMBYxFDAS"
            + "BgNVBAMMC2VkZ2NfZGV2X2RlMIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKC"
            + "AYEAt1aoSm/JB7qth70XBPR4avb1wcKHpBLFBDZIZnHzKYWvIy6JIXgd342tK825"
            + "0jOJ5UC1SVJdtAckWEkV5HYQ3qJ7qr6booEQzK64lLSk6oimjOnnIOFWEIrPPqW+"
            + "nQFOyw96opf6ISiyVvUipJVFuQC2RE3Ci/yKGBO7LeMQi2FDw+edo4/HtsmJlkEz"
            + "8JxnCniwjTRCnRGNAs7YXMlrwcCcyIarDtxbdbcwm/6WpuOnj8MzTAAUXQ+SeFOq"
            + "MlvUosKxL34nJ7liHySu6uuGCopFSvuRh3yIuwAqeufGVKBfoiJkrtsn+AB/Q/kP"
            + "XpPR7Dk2NybbJX3g+dh2ok08zpbVcYBRrtITXPZIQvLuZXMd1CUnNz0aOWNAxT6P"
            + "v4R4ROavuQcjJR785mspCovqXCy8SpD4JHs+HxYqE7RTWzd3j4HmPf7NuWMnlH04"
            + "J2h10V/EffHu65+wQ4s9dMCRLttOBScV6EAgRLoCt11tvc8XUxzI0yq17YntZDr2"
            + "1SjJAgMBAAEwDQYJKoZIhvcNAQELBQADggGBABIWLWx/RQ3WQoHXmbLhkTTtM2b3"
            + "Q/TZCXz5ZB89l/CrTeLQ+hy5pYv5HUTz00JnikyxbfVwsNhfVRMYm0NVJf6WWqHB"
            + "OIk9MKAxksJ49QFHdL2sW4Vm5XhGy2FDaEgtx58q3koNHY9e5FyOcEZcXo2+eXKO"
            + "bOsj80RJV5aj53SWY3Si+sq9iJMGYghskaEs/rnWn65ullbUKuC1+vkOV3qfFPKo"
            + "CxeHlmGzdokRzbVKtXjDqb/edRX6I4k7laZ0+irFQqftvkaMHVEf13nXTIgQ9rpp"
            + "+JQ0Y2pWSLPnWf/dah/D0/NmwI6E6V5+9U6i73RcalGw97gfyorMkYFFE8ByLdfp"
            + "n76oTgJaXN/CQDLm2yzOX/ynt4t0ycqcVYrzewiKY2Fpnhao4U00vrh+0lwdUFr3"
            + "jpOMeNg/2UDYhpWwWiT1ik+D6PSfKQ7Amuph6VcYEy/grQxNxPWcghoZSVKdXhOz"
            + "6ggdK/eFNlO1aYj/DLxV3ZWcrAYk6dS4rnn8Ow==";

    public static final String TEST_CERT_3_KID = "zoQi+KTb8LM=";

    private final SignerInformationRepository signerInformationRepository;
    private final CertificateUtils certificateUtils;

    private X509Certificate convertStringToX509Cert(String certificate) throws CertificateException {
        InputStream targetStream = new ByteArrayInputStream(Base64.getDecoder().decode(certificate));
        return (X509Certificate) CertificateFactory
            .getInstance("X509")
            .generateCertificate(targetStream);
    }

    public Long insertCertString(String certStr) {
        String kid;
        try {
            kid = certificateUtils.getCertKid(convertStringToX509Cert(certStr));
        }catch (CertificateException e) {
            kid = "kid_"+ ZonedDateTime.now();
        }

        SignerInformationEntity cert = new SignerInformationEntity(
            null,
            kid,
            ZonedDateTime.now(),
            certStr
        );

        signerInformationRepository.save(cert);

        return cert.getId();
    }

    public TrustListItem createTrustListItem(String certStr) {
        String kid;
        try {
            kid = certificateUtils.getCertKid(convertStringToX509Cert(certStr));
        }catch (CertificateException e) {
            kid = "kid_"+ ZonedDateTime.now();
        }

        TrustListItem item = new TrustListItem();
        item.setKid(kid);
        item.setTimestamp(ZonedDateTime.now());
        item.setRawData(certStr);

        return item;
    }
}
