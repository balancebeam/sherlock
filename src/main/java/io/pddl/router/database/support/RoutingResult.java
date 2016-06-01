package io.pddl.router.database.support;

import java.util.List;

import io.pddl.router.database.merger.Merger;

public class RoutingResult {
    private List<String>  resourceIdentities;
    private Merger<?, ?> merger;

    public List<String> getResourceIdentities() {
        return resourceIdentities;
    }

    public void setResourceIdentities(List<String> resourceIdentities) {
        this.resourceIdentities = resourceIdentities;
    }

    public void setMerger(Merger<?, ?> merger) {
        this.merger = merger;
    }

    public Merger<?, ?> getMerger() {
        return merger;
    }

}
