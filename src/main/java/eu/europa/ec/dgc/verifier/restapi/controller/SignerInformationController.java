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

import eu.europa.ec.dgc.verifier.entity.SignerInformationEntity;
import eu.europa.ec.dgc.verifier.service.SignerInformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class SignerInformationController {

    private static final String X_RESUME_TOKEN_HEADER = "X-RESUME-TOKEN";
    private static final String X_KID_HEADER = "X-KID";

    private final SignerInformationService signerInformationService;


    /**
     * Http Method for getting signer certificate.
     */
    @GetMapping(path = "/signercertificateUpdate", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(
        summary = "Gets one signer certificate and a resume token.",
        description = "This method return one signer certificate and a corresponding resume token. In order to "
            + "download all available certificates, start calling this method without the resume token set. Then repeat"
            + " to call this method, with the resume token parameter set to the value of the last response. When you "
            + "receive a 204 response you have downloaded all available certificates.",
        tags = {"Signer Information"},
        parameters = {
            @Parameter(
                in = ParameterIn.HEADER,
                name = "X-RESUME-TOKEN",
                description = "Defines where to resume the download of the certificates",
                schema = @Schema(implementation = Long.class))
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns one signer certificate and as header parameter a resume token and the kid. "
                    + "There might be more certificates available to download. Repeat the request with the resume "
                    + "token parameter set to the actual value, until you get a 204 response.",
                headers = {
                    @Header(
                        name = "X-RESUME-TOKEN",
                        description = "Token can be used to resume the download of the certificates."),
                    @Header(
                        name = "X-KID",
                        description = "The kid of the returned certificate.")
                },
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(implementation = String.class),
                    examples = {
                        @ExampleObject(value =
                            "MIIBGzCBwqADAgECAgRggUObMAoGCCqGSM49BAMCMBYxFDASBgNVBAMMC2VkZ2Nf"
                            + "ZGV2X2VjMB4XDTIxMDQyMjA5MzYyN1oXDTIyMDQyMjA5MzYyN1owFjEUMBIGA1UE"
                            + "AwwLZWRnY19kZXZfZWMwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQVQc9JY190"
                            + "s/Jn0CBSq/AWuxmqUzRVu+AsCe6gfbqk3s0e4jonzp5v/5IMW/9t7v5Fu2ITMmOT"
                            + "VfKL1TuM+aixMAoGCCqGSM49BAMCA0gAMEUCIQCGWIk6ZET3afRxdpFVuXdrEYtF"
                            + "iR1MGDx4HweZfspjSgIgBdCJsT746/FI3euIbzKDoeY65m+Qx2/4Cd/vOayNbuw="
                            )})),
            @ApiResponse(
                responseCode = "204",
                description = "No Content available. All certificates already downloaded.",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    public ResponseEntity<String> getSignerCertificateUpdate(
        @RequestHeader(value = X_RESUME_TOKEN_HEADER, required = false) Long resumeToken
    ) {
        HttpHeaders responseHeaders = new HttpHeaders();
        SignerInformationEntity signerInformation = signerInformationService.getCertificate(resumeToken).orElse(null);

        if (signerInformation == null) {
            return ResponseEntity.noContent().build();
        }

        responseHeaders.set(X_RESUME_TOKEN_HEADER, signerInformation.getId().toString());
        responseHeaders.set(X_KID_HEADER, signerInformation.getKid());

        return ResponseEntity.ok()
            .headers(responseHeaders)
            .body(signerInformation.getRawData());
    }


    /**
     * Http Method for getting list of valid certificates ids.
     */
    @GetMapping(path = "/signercertificateStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Gets a list of kids from all valid certificates.",
        tags = {"Signer Information"},
        description = "Gets a list of kids from all valid certificates. This list can be used to verify, that the "
            + "downloaded certificates are still valid. If a kid of a downloaded certificate is not part of the list, "
            + "the certificate is not valid any more.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a list of kids of all valid certificates.",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"8xYtW2837bc=\",\"zoQi+KT68LM=\"]")}))
        })
    public ResponseEntity<List<String>> getSignerCertificateStatus() {

        return ResponseEntity.ok(signerInformationService.getListOfValidKids());
    }

}
