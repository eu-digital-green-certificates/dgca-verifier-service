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
import eu.europa.ec.dgc.gateway.connector.model.TrustedIssuer;
import eu.europa.ec.dgc.utils.CertificateUtils;
import eu.europa.ec.dgc.verifier.entity.SignerInformationEntity;
import eu.europa.ec.dgc.verifier.entity.TrustedIssuerEntity;
import eu.europa.ec.dgc.verifier.repository.SignerInformationRepository;
import eu.europa.ec.dgc.verifier.repository.TrustedIssuerRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLStreamHandler;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrustedIssuerTestHelper {

    @Autowired
    TrustedIssuerRepository trustedIssuerRepository;


    public TrustedIssuerEntity getIssuer(int number) {
        TrustedIssuerEntity issuer = new TrustedIssuerEntity();

        switch (number) {
            case 1:
                issuer.setEtag("TestEtag");
                issuer.setCreatedAt(ZonedDateTime.parse("2022-04-04T02:21:00Z"));
                issuer.setCountry("DE");
                issuer.setUrl("https://TestUrl.de");
                issuer.setName("example1.de");
                issuer.setUrlType(TrustedIssuerEntity.UrlType.HTTP);
                issuer.setThumbprint("thumbprint1");
                issuer.setSslPublicKey("PublicKey1");
                issuer.setKeyStorageType("JWKS");
                issuer.setSignature("Signature1");
                return issuer;

            case 2:
                issuer.setEtag("TestEtag");
                issuer.setCreatedAt(ZonedDateTime.parse("2022-04-03T03:33:00Z"));
                issuer.setCountry("DE");
                issuer.setUrl("https://TestUrl2.de");
                issuer.setName("example2.de");
                issuer.setUrlType(TrustedIssuerEntity.UrlType.HTTP);
                issuer.setThumbprint("thumbprint2");
                issuer.setSslPublicKey("PublicKey2");
                issuer.setKeyStorageType("JWKS");
                issuer.setSignature("Signature2");
                break;
            default:
                issuer.setEtag("TestEtag");
                issuer.setCreatedAt(ZonedDateTime.parse("2022-04-03T03:33:00Z"));
                issuer.setCountry("DE");
                issuer.setUrl("https://TestUrlDefault.de");
                issuer.setName("exampleDefault.de");
                issuer.setUrlType(TrustedIssuerEntity.UrlType.HTTP);
                issuer.setThumbprint("thumbprintDefault");
                issuer.setSslPublicKey("PublicKeyDefault");
                issuer.setKeyStorageType("JWKS");
                issuer.setSignature("SignatureDefault");
                break;
        }

        return issuer;

    }


    public void insertTrustedIssuer(TrustedIssuerEntity issuer) {
        trustedIssuerRepository.save(issuer);
    }


    public List<TrustedIssuer> getTrustedIssuerList() {
        List<TrustedIssuer> list = new ArrayList<>();

        TrustedIssuer issuer = new TrustedIssuer();
        issuer.setCountry("DE");
        issuer.setUrl("https://ministry-of-health.country-de.de/.well-known/jwks.json");
        issuer.setType(TrustedIssuer.UrlType.HTTP);
        issuer.setThumbprint("8e5b84a5c807f8661e470453119830f2ec27971fce4a3420bb744bad66e5bf4c");
        issuer.setSslPublicKey("MHcCAQEEICdvyZFxcPenETpnkmMf8m7te73UE6olhUB72OpIuGRpoAoGCCqGSM49AwEHoUQDQgAE7ni62sNPT7"
            + "02PoVkwd8+oCJMkDjht8gcFVGSgYNmjUFDXjKuLK/IVl87xQ5G8zNTbIMllwD1JJZB9LElhFb3JA==");
        issuer.setKeyStorageType("JWKS");
        issuer.setSignature("MIAGCSqGSIb3DQEHAqCAMIACAQExDTALBglghkgBZQMEAgEwgAYJKoZIhvcNAQcBAACggDCCBX0wggNloAMCAQICF"
            + "CfArZMSPZ2iPmF85n5LHsj4D5XgMA0GCSqGSIb3DQEBCwUAME4xCzAJBgNVBAYTAkVVMRcwFQYDVQQIDA5FdXJvcGVhbiBVbmlvbjEU"
            + "MBIGA1UECgwLVHJ1c3RBbmNob3IxEDAOBgNVBAsMB1RTVCBFTlYwHhcNMjEwNDIyMDgxNTIyWhcNMzEwNDIwMDgxNTIyWjBOMQswCQY"
            + "DVQQGEwJFVTEXMBUGA1UECAwORXVyb3BlYW4gVW5pb24xFDASBgNVBAoMC1RydXN0QW5jaG9yMRAwDgYDVQQLDAdUU1QgRU5WMIICIj"
            + "ANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA13sh56S2sRAwnS4TCKci0UHGFC1/GxptcaAow2jznzRaJyz7k6oghedDzibFZREen"
            + "g3cO+pw4XpNO8SiWK8w8fipE9TOkbWBNP8cij/yWj+jfyZvVCPY8eXyS5okzS2PNN2lPswdiB5m5BkuXcm8I8d0fgi4bTzT3lwtxlRo"
            + "JZo6LVMFjI/sB3LTYsMiL/OnYozpQWf7Cd6wLJI3c9IiQWFH40dGFFwtdQifDWPjOj9iwMASeCarqtOpNhpkn1ZxCDmqPj1mPqreLdq"
            + "2RCbzrdvuFRs8KsIrjzJFCcBACPzQeP0jFijPhMa9p8BLSwCrlZOz7OEASPqWDstOqBazTUYBvcwGnP2ZcBuXKUS+lN9V+r37J4ANb/"
            + "OpM+iZuPUURxf7OxPa+0INauy6OD8018OleL4svS+8tQadT4G9Nbr/2JqFfqat0FVhaZxQHEyLgQdt70wX1BOctgbCKlGQKBuLMyvyT"
            + "wUJ6Qd0IKxmzFbOVfe+AWHb+V+x8oBpAo+vhS6OCaFuB8dIma1pgf6JP6kfmBERvm8n7158q92ZfGebzhSDhbsuB6Gaj0Ew5qJ/kdzQ"
            + "rZP5QywHZQ8mEum7JR8rygPEEXDRhdtn3CHIDWEt0we+hGU2GchHOrZwMenQKMdxWnNr5/4M6WobefnOk+t2t4aF1ceWd8nXvK2j1l8"
            + "CAwEAAaNTMFEwHQYDVR0OBBYEFK9nb1NMVv4ZzXG7A2alSueXrLBQMB8GA1UdIwQYMBaAFK9nb1NMVv4ZzXG7A2alSueXrLBQMA8GA1"
            + "UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQELBQADggIBAEujCHeHgcqBFeKvt9bAsEDB1QH19+kcd2TdW87GWlA+sYPM3ARwSy5E7JbYj"
            + "yk0pZ/XbDi6qC+CE8OgOyWQaj9CELEZCktXZsdGvOs9dKJd5yf97CLDT9EMp2284Ek67VWp5wqqa1+B6xGTg5r8a0OCNrCR04siQNoQ"
            + "3pq669hQfhmg5iR0sz4JZrgUL6LIukrd5b/kDvaP37xh8gUrYLX5ApdQFuX41FiP/zcwC4/LG4llsAfYw2lh9ZhXqj3VW8SCayYeJ/O"
            + "ExQLM8sHCxJ5NMHoXEvlOjoz+X3/Jib7GHIb0z70EaA8BN6KQ8YPcm+U6sgrjsj501WNAz2GA7ji5Iv/Pet5HGZsYNsDYZSWspe5hbc"
            + "Buc271sVbofLkIXxS8l1mVyhJYj4G+X2DWU3RDoQE+XN8wUdYXcrnKlpp8BKQTOxjofp5xnymCq5GXO50+K1C/tqHjCP1aiir2V1Sb1"
            + "SumgFoJ10bJXCaqCtUX1/7U7f9lGLirAhgN26s4T13hp+8X1D2hMxfo0w/w90fvtcxfSxutoMwwyU917JtPO/8TA+rE07MbnS0SVsYI"
            + "Pg+CVPBHV2jSa1ZVSSsVhJSteG6Hs971ci3kgo4rN/ukosBycylzjBLXBnWfWYAoMb3YoNs1jQJnSyll+N2WxX7vHkKwPrh7OpI9yh+"
            + "IEOnYAAAxggMoMIIDJAIBATBmME4xCzAJBgNVBAYTAkVVMRcwFQYDVQQIDA5FdXJvcGVhbiBVbmlvbjEUMBIGA1UECgwLVHJ1c3RBbm"
            + "Nob3IxEDAOBgNVBAsMB1RTVCBFTlYCFCfArZMSPZ2iPmF85n5LHsj4D5XgMAsGCWCGSAFlAwQCAaCBljAYBgkqhkiG9w0BCQMxCwYJK"
            + "oZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0yMjAzMjUxMTE3MzRaMCsGCSqGSIb3DQEJNDEeMBwwCwYJYIZIAWUDBAIBoQ0GCSqGSIb3"
            + "DQEBCwUAMC8GCSqGSIb3DQEJBDEiBCDRX6mP3IuhUUd3UlbOhbuPYgXXjxeGv+F6IlfEC1aeRTANBgkqhkiG9w0BAQsFAASCAgC23Mz"
            + "bNZgXilk+NjuGPfbqQM2veffsKdA0Ln89ODg7Bjtjc0UKTpIQj/o8K9xR/xLkANxM+jLr1v4ya7CUwG9fCde0lqxozSl/j4+P+9Ir82"
            + "yTDO7AgT0tNpYI+Pa1NzIlRNgqiTVfEg+AmaKLHkg/SJaDa3KxMslkaeQrUwGqaWBLbaMjQFzk/S92s+uRl00At04peXClb87ml6qlO"
            + "BEipjzpcmz/pJPXctBJ38rLSaWyId+Gi+2z5xyClP3N5xUBumVNJZQvkE21cxggUw9CF7m7TPl6O3+6pbkW5ZLrDPOYvGMVH2XYkIJN"
            + "AsxEnJSOIEhCAF2PWaKQ5A2ioHOpEvO7Ao2XHxHYZviH66dibxz1tZKe+lxdn65wChfHimvgmu3qyEVjAW3DcHBK8Vs4vB5xdBcx9Q8"
            + "1tES/w/Q5ML4rIXKHv6aWlg5cpLuxY6q/T39AxxHnn7CZfIhj+A7kFQGQzy98qRj/qUDgTGF2VoEVX5hDRpkINZhStsW5pTVWtppLVc"
            + "CLn7L67FKp8pj8z1S5XY/5akbflY0NPy/a9u71aVHPA+O3RaOlNKG9ZzIKBjApdoDuEEabhwmUmqxbtPhKOSklhv0qOJ1rvuMZLCOha"
            + "S1u3C1KyLok+6WI0oSr+hnLwzR69j9Mcfrq98HjvYpmZgSgOKaRe4XsKIBpNQAAAAAAAA==");

        issuer.setTimestamp(ZonedDateTime.parse("2022-03-25T12:14:49+01:00"));
        issuer.setName("example-de");
        issuer.setDomain("domain");
        issuer.setUuid("b446e0e1-ff8b-45a0-8da0-303caa533ae5");

        list.add(issuer);
        return list;
    }


}
