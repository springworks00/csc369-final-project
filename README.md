# CSC 369 Final Project
Linear Regression

[Housing Data](https://www.kaggle.com/datasets/thuynyle/redfin-housing-market-data?select=zip_code_market_tracker.tsv000)

There are 5 files in this set, describing the housing markets on a (1) national, (2) state, (3) county, (4) neighborhood, and (5) zip basis. 

### Final Product
Input: name of x variable and the data files
Output: printed regression equation `y=mx+b` where `y` is the median home price (we need to calculate `m` and `b`)

### Role 1
Determine what kind of joins we can do with the 5 data sets, then implement the joins in Scala. (It must be complex)

### Role 2
Implement the single (not multi) linear regression algorithm, 
making it suitable for distributed RDD computations: 
[Linear Regression (Python Implementation) - GeeksforGeeks](https://www.geeksforgeeks.org/linear-regression-python-implementation/) 
If this is not implemented properly, our code will take way to long to run (we’re dealing with multiple GBs of data)

Note that the regression function is super simple. 
The challenge is solely distributing the work, not trying to comprehend some complex concept.

### Role 3
Combine the code of Roles 1 and 2, writing all the other RDD glue code/data re-formatting. 
Get the inputs and print the outputs. Make the whole program work, be sure the code is fully understandable,
and make use of key functions we learned in class where possible.

### Role 4
Setup the project, bring all the code together onto the server with no bugs, comment the code (just enough to help him grade), 
show examples of different x-variables being used, and submit it. Fully responsible for managing the project and seeing it through.

### Other Notes:
Just to be clear, the thing to be “distributed” is the linear regression calculation (finding the `m` and `b` of `y=mx+b`). 
If you look at the link, you’ll see a lot of `sum` and `mean` calls.
