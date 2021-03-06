package no.fint.consumer.models.ramme;

import no.fint.model.resource.administrasjon.kodeverk.RammeResource;
import no.fint.model.resource.administrasjon.kodeverk.RammeResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class RammeLinker extends FintLinker<RammeResource> {

    public RammeLinker() {
        super(RammeResource.class);
    }

    public void mapLinks(RammeResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public RammeResources toResources(Collection<RammeResource> collection) {
        return toResources(collection.stream(), 0, 0, collection.size());
    }

    @Override
    public RammeResources toResources(Stream<RammeResource> stream, int offset, int size, int totalItems) {
        RammeResources resources = new RammeResources();
        stream.map(this::toResource).forEach(resources::addResource);
        addPagination(resources, offset, size, totalItems);
        return resources;
    }

    @Override
    public String getSelfHref(RammeResource ramme) {
        return getAllSelfHrefs(ramme).findFirst().orElse(null);
    }

    @Override
    public Stream<String> getAllSelfHrefs(RammeResource ramme) {
        Stream.Builder<String> builder = Stream.builder();
        if (!isNull(ramme.getSystemId()) && !isEmpty(ramme.getSystemId().getIdentifikatorverdi())) {
            builder.add(createHrefWithId(ramme.getSystemId().getIdentifikatorverdi(), "systemid"));
        }
        
        return builder.build();
    }

    int[] hashCodes(RammeResource ramme) {
        IntStream.Builder builder = IntStream.builder();
        if (!isNull(ramme.getSystemId()) && !isEmpty(ramme.getSystemId().getIdentifikatorverdi())) {
            builder.add(ramme.getSystemId().getIdentifikatorverdi().hashCode());
        }
        
        return builder.build().toArray();
    }

}

