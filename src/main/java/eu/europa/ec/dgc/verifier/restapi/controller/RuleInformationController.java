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

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
public class RuleInformationController {

    private static final String X_RESUME_TOKEN_HEADER = "X-RESUME-TOKEN";
    private static final String X_RULEID_HEADER = "X-RULEID";

    /**
     * Http Method for getting validation rules.
     */

    @GetMapping(path = "/ruleUpdate")
    public ResponseEntity<String> getRuleUpdate(
            @RequestHeader(X_RESUME_TOKEN_HEADER) Integer resumeToken
    ) {

        log.info("Resume token: {}", resumeToken.toString());

        return ResponseEntity.ok("ruleUpdate");
    }


    /**
     * Http Method for getting the status of a specific validation Rule.
     */

    @GetMapping(path = "/ruleStatus")
    public ResponseEntity<String> getRuleStatus(
            @RequestHeader(X_RULEID_HEADER) Integer kid
    ) {

        log.info("Key Identifier: {}", kid.toString());

        return ResponseEntity.ok("ruleStatus");
    }

}
