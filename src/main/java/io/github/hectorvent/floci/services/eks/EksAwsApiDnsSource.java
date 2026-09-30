package io.github.hectorvent.floci.services.eks;

import io.github.hectorvent.floci.core.common.dns.DnsAnswer;
import io.github.hectorvent.floci.core.common.dns.DnsClientRecordSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Resolves AWS service hostnames for EKS nodes to the link-local address that each node forwards
 * to Floci. SDKs that ignore AWS_ENDPOINT_URL then reach Floci from pods and host-network agents.
 */
@ApplicationScoped
public class EksAwsApiDnsSource implements DnsClientRecordSource {

    private static final int TTL_SECONDS = 60;
    private static final int TYPE_A = 1;

    private final EksClusterManager clusterManager;

    @Inject
    public EksAwsApiDnsSource(EksClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public Optional<DnsAnswer> resolve(String name, int type, String clientAddress) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (!(lower.equals("amazonaws.com") || lower.endsWith(".amazonaws.com"))
                || !clusterManager.forwardsAwsApi(clientAddress)) {
            return Optional.empty();
        }
        // The forwarded address is IPv4 only, so other record types answer with no data.
        return Optional.of(type == TYPE_A
                ? DnsAnswer.records(List.of(EksClusterManager.AWS_API_ADDRESS), TTL_SECONDS)
                : DnsAnswer.noData());
    }
}
