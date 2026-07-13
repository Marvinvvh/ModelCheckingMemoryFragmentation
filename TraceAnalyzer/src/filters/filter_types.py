import filters.filters as fl
FILTER_TYPE_IN_COLUMN: fl.filter_type = lambda x, y: any(s in str(x) for s in y)
FILTER_TYPE_EXACT: fl.filter_type = lambda x, y: any(s == str(x) for s in y)