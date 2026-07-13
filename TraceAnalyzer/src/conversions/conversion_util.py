import pandas as pd
import pyarrow.parquet as pq

def get_max_time_values(df: pd.DataFrame) -> pd.DataFrame:
    return df.loc[df.groupby('hash_query')['time'].idxmax()]

def stamp(row, filters = [], separator=" | "):
    s = ""
    for i, name in enumerate(filters):
        value_entry = row[name]
        s += str(value_entry)
        if i < len(filters) - 1:
            s += separator

    return s

# Create a unique stamp for each query.
def stamp_dataframe(df: pd.DataFrame, stamps):
    dff = df.copy()
    dff_uniques = dff.groupby('query_identifier').head(1)
    dff_uniques['series'] = dff_uniques.apply(lambda r: stamp(r, stamps), axis=1)
    dff = dff.merge(dff_uniques, on='hash_query')
    return dff

def get_trace_data(df_query_search: pd.DataFrame, trace_path) -> pd.DataFrame:
    df_trace = pd.read_parquet(trace_path)

    return df_trace.merge(df_query_search, on=["hash_trace"], how="left")

# Retrieve query data from the hashed dataset
def get_query_data(df_query_search: pd.DataFrame, hashes_path) -> pd.DataFrame:
    dataset = pq.ParquetDataset(hashes_path,
                                partitioning='hive',
                                filters=[
                                    ('hash_trace', 'in', df_query_search['hash_trace'].to_list()),
                                    ('hash_query', 'in', df_query_search['hash_query'].to_list())])
    table = dataset.read()
    df_data: pd.DataFrame = table.to_pandas()

    return df_data.merge(df_query_search, on=["hash_query", "hash_trace"], how="left")

def get_stamped_data(df_query_search, hashes_path, stamps):
    return stamp_dataframe(get_query_data(df_query_search, hashes_path), stamps)