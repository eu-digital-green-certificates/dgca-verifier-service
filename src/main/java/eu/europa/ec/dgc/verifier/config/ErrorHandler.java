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

import eu.europa.ec.dgc.verifier.exception.BadRequestException;
import eu.europa.ec.dgc.verifier.restapi.dto.ProblemReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles {@link BadRequestException} when a validation failed.
     *
     * @param e the thrown {@link BadRequestException}
     * @return A ResponseEntity with a ErrorMessage inside.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleException(BadRequestException e) {
        return ResponseEntity
            .status(e.getStatus())
            .body(e.getMessage());
    }


    /**
     * Global Exception Handler to wrap exceptions into a readable JSON Object.
     *
     * @param e the thrown exception
     * @return ResponseEntity with readable data.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemReportDto> handleException(Exception e) {
        if (e instanceof ResponseStatusException) {
            return ResponseEntity
                .status(((ResponseStatusException) e).getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ProblemReportDto("co", "prob", "val", "det"));
        } else {
            log.error("Uncatched exception", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ProblemReportDto("0x500", "Internal Server Error", "", ""));
        }
    }


}
