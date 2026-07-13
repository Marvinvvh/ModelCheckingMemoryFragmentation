
import json

class Model:
    def __init__(self, model_config, model_variables):
        self.model_config: ModelConfig = model_config
        self.model_variables: ModelVariables = model_variables
    
    def to_dict(self) -> dict[str, str]:
        return {**self.model_config.to_dict(), **self.model_variables.to_dict()}
    
class ModelVariablesParser:
    def __init__(self, model_variables):
        self.model_variables = model_variables 
    
    def parse(self):
        trace_action_count = self.model_variables['traceActionCount']
        allocator_identifier = self.model_variables['identifier']
        if trace_action_count is None or allocator_identifier is None:
            return None
        
        return ModelVariables(trace_action_count, allocator_identifier)

class ModelVariables:
    def __init__(self, trace_action_count, allocator_identifier):
        self.trace_action_count = trace_action_count
        self.allocator_identifier = allocator_identifier
    
    def to_dict(self) -> dict[str, str]:
        return {"model_variables_trace_action_count":self.trace_action_count, "model_variables_allocator_identifier":self.allocator_identifier}

class ModelVariablesRegister:
    def __init__(self):
        self.parsers = {}
        self.default_parser = None
    
    def addParser(self, tag, parser):
        self.parsers[tag] = parser
    
    def addDefaultParser(self, parser):
        self.default_parser = parser
    
    def parse(self, variables_json: dict):
        variables_type = variables_json['identifier']
        if variables_type in self.parsers:
            fn = self.parsers[variables_type]
            return fn(variables_json).parse()
        elif self.default_parser is not None:
            fn = self.default_parser
            return fn(variables_json).parse()

def parse_model_variables(variables_path, parser: ModelVariablesRegister) -> ModelVariables | None:
    json_model_variables: dict
    with open(variables_path, "r") as json_raw_data:
        json_model_variables = json.load(json_raw_data)

    return parser.parse(json_model_variables)

class ModelConfigParser:
    def __init__(self, model_config):
        self.model_config = model_config
    
    def parse(self):
        identifier = self.model_config['identifier']
        page_size = self.model_config['pageSize']  
        amount_of_pages = self.model_config['amountOfPages']
        address_alignment_allocator = self.model_config['addressAlignmentAllocator']
        size_multiple_allocator = self.model_config['sizeMultipleAllocator']
        allocator_count = self.model_config['allocatorCount']

        if None in [identifier, page_size, amount_of_pages, address_alignment_allocator, allocator_count]:
            return None
        
        return ModelConfig(identifier, page_size, amount_of_pages, address_alignment_allocator, size_multiple_allocator, allocator_count)

class ModelConfig:
    def __init__(self, identifier, page_size, amount_of_pages, address_alignment, size_multiple, allocator_count):
        self.identifier = identifier
        self.page_size = page_size
        self.amount_of_pages = amount_of_pages
        self.address_alignment = address_alignment
        self.size_multiple = size_multiple
        self.allocator_count = allocator_count
        self.total_memory_size = amount_of_pages * page_size

    def to_dict(self) -> dict[str, str]:
        return {"model_config_identifier":self.identifier,
                "model_config_page_size":self.page_size,
                "model_config_total_memory_size":self.total_memory_size,
                "model_config_amount_of_pages":self.amount_of_pages,
                "model_config_address_alignment":self.address_alignment,
                "model_config_size_multiple":self.size_multiple,
                "model_config_allocator_count":self.allocator_count,
                }

class ModelConfigRegister:
    def __init__(self):
        self.parsers = {}
        self.default_parser = None
    
    def addParser(self, tag, parser):
        self.parsers[tag] = parser
    
    def addDefaultParser(self, parser):
        self.default_parser = parser
    
    def parse(self, config_json: dict):
        config_type = config_json['identifier']
        if config_type in self.parsers:
            fn = self.parsers[config_type]
            return fn(config_json).parse()
        elif self.default_parser is not None:
            fn = self.default_parser
            return fn(config_json).parse()

def parse_model_config(model_config_path, parser: ModelConfigRegister) -> ModelConfig | None:
    json_model_config: dict
    with open(model_config_path, "r") as json_raw_data:
        json_model_config = json.load(json_raw_data)

    return parser.parse(json_model_config)

def parse_model(model_config_path, model_variables_path, parser_config, parser_variables):
    model_config = parse_model_config(model_config_path, parser_config)
    model_variables = parse_model_variables(model_variables_path, parser_variables)

    if model_config is None or model_variables is None:
        return None

    return Model(model_config, model_variables) 