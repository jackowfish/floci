package io.github.hectorvent.floci.core.common.dns;

import java.util.Optional;

/**
 * A source of answers that depend on which container asked. {@link EmbeddedDnsServer} consults
 * it before the zone-owning {@link DnsRecordSource}s, and discovers implementations through CDI.
 */
public interface DnsClientRecordSource {

    /**
     * Answers a query from {@code clientAddress}, or returns empty to leave it to the next source.
     * The name arrives without a trailing dot and in the case the client sent.
     */
    Optional<DnsAnswer> resolve(String name, int type, String clientAddress);
}
