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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/context")
@Slf4j
public class ContextController {

    /**
     * Http Method for getting the current context.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Provide configuration information for the verifier app",
        tags = {"Configuration"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns the current context for the verifier app.",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = String.class),
                    examples = {@ExampleObject(value = "{\"origin\":\"DE\",\"versions\":{\"default\":{\"privacyUrl\":"
                          + "\"https://publications.europa.eu/en/web/about-us/legal-notices/eu-mobile-apps\","
                          + "\"context\":{\"url\":\"https://dgca-verifier-service.example.com/context\",\"pubKeys\":"
                          + "[\"lKdU1EbQubxyDDm2q3N8KclZ2C94+3eI=\","
                          + "\"r/mIkG3eEpVdm+u/ko/cwxzOMo1bkA5E=\"]},\"endpoints\":{\"status\":{\"url\":"
                          + "\"https://dgca-verifier-service.example.com/signercertificateStatus\",\"pubKeys\":"
                          + "[\"lKdU1EbQubxyDDm2q3N8KclZ2C94+3eI=\","
                          + "\"r/mIkG3eEpVdm+u/ko/cwxzOMo1bkA5E=\"]},\"update\":{\"url\":"
                          + "\"https://dgca-verifier-service.example.com/signercertificateUpdate\",\"pubKeys\":"
                          + "[\"lKdU1EbQubxyDDm2q3N8KclZ2C94+3eI=\","
                          + "\"r/mIkG3eEpVdm+u/ko/cwxzOMo1bkA5E=\"]}}},\"0.1.0\":{\"outdated\":true}}}")}))
        }
    )
    public ResponseEntity<String> getContext() {
        Resource resource = new ClassPathResource("/static/context.json");
        try {
            return  ResponseEntity.ok(IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Could not read context file");
        }
        return ResponseEntity.ok("");
    }

}
