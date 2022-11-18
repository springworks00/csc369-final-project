# CSC 369 Final Project
Linear Regression

[Housing Data](https://www.kaggle.com/datasets/thuynyle/redfin-housing-market-data?select=zip_code_market_tracker.tsv000)

There are 5 files in this set, describing the housing markets on a (1) national, (2) state, (3) county, (4) neighborhood, and (5) zip basis. 
There is plenty of `join`-ready material to work with because each file's columns are nearly identical.

Pick a few quantitative variables not related to price (i.e. `new_listings_mom`, `inventory`, `sold_above_list`) and scatter-plot
them against:

1. The median home price within a ZIP.
2. The median home price within a neighborhood.
3. The median home price within a county.
4. (etc)

(i.e. `y=median_sale_price` and `x=new_listings_mom`)

Then, graph a linear-regression line through each scatter plot.

In order to see which variables are "better" at predicting the median prices,
we would have to evaluate each linear-regression model with a cross-validation score.
Finding which variables are "better" predictors is the whole purpose of linear regression,
but if calculating this score is not necessary for an "A" then let's not worry about it.


Todo:
- [x] Push a working spark project template (Lan)
- [ ] Push a build script for convenience (Dylan)
- [ ] Find a Scala graphing library (scatter plot + line)

Essential Features:
1. Join files + other skills learned from class
2. Calculate the regression (distributed)
3. Graph the regression
