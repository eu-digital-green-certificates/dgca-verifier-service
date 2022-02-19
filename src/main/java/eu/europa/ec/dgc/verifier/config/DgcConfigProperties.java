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

package eu.europa.ec.dgc.verifier.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("dgc")
public class DgcConfigProperties {

    private final CertificatesDownloader certificatesDownloader = new CertificatesDownloader();

    private final TrustedIssuerDownloader trustedIssuerDownloader = new TrustedIssuerDownloader();

    private final String Context = "";

    @Getter
    @Setter
    public static class CertificatesDownloader {
        private Integer timeInterval;
        private Integer lockLimit;
    }

    @Getter
    @Setter
    public static class TrustedIssuerDownloader {
        private boolean enabled;
        private Integer timeInterval;
        private Integer lockLimit;
    }
}
