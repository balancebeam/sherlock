package io.pddl.audit.support;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.pddl.audit.SqlAuditor;

public class SimpleSqlAuditor implements SqlAuditor {

    private transient final Log logger = LogFactory.getLog(SimpleSqlAuditor.class);

    public void audit(String sql, List<Object> parameters) {
        logger.info("SQL:"+sql+" - Parameter:"+parameters);
    }

}
