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

import eu.europa.ec.dgc.verifier.dto.TrustedIssuerDto;
import eu.europa.ec.dgc.verifier.mapper.IssuerMapper;
import eu.europa.ec.dgc.verifier.service.TrustedIssuerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
public class TrustedIssuerController {


    private final TrustedIssuerService trustedIssuerService;

    private final IssuerMapper issuerMapper;


    /**
     * Http Method for getting trusted issuers.
     */
    @GetMapping(path = "/trustedissuers", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Gets one trusted issuers",
        description = "This method return a list of trusted issuers.",
        tags = {"Trusted Issuers"},
        parameters = {
            @Parameter(
                in = ParameterIn.HEADER,
                name = "IF-NONE-MATCH",
                description = "When the eTag matches the current Tag, there is a 304 response.",
                required = false,
                schema = @Schema(implementation = String.class))
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns the the trusted issuers list.",
                headers = @Header(name = HttpHeaders.ETAG, description = "ETAG of the current data set"),
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema =
                    @Schema(implementation = TrustedIssuerDto.class)))),
            @ApiResponse(
                responseCode = "304",
                description = "Not modified.")
        }
    )
    public ResponseEntity<List<TrustedIssuerDto>> getTrustedIssuers(
        @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, defaultValue = "") String ifNoneMatch) {

        String currentEtag = trustedIssuerService.getEtag();

        if (ifNoneMatch.equals(currentEtag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity.ok().eTag(currentEtag).body(issuerMapper.trustedIssuerEntityToTrustedIssuerDto(
            trustedIssuerService.getAllIssuers(currentEtag)));
    }

}
