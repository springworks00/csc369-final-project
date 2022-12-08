# CSC 369 Final Project
Linear Regression

**THIS IS NOT THE SUBMISSION BRANCH**

switch from `old-main` to `main` to see the submission branch

[Housing Data](https://www.kaggle.com/datasets/thuynyle/redfin-housing-market-data?select=zip_code_market_tracker.tsv000)

Per Lan's request, we'll only use the ZIP-based data file (3 GB).

### Final Product
Input: name of x variable and the data files
Output: printed regression equation `y=mx+b` where `y` is the median home price (we need to calculate `m` and `b`)

### Role 1
Cut down the size of the ZIP data set and split it into multiple, join-able files.
Then write the scala code to re-join these files. Make sure it is complex enough.

### Role 2
Implement the single (not multi) linear regression algorithm, 
making it suitable for distributed RDD computations: 
[Linear Regression (Python Implementation) - GeeksforGeeks](https://www.geeksforgeeks.org/linear-regression-python-implementation/) 
Make sure this runs fast enough (i.e. just stay true to the principles learned in class)

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
