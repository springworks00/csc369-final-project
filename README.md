# Linear Regression with Spark

This is the submission branch

### Final Product
Input: name of x variable and the data files 

Output: printed regression equation y=mx+b where y is the home price, m is the slope, and b is the y-intercept

### Role 1
Find data and join into a formatted `RDD[(String, String, ...)]`.

### Role 2
Implement the single (not multi) linear regression algorithm, making it suitable for distributed RDD computations. Make sure this runs fast enough (i.e. just stay true to the principles learned in class)

Note that the regression function is super simple. The challenge is solely distributing the work, not trying to comprehend some complex concept.

### Role 3
Combine the code of Roles 1 and 2, writing all the other RDD glue code/data re-formatting. Get the inputs and print the outputs. Make the whole program work, be sure the code is fully understandable, and make use of key functions we learned in class where possible.

### Role 4
Setup the project, bring all the code together onto the server with no bugs, comment the code (just enough to help him grade), show examples of different x-variables being used, and submit it. Fully responsible for managing the project and seeing it through.
