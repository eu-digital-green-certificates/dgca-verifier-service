package eu.europa.ec.dgc.verifier.restapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Schema(
    name = "DeltaList",
    type = "object",
    example = "{\n"
        + "\"updated\": [\"33333d=\",\"333311=\",\"55554=\"],\n"
        + "\"deleted\":[\"3115adf=\"]\n"
        + "}"
)
@Getter
@AllArgsConstructor
public class CertificatesLookupResponseItemDto {
    String kid;
    String rawData;
}
