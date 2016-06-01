package io.pddl.router.database;


import io.pddl.exception.RoutingException;
import io.pddl.router.database.support.RoutingResult;


public interface DatabaseRouter<T> {
	
	RoutingResult doRoute(T routingFact) throws RoutingException;
	
	RoutingResult doGlobalTableRoute(T routingFact) throws RoutingException;
	
}
