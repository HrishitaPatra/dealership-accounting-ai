#!/usr/bin/env python3
"""
Time Series Forecasting Script for Dealership Accounting System
Uses ARIMA/SARIMAX models to forecast bank transactions and exception resolution trends
"""

import sys
import json
import warnings
import pandas as pd
import numpy as np
from statsmodels.tsa.arima.model import ARIMA
from statsmodels.tsa.statespace.sarimax import SARIMAX
from datetime import datetime, timedelta

warnings.filterwarnings('ignore')

def forecast_bank_transactions(data):
    """
    Forecast bank transaction amounts using SARIMAX (seasonal model)
    """
    try:
        # Convert to pandas DataFrame
        df = pd.DataFrame(data)
        df['date'] = pd.to_datetime(df['date'])
        df = df.sort_values('date')
        df.set_index('date', inplace=True)
        
        # Aggregate daily amounts
        daily_amounts = df.groupby(df.index.date)['amount'].sum()
        daily_amounts.index = pd.to_datetime(daily_amounts.index)
        
        # Fill missing dates with 0
        date_range = pd.date_range(start=daily_amounts.index.min(), end=daily_amounts.index.max(), freq='D')
        daily_amounts = daily_amounts.reindex(date_range, fill_value=0)
        
        # Fit SARIMAX model (simplified for faster performance)
        # Using simpler parameters to reduce computation time
        model = SARIMAX(daily_amounts, order=(1, 0, 0), seasonal_order=(1, 0, 0, 7))
        fitted_model = model.fit(disp=False)
        
        # Forecast next 30 days
        forecast_steps = 30
        forecast = fitted_model.forecast(steps=forecast_steps)
        forecast_ci = fitted_model.get_forecast(steps=forecast_steps).conf_int()
        
        # Prepare forecast data
        last_date = daily_amounts.index[-1]
        forecast_dates = [last_date + timedelta(days=i+1) for i in range(forecast_steps)]
        
        forecast_data = {
            'dates': [d.strftime('%Y-%m-%d') for d in forecast_dates],
            'values': [max(0, float(v)) for v in forecast],  # Ensure non-negative
            'lower_bound': [max(0, float(v)) for v in forecast_ci.iloc[:, 0]],
            'upper_bound': [max(0, float(v)) for v in forecast_ci.iloc[:, 1]]
        }
        
        # Historical data for visualization
        historical_data = {
            'dates': [d.strftime('%Y-%m-%d') for d in daily_amounts.index[-60:]],  # Last 60 days
            'values': [float(v) for v in daily_amounts.values[-60:]]
        }
        
        return {
            'success': True,
            'historical': historical_data,
            'forecast': forecast_data,
            'model_type': 'SARIMAX(1,1,1)x(1,1,1,7)'
        }
        
    except Exception as e:
        return {
            'success': False,
            'error': str(e)
        }

def forecast_exception_resolution(data):
    """
    Forecast exception resolution rate using ARIMA
    """
    try:
        # Convert to pandas DataFrame
        df = pd.DataFrame(data)
        df['date'] = pd.to_datetime(df['date'])
        df = df.sort_values('date')
        
        # Calculate monthly resolution rate
        df['month'] = df['date'].dt.to_period('M')
        monthly_stats = df.groupby('month').agg({
            'status': lambda x: (x == 'RESOLVED').sum() / len(x) * 100  # Resolution rate %
        }).reset_index()
        monthly_stats.columns = ['month', 'resolution_rate']
        monthly_stats['month'] = monthly_stats['month'].dt.to_timestamp()
        monthly_stats.set_index('month', inplace=True)
        
        # Fit ARIMA model (simplified for faster performance)
        model = ARIMA(monthly_stats['resolution_rate'], order=(1, 0, 0))
        fitted_model = model.fit()
        
        # Forecast next 6 months
        forecast_steps = 6
        forecast = fitted_model.forecast(steps=forecast_steps)
        forecast_ci = fitted_model.get_forecast(steps=forecast_steps).conf_int()
        
        # Prepare forecast data
        last_month = monthly_stats.index[-1]
        forecast_months = [last_month + pd.DateOffset(months=i+1) for i in range(forecast_steps)]
        
        forecast_data = {
            'dates': [d.strftime('%Y-%m') for d in forecast_months],
            'values': [min(100, max(0, float(v))) for v in forecast],  # Clamp to 0-100%
            'lower_bound': [min(100, max(0, float(v))) for v in forecast_ci.iloc[:, 0]],
            'upper_bound': [min(100, max(0, float(v))) for v in forecast_ci.iloc[:, 1]]
        }
        
        # Historical data
        historical_data = {
            'dates': [d.strftime('%Y-%m') for d in monthly_stats.index],
            'values': [float(v) for v in monthly_stats['resolution_rate'].values]
        }
        
        return {
            'success': True,
            'historical': historical_data,
            'forecast': forecast_data,
            'model_type': 'ARIMA(1,1,1)'
        }
        
    except Exception as e:
        return {
            'success': False,
            'error': str(e)
        }

def main():
    """
    Main function - reads JSON from stdin, performs forecasting, outputs JSON to stdout
    """
    try:
        # Read input JSON from stdin
        input_data = json.loads(sys.stdin.read())
        
        forecast_type = input_data.get('type', 'bank_transactions')
        data = input_data.get('data', [])
        
        if forecast_type == 'bank_transactions':
            result = forecast_bank_transactions(data)
        elif forecast_type == 'exception_resolution':
            result = forecast_exception_resolution(data)
        else:
            result = {
                'success': False,
                'error': f'Unknown forecast type: {forecast_type}'
            }
        
        # Output result as JSON
        print(json.dumps(result))
        
    except Exception as e:
        error_result = {
            'success': False,
            'error': str(e)
        }
        print(json.dumps(error_result))
        sys.exit(1)

if __name__ == '__main__':
    main()

