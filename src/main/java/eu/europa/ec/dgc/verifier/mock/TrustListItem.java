package eu.europa.ec.dgc.verifier.mock;

import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class TrustListItem {

    private String kid;

    private ZonedDateTime timestamp;

    private String rawData;

}
