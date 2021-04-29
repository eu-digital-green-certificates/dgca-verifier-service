package eu.europa.ec.dgc.verifier.mock;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DgcGatewayConnector {
    /**
     * Dummy function.
     * @return trusted item list
     */
    public List<TrustListItem> getTrustedCertificates() {

        TrustListItem item = new TrustListItem();
        item.setKid("testKid");
        item.setTimestamp(ZonedDateTime.now());
        item.setRawData("testRawData");

        ArrayList<TrustListItem> items = new ArrayList<>();

        items.add(item);
        return items;
    }
}
