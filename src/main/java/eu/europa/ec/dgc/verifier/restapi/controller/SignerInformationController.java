package eu.europa.ec.dgc.verifier.restapi.controller;

import eu.europa.ec.dgc.verifier.restapi.converter.CmsMessageConverter;
import eu.europa.ec.dgc.verifier.restapi.dto.ProblemReportDto;
import eu.europa.ec.dgc.verifier.restapi.dto.SignedCertificateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
public class SignerInformationController {

    private static final String X_RESUME_TOKEN_HEADER = "X-RESUME-TOKEN";
    private static final String X_KID_HEADER ="X-KID";


    /**
     * Http Method for getting signer certificate.
     */

    @GetMapping(path = "/signercertificateUpdate")
    public ResponseEntity<String> getSignerCertificateUpdate(
            @RequestHeader(X_RESUME_TOKEN_HEADER) Integer resumeToken
    ) {

        log.info("Resume token: {}", resumeToken.toString());

        return ResponseEntity.ok("signercertificateUpdate");
    }


    /**
     * Http Method for getting status update for key identifier.
     */

    @GetMapping(path = "/signercertificateStatus")
    public ResponseEntity<String> getSignerCertificateStatus(
            @RequestHeader(X_KID_HEADER) Integer kId
    ) {

        log.info("Key Identifier: {}", kId.toString());

        return ResponseEntity.ok("signercertificateStatus");
    }

}
