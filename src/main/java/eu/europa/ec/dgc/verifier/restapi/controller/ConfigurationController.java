package eu.europa.ec.dgc.verifier.restapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/context")
@Slf4j
public class ConfigurationController {

    /**
     * Http Method for getting the current context.
     */

    @GetMapping(path = "/context")
    public ResponseEntity<String> getContext() {

        log.info("Get Context");

        return ResponseEntity.ok("context");
    }

}
