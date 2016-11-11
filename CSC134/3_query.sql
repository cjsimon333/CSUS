/**
 * Query 1:
 * List the cars (license, model and year, Amount_of_Damage)
 * that have been involved in any accidents.
 * Order the result by license in descending order.
 */
SELECT   c.License, Model, Year
FROM     CAR AS c, HAD AS h
WHERE    h.License = c.License 
ORDER BY h.License DESC

/**
 * Query 2:
 * List the cars that have more than two accidents.
 * List license and number of accidents.
 */
SELECT c.License, COUNT(*)
FROM   CAR AS c, HAD AS h
WHERE  c.License = h.license AND (SELECT COUNT(*) AS NumberOfAccidents
                                  FROM   CAR AS c, HAD AS h
                                  WHERE  c.License = h.License) > 2
/* OR */
SELECT   c.License, COUNT(*) AS NumberOfAccidents
FROM     CAR AS c, HAD AS h
WHERE    c.License = h.license
GROUP BY c.License
HAVING   COUNT(*) > 2 

/**
 * Query 3:
 * List information (driver name, amount of damage, license, model and SSN)
 * about the accidents in which the owner of the car are involved
 * i.e., the Driver_Name and Customer_Name are the same.
 */
SELECT a.Driver_Name, a.Amount_of_Damage, c.License, c.Model, cm.SSN
FROM   CUSTOMER AS cm, CAR AS cr, HAD AS h, ACCIDENT AS a
WHERE  a.Driver_Name = cm.Customer_Name

/**
 * Query 4:
 * Obtain the information of any policy
 * (policy number, policy rate and policy details)
 * whose policy rate is higher than the rate of policy number 12.
 */
SELECT PolicyNo, Policy_Rate, Policy_Details
FROM   
WHERE  

/**
 * Query 5:
 * Consider all policies, list the lowest rate, highest rate, and average rate.
 */

/**
 * Query 6: Get information
 * (accident number, driver name, accident date and amount of damage)
 * of the accident which has the highest amount of damage among all accidents.
 */

/**
 * Query 7: Get the information (customer name, city and state)
 * about the customers who own cars with the model of ‘Honda Accord’.
 * Also get those cars’ license, and year made.
 */

/**
 * Query 8: Retrieve the customers’ information
 * (SSN, customer name, street, city, state and zip)
 * who live either in ‘Roseville’ or ‘Sunrise’.
 * (‘Roseville’ and ‘Sunrise’ are city names)
 */

/**
 * Query 9: Retrieve policies that cover more than 2 cars.
 * List policy number and number of cars covered.
 */