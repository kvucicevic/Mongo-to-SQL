## Mongo_to_SQL
This project is a part of the "Databases' university subject. This is a simple app where the user can \
query the Mongo database using SQL. Our task was to give an object  representation of an SQL query, \
validate it and parse it to a Mongo query. Then we query the Mongo database and show the results.

### Our pipeline

The pipeline can be seen in the app/AppCore file. It consists of: 
1. Representing the query as an SQL object (sql package) 
2. Validating the query (validator package) 
3. Adapting the query for Mongo (adapter package) 
4. Querying the Mongo DB (database package)

### Examples of the processed queries
```
select first_name, last_name from employees where department_id in (
 select department_id from employees where employee_id = 103 or employee_id = 156)

db.employees.aggregate( 
    { $lookup: {
	     from: "employees", 
	     localField: "department_id", 
	     foreignField: "department_id", 
	     as: "result" 
     }
    }
    { $unwind: "$result" }, 
    { $match: { 
        $or: [ { "result.employee_id": {$eq: 103} }, { "result.employee_id": {$eq: 156} } ] } 
     }, 
    { $project: {
        first_name: 1, 
        last_name: 1, 
        _id: 0 
        } 
    }
)
```
```
select job_id, avg(salary) from employees group by job_id

db.employees.aggregate(
    { $project: 
        {job_id: "$_id.job_id", avgsalary: 1, _id: 0 } 
    }
)
```
