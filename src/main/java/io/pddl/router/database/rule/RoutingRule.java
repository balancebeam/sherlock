package io.pddl.router.database.rule;

public interface RoutingRule<F, T> {
    /**
     * @param <F>, the type of the routing fact
     * @param routeFact, the fact to check against
     * @return
     */
    boolean isDefinedAt(F routingFact);

    /**
     * if a update or delete will involve multiple data sources, we have to
     * return a group of data sources to use.<br>
     * for rules the matches only one data source, return a set with size==1.<br>
     * 
     * @return
     */
    T action();
}
