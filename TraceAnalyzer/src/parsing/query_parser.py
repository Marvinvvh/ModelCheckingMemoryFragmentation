import pathlib
import json
import numpy as np
from .shared_datatypes import Sample

class TraceQueryCollectionParser:
    def __init__(self, query_collection, query_parser):
        self.query_parser: QueryParserRegister = query_parser
        self.query_collection = query_collection
    
    def parse(self):
        query_identifier = self.query_collection['queryIdentifier']
        queries = self.query_collection['queries']
        collections = self.query_collection['collections']

        if None in [query_identifier, queries, collections]:
            return None
        
        parsed_queries = []
        for query in queries:
            parsed_query = self.query_parser.parse(query)
            if parsed_query is None:
                continue
                return None
            parsed_queries.append(parsed_query)
        
        parsed_collections = self.parse_collections(collections, self.query_parser)
        return TraceQueryCollection(query_identifier, parsed_queries, parsed_collections)
    
    def parse_collections(self, query_collection, query_parser):

        if len(query_collection) == 0:
            return []
        
        parsed_collections = []
        for collection in query_collection:
            parser = TraceQueryCollectionParser(collection, query_parser)
            parsed_collection = parser.parse()
            if parsed_collection is None:
                return None
            parsed_collections.append(parsed_collection)
        
        return parsed_collections

class TraceQueryCollection:
    def __init__(self, identifier, queries, collections):
        self.identifier: str = identifier
        self.queries: list[TraceQuery] = queries
        self.collections: list[TraceQueryCollection] = collections

    def flatten_to_dict(self, hierarchy, path):
        collection_path = self.identifier if path is None else path + "/" + self.identifier
        query_hierarchy = {**hierarchy, "query_collection_identifier": collection_path}
        entries = []
        for trace_query in self.queries:
            entries.append(trace_query.flatten_to_dict(query_hierarchy))
        # TODO: Test
        for trace_collection in self.collections:
            entries.extend(trace_collection.flatten_to_dict(hierarchy, collection_path))
        return entries
        

class TraceQueryParser:
    def __init__(self, query):
        self.query = query
        return
    
    def parse(self):
        query_identifier = self.query['identifier']
        query_property = self.query['traceProperty']
        query_result = self.query['traceQueryResult']
        unit = self.query['unit']
        query_type = self.query['type']

        if None in [query_property, query_identifier, query_result, unit]:
            return None

        query_property = TraceQueryPropertyParser(query_property).parse()
        query_result = TraceQueryResultParser(query_result).parse()

        if query_property is None or query_result is None:
            return None

        return TraceQuery(query_identifier, query_property, query_result, unit, query_type)

class TraceQuery:
    def __init__(self, query_identifier, query_property, query_result, unit, query_type):
        self.identifier: str = query_identifier
        self.unit: str = unit
        self.query_type = query_type
        self.query_property: TraceQueryProperty = query_property
        self.query_result: TraceQueryResult = query_result

    def flatten_to_dict(self, hierarchy) -> dict:
        return {**hierarchy,
            "query_identifier":self.identifier,
            "query_unit":self.unit,
            "query_type":self.query_type,
            **self.query_property.to_dict(),
            **self.query_result.to_dict(),
        }


class TraceQueryPropertyParser:
    def __init__(self, query_property):
        self.query_result = query_property
    
    def parse(self):
        query_string: str = self.query_result['queryString']
        query_comment: str = self.query_result['queryComment']
        if query_string is None or query_comment is None:
            return None
        return TraceQueryProperty(query_string, query_comment)

class TraceQueryProperty:
    def __init__(self, query_string, query_comment):
        self.query_string = query_string
        self.query_comment = query_comment
    
    def to_dict(self) -> dict:
        return {
            "query_string":self.query_string,
            "query_comment": self.query_comment,
        }

class TraceQueryResultParser:
    def __init__(self, query_result):
        self.query_result = query_result
    
    def parse(self):
        query_value = self.query_result['queryValue']
        if query_value is None:
            return None

        query_status = query_value['status']        
        if query_status is None:
            return None
        
        query_data = self.query_result['queryData']
        if query_data is None:
            return None
        
        parsed_query_data = self._parse_query_data(query_data)
        if query_data is None:
            return None
        
        
        return TraceQueryResult(query_status, parsed_query_data)
    
    def _parse_query_data(self, query_data):
        outer_data = query_data['data']
        inner_data = []
        samples = []
        if outer_data:
            inner_data = outer_data[0]['data']
            samples = inner_data[0]['samples']
            if(samples is None):
                return None
        
        parsed_samples = []
        for sample in samples:
            time = sample['x']
            value = sample['y']
            if time is None or value is None:
                return None
            parsed_samples.append(Sample(time, value))
            
        return parsed_samples 


class TraceQueryResult:
    def __init__(self, query_status, query_data):
        self.query_status = query_status
        self.query_data: list[Sample] = query_data
        return
    
    def to_dict(self) -> dict:
        return {
            "query_status":self.query_status,
            "query_data":[s.to_dict() for s in self.query_data]
        }


class QueryCollectionParserRegister:
    def __init__(self):
        self.parsers = {}
        self.default_parser = None
    
    def addParser(self, tag, parser, query_parser):
        self.parsers[tag] = (parser, query_parser)
    
    def addDefaultParser(self, parser):
        self.default_parser = parser
    
    def parse(self, query_json: dict) -> TraceQueryCollection | None:
        query_type = query_json['queryIdentifier']
        if query_type in self.parsers:
            fn, q_parser = self.parsers[query_type]
            return fn(query_json, q_parser).parse()
        elif self.default_parser is not None:
            fn = self.default_parser
            return fn(query_json).parse()

class QueryParserRegister:
    def __init__(self):
        self.parsers = {}
        self.default_parser = None
    
    def addParser(self, tag, parser):
        self.parsers[tag] = parser

    def addDefaultParser(self, parser):
        self.default_parser = parser    

    def parse(self, query_json: dict) -> TraceQuery | None:
        query_type = query_json['identifier']
        if query_type in self.parsers:
            fn = self.parsers[query_type]
            return fn(query_json).parse()
        elif self.default_parser is not None:
            fn = self.default_parser
            return fn(query_json).parse()

def derive_all_queries(json_query: dict) -> list:
    json_queries: list = []
    queries: list = json_query['queries']
    if(queries is not None):
        json_queries += queries
    return json_queries

def parse_query_file(query_path, parser: QueryCollectionParserRegister) -> TraceQueryCollection | None:
    json_query: dict
    with open(query_path, "r") as json_raw_data:
        json_query = json.load(json_raw_data)
    parsed_query_collection = parser.parse(json_query)

    return parsed_query_collection