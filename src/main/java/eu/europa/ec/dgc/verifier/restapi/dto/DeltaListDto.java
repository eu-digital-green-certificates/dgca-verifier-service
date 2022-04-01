package eu.europa.ec.dgc.verifier.restapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;


@Schema(
    name = "DeltaList",
    type = "object",
    example = "{\n"
        + "\"updated\": [\"33333d=\",\"333311=\",\"55554=\"],\n"
        + "\"deleted\":[\"3115adf=\"]\n"
        + "}"
)
@Data
@AllArgsConstructor
public class DeltaListDto {
    List<String> updated;
    List<String> deleted;
}
