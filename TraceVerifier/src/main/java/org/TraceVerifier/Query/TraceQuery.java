package org.TraceVerifier.Query;

import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryResult;

/**
 * Query which can be tested with the model.
 */

public interface TraceQuery extends Passable {
    public String getIdentifier();

    public TraceProperty getProperty();

    public String getUnit();

    public TraceQueryResult getResult();

    public Query generateUPPAALQuery();

    public void parseUPPAALQueryResult(QueryResult queryResult);
}
