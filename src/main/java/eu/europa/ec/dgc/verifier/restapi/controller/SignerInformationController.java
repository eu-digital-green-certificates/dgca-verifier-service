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
        summary = "Gets one signer certificate.",
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
                description = "Returns a filtered list of trusted certificates.",
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
        summary = "Gets list of kids from all valid certificates.",
        tags = {"Signer Information"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a filtered list of trusted certificates.",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"8xYtW2837bc=\",\"zoQi+KT68LM=\"]")}))
        })
    public ResponseEntity<List<String>> getSignerCertificateStatus() {

        return ResponseEntity.ok(signerInformationService.getListOfValidKids());
    }

}
