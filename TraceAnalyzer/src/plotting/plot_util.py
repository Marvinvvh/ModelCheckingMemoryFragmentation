import pandas as pd
import plotly.express as px
from plotly_resampler import FigureResampler

        
def show_index_chart_query_data(title:str, query_data: pd.DataFrame, show_legend:bool = True, labels: dict[str, str] = {}, y_name:str = "Value"):
    fig = px.line(query_data, x="time", y="size",labels={**labels, "time":"Time ticks", "size": y_name}, title=title, color='trace_identifier')
    fig.update_layout(showlegend=show_legend)

    return fig

def show_index_chart_query_data_upsampled(title:str,  query_data: pd.DataFrame, show_legend:bool = True, labels: dict[str, str] = {}, y_name:str = "Value", series: str = "series", use_dash: bool = True, port: int = 5000):
    fig = px.line(query_data, x="time", y="value", labels={**labels, "time":"Time ticks", "value": y_name}, title=title, color='model_config_size_multiple')
    fig = px.line(query_data, x="time", y="value", labels={**labels, "time":"Time ticks", "value": y_name}, color='model_config_size_multiple')
    fig.update_layout(showlegend=show_legend)
    if use_dash:
        resampler_fig = FigureResampler(fig, default_n_shown_samples=5000)
        resampler_fig.show_dash(mode='inline', port=port)
    else:
         fig.show()
    
    return fig

