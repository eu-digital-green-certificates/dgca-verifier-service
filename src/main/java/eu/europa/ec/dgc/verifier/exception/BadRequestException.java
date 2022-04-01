/*-
 * ---license-start
 * eu-digital-green-certificates / dgca-revocation-distribution-service
 * ---
 * Copyright (C) 2022 T-Systems International GmbH and all other contributors
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

package eu.europa.ec.dgc.verifier.exception;

public class BadRequestException extends RuntimeException {
    public int getStatus() {
        return status;
    }

    private final int status = 400;

    /**
     * Constructor for BadRequestException.
     *
     * @param message Massage of the exception.
     * @param inner Inner exception information
     */
    public BadRequestException(String message, Throwable inner) {

        super(message, inner);
    }

    /**
     * Constructor for BadRequestException.
     *
     * @param message Massage of the exception.
     */
    public BadRequestException(String message) {

        super(message);
    }

}
