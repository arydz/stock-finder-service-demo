# Brief Description of integration with EDGAR server
Currently, this link is used to gather a required stock data `https://www.sec.gov/files/company_tickers.json`
Another data file that could be used is `https://www.sec.gov/files/company_tickers_exchange.json`
<br>
Thanks to this, you can provide a market index when saving information about shares in the database.
<br>
But, this information can be gathered from kaggle example dataset **(that will be used in this project)**, when saving candle data. This approach is chosen for learning purposes.
Instead of gathering for each stock a market index, it will be provided only once when persistence runs via Spark (in the future, currently custom implementation used). It will be taken from zip file folder (like nasdaq, nyse, sp500)

# Important classes
- `EdgarClient`