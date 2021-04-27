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
