const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql');
const path = require('path');
const mysql2 = require('mysql2/promise');
const ejs = require('ejs');
const app = express();
const port = process.env.PORT || 5000;
//ejs connection
app.set('view engine', 'ejs');
app.set("views",path.resolve("./views"));
//middleware
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(express.static(path.join(__dirname, 'public')));

// MySQL
const pool = mysql.createPool({
    connectionLimit: 10000,
    host: 'localhost',
    user: 'root', // Your MySQL username
    password: '', // Your MySQL password (leave empty if you haven't set any)
    database: 'medisup1'
});

//global variables
let usernameconst1;
let supplierconst1;
let orgconst1;
let orgconst2;

// Handle POST request for signup//////////////////////////////////////////////////////
app.post('/signup', (req, res) => {
    const { username, email, password, category } = req.body;
    usernameconst1=req.body.username;
    console.log('Received data:', { username, email, password, category });

    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }

        if(req.body.category === 'pharmacy')
            {
                
                const query1 = `CREATE TABLE IF NOT EXISTS ${usernameconst1} (
                    med_id VARCHAR(255) NOT NULL PRIMARY KEY,
                    descrip VARCHAR(255) NOT NULL,
                    price FLOAT DEFAULT NULL,
                    min_count INT DEFAULT NULL,
                    quantity INT DEFAULT NULL,
                    last_updated DATE DEFAULT NULL
                  ) ENGINE=InnoDB;`;
                  
                  connection.query(query1, (error, results) => {
                    if (error) {
                      console.error('Error executing query: ' + error.message);
                      res.status(500).send('Internal Server Error');
                      return;
                    }
                  
                  });
            }  

        const query = 'INSERT INTO login1 (username, email, password, category) VALUES (?, ?, ?, ?)';
        connection.query(query, [username, email, password, category], (error, results) => {
            connection.release();
            if (error) {
                console.error('Error executing query: ' + error.message);
                res.status(500).send('Internal Server Error');
                return;
            }
            else{
                res.redirect('/login.html');

            }
        });
         
    });
});
/////////////////////////////////////////////////////////////

// Handle POST request for login/////////////////////////////////////////////////////////////
app.post('/login', (req, res) => {
    const { username, password } = req.body;
    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }

        const query = 'SELECT * FROM login1 WHERE username = ? AND password = ?';
        connection.query(query, [username, password], (error, results) => {
            connection.release();
            if (error) {
                console.error('Error executing query: ' + error.message);
                res.status(500).send('Internal Server Error');
                return;
            }


            if (results.length === 1) {
                    if(results[0].category === 'pharmacy')
                    {
                        usernameconst1=req.body.username;
                            res.redirect('/pharome-details');
                    }
                    else if(results[0].category === 'supplier')
                        {
                            supplierconst1=req.body.username;
                            res.redirect('/supplier-details');
                        }
                    else if(results[0].category === 'organization')
                            res.redirect('/organization-details');
                    else
                            res.status(500).send(results);
            
            } 
            else {
                // Invalid credentials
                 res.status(401).send('Invalid username or password');
                
            }
    
        });
    });
});
/////////////////////////////////////////////////////////////////////////////////////////

//pharmacy details form/////////////////////////////////////////////////////
app.post('/pharprof', (req, res) => {
    const { pharmacyname, location, pinno, contactnumber, established} = req.body;

    console.log('Received data:', { pharmacyname, location, pinno, contactnumber, established});

    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }
        
        const query = 'INSERT INTO pharmacy (username, pharmacyname, location, pinno, contactnumber, established) VALUES (?, ?, ?, ?, ?, ?)';
        connection.query(query, [usernameconst1, pharmacyname, location, pinno, contactnumber, established], (error, results) => {
            connection.release();
            if (error) {
                console.error('Error executing query: ' + error.message);
                res.status(500).send('Internal Server Error');
                return;
            }
            else{
                res.redirect('/pharome-details');
            }
        });
    });
});
/////////////////////////////////////////////////////////////////////////

//handle to retireve data from database for pharmacy home details//////////////////////////////
app.get('/pharome-details', (req, res) => {
    const username = usernameconst1; // Assuming usernameconst1 holds the logged-in username
  
    pool.getConnection((err, connection) => {
      if (err) {
        console.error('Error connecting to database: ' + err.message);
        res.status(500).send('Internal Server Error');
        return;
      }
  
      const query = 'SELECT * FROM pharmacy WHERE username = ?'; // Update table name and criteria
      connection.query(query, [username], (error, results) => {
        connection.release();
        if (error) {
          console.error('Error executing query: ' + error.message);
          res.status(500).send('Internal Server Error');
          return;
        }
  
        if (results.length === 0) {
          // Handle case where no pharmacy details found
          res.redirect('/pharprof.html');
        }
  
        const pharmacyDetails = results[0]; // Assuming there's only one record per username
        res.render('pharome', { pharmacyDetails }); // Pass retrieved data to the template
      });
    });
  });
////////////////////////////////////////////////////////////////////////////

//from home to stock
  app.get('/pharstock.html', (req, res) => {
    res.redirect('/pharstock-details');
});

//from home/stock to order
app.get('/pharorder.html', (req, res) => {
    res.redirect('/pharorder-details');
});

//from order/stock to home
app.get('/pharome.html', (req, res) => {
    res.redirect('/pharome-details');
});

app.get('/login.html', (req, res) => {
    res.redirect('/login');
});

//from org home/order to org view
app.get('/orgview.html', (req, res) => {
    res.redirect('/orgview-details');
});

//from org view/order to org home
app.get('/orgome.html', (req, res) => {
    orgconst1=null;
    res.redirect('/organization-details');
});

//from org home/view to org order
app.get('/orgorder.html', (req, res) => {
    orgconst2=null;
    res.redirect('/orgorder-details');
});

//from org home/order/view to org msg
app.get('/orgmessage.html', (req, res) => {
    res.redirect('/orgmessage-details');
});

//from pharmacy home/order/stock to pharmacy msg
app.get('/pharmessage.html', (req, res) => {
    res.redirect('/pharmessage-details');
});

//from supome to suplist
app.get('/suplist.html', (req, res) => {
    res.redirect('/suplist-details');
});

//from suplist to supome
app.get('/supome.html', (req, res) => {
    res.redirect('/supplier-details');
});

//handle to fetch pharmacy stock details//////////////////////////////////////////////////////////////
async function getStockData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const [rows] = await connection.query(`SELECT * FROM ${usernameconst1} `); // Adjust query based on your table name
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching stock data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/pharstock-details', async (req, res) => {
    const stockData = await getStockData();
    if (stockData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching stock data');
      }
    res.render('pharstock', { stockData,usernameconst1 });
});
////////////////////////////////////////////////////////////////////////////////////////////

//handle to fetch pharmacy order details//////////////////////////////////////////////////////////////
async function getOrderData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql = `SELECT * FROM med_order WHERE username = '${usernameconst1}'`;
        const [rows] = await connection.query(sql);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching order data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

async function getSuplistData1() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql1 = `SELECT * FROM medsup`;
        const [rows] = await connection.query(sql1);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching phar sup list:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/pharorder-details', async (req, res) => {
    const orderData = await getOrderData();
    if (orderData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching order data');
      }
      const suplistData1 = await getSuplistData1();
      if (suplistData1.error) { // Check for error from getStockData
          // Handle error - e.g., display an error message to the user
          return res.status(500).send('Error fetching phar suplist');
        }
    res.render('pharorder', { orderData,suplistData1,usernameconst1 });
});
////////////////////////////////////////////////////////////////////////////////

//form of pharmacy new/update stock/////////////////////////////////////////////////////////
app.post('/newstock', (req, res) => {
    const { med_id,descrip,price,min_count,quantity } = req.body;
    console.log('Received data:', {  med_id,descrip,price,min_count,quantity });

    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }
        //for admin
        if(req.body.descrip)
        {
            const currentDate1 = new Date();
            const hoursToAdd = 5.5; // Adjust for daylight saving time if needed  
            const indianTime = new Date(currentDate1.getTime() + (hoursToAdd * 60 * 60 * 1000));

            const currentDate=indianTime;
            const formattedDate = currentDate.toISOString().slice(0, 10);
            const query1 = `INSERT INTO admin ( username,med_id,min_count,quantity,last_updated ) VALUES (?, ?, ?, ?,?)`;
            connection.query(query1, [ usernameconst1,med_id,min_count,quantity,formattedDate ], (error, results) => {
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
                
            });
             
            //for pharmacy
            const query = `INSERT INTO ${usernameconst1} ( med_id,descrip,price,min_count,quantity,last_updated ) VALUES (?, ?, ?, ?, ?,?)`;
            connection.query(query, [ med_id,descrip,price,min_count,quantity,formattedDate ], (error, results) => {
                connection.release();
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
                else{
                    res.redirect('/pharstock-details');
                }
            });
            
        }
        else{
            //for admin
            const currentDate1 = new Date();
            const hoursToAdd = 5.5; // Adjust for daylight saving time if needed  
            const indianTime = new Date(currentDate1.getTime() + (hoursToAdd * 60 * 60 * 1000));

            const currentDate=indianTime;
            console.log(currentDate);
            const formattedDate = currentDate.toISOString().slice(0, 10);
            const query1 = `UPDATE admin set quantity=?,last_updated=? where med_id=? and username=?`;
            connection.query(query1, [ quantity,formattedDate,med_id,usernameconst1 ], (error, results) => {
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
            
            });
            
            //for pharmacy
            if(req.body.price)
            {
                const query = `UPDATE ${usernameconst1} set price=?, quantity=?, last_updated=? where med_id= ? `;
                connection.query(query, [ price,quantity,formattedDate,med_id ], (error, results) => {
                    connection.release();
                    if (error) {
                        console.error('Error executing query: ' + error.message);
                        res.status(500).send('Internal Server Error');
                        return;
                    }
                    else{
                        res.redirect('/pharstock-details');
                    }
                });
            }
            else
            {
                const query = `UPDATE ${usernameconst1} set quantity=?, last_updated=? where med_id= ? `;
                connection.query(query, [ quantity,formattedDate,med_id ], (error, results) => {
                    connection.release();
                    if (error) {
                        console.error('Error executing query: ' + error.message);
                        res.status(500).send('Internal Server Error');
                        return;
                    }
                    else{
                        res.redirect('/pharstock-details');
                    }
                });
            }

        }
    
    });
});
/////////////////////////////////////////////////////////////////////////////////

//form of pharmacy order////////////////////////////////////////////////////////////////
app.post('/orderstock', (req, res) => {
    const { med_id,quantity,sup_id } = req.body;
    console.log('Received data:', { med_id,quantity,sup_id });
    const currentDate1 = new Date();
            const hoursToAdd = 5.5; // Adjust for daylight saving time if needed  
            const indianTime = new Date(currentDate1.getTime() + (hoursToAdd * 60 * 60 * 1000));

            const currentDate=indianTime;
    const formattedDate = currentDate.toISOString().slice(0, 10);
    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }
            const query = `INSERT INTO med_order ( username,med_id,quantity,sup_id, ordered_on ) VALUES (?, ?, ?, ?, ?)`;
            connection.query(query, [ usernameconst1,med_id,quantity,sup_id,formattedDate ], (error, results) => {
                connection.release();
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
                else{
                    res.redirect('/pharorder-details');
                }
            });
        
    });
});
//////////////////////////////////////////////////////////////////////////////////

//handle to fetch supplier home details////////////////////////////////////////////////////////////////
async function getSupplierData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql = `SELECT * FROM med_order WHERE sup_id = '${supplierconst1}'`;
        const [rows] = await connection.query(sql);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching supplier data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

async function getPharmacyData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql = `SELECT * FROM pharmacy`;
        const [rows] = await connection.query(sql);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching pharmacy data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/supplier-details', async (req, res) => {
    const pharmacyData = await getPharmacyData();
    if (pharmacyData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching pharmacy data');
      }
    const supplierData = await getSupplierData();
    if (supplierData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching supplier data');
      }
    res.render('supome', { supplierData,pharmacyData,supplierconst1 });
});
//////////////////////////////////////////////////////////////////////////////////////

//handle to fetch organization home alert details//////////////////////////////////////////////////////////////
async function getOrganData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql = `SELECT * FROM admin WHERE quantity<min_count;`;
        const [rows] = await connection.query(sql);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching organization data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/organization-details', async (req, res) => {
    const organData = await getOrganData();
    if (organData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching organization data');
      }
    res.render('orgome', { organData });
});
////////////////////////////////////////////////////////////////////////////////////////////////////

//change min_count by organization//////////////////////////////////////////////////////
app.post('/changemin', (req, res) => {
    const { username,med_id,min_count } = req.body;
    console.log('Received data:', {  username,med_id,min_count });

    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
            }
            const query1 = `UPDATE ${username} set min_count=? where med_id=?`;
            connection.query(query1, [ min_count,med_id ], (error, results) => {
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
            });
            const query2 = `UPDATE admin set min_count=? where med_id=?`;
            connection.query(query2, [ min_count,med_id ], (error, results) => {
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
                else
                    res.redirect('/organization-details'); 
            });
                 
    });
});
/////////////////////////////////////////////////////////////////////////////////////

//handle to fetch organisation stock view details/////////////////////////////////////////////////////////
async function getPharmacyData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql = `SELECT * FROM pharmacy`;
        const [rows] = await connection.query(sql);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching pharmacy data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

async function getOrgstockData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql1 = `SELECT * FROM ${orgconst1} `;
        const [rows] = await connection.query(sql1);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        //console.error('Error fetching orgstock data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/orgview-details', async (req, res) => {
    const pharmacyData = await getPharmacyData();
    if (pharmacyData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching pharmacy data');
      }
      const orgstockData = await getOrgstockData();
    if (orgstockData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        //return res.status(500).send('Error fetching orgstock data');
      }
    res.render('orgview', { pharmacyData,orgstockData});
});

app.post('/orgshowstock', (req, res) => {
    const { username } = req.body;
    console.log('Received data:', {  username });
    orgconst1=req.body.username;
    res.redirect('/orgview-details');

});
////////////////////////////////////////////////////////////////////////////////////////

//handle to fetch organisation order details/////////////////////////////////////////////////////
async function getOrgorderData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql = `SELECT * FROM med_order where username='${orgconst2}'`;
        const [rows] = await connection.query(sql);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching orgorder data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/orgorder-details', async (req, res) => {
      const orgorderData = await getOrgorderData();
    if (orgorderData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching orgstock data');
      }
    res.render('orgorder', { orgorderData});
});

app.post('/orgshoworder', (req, res) => {
    const { username } = req.body;
    console.log('Received data:', {  username });
    orgconst2=req.body.username;
    res.redirect('/orgorder-details');

});
////////////////////////////////////////////////////////////////////////////////////////

//form of org msg////////////////////////////////////////////////////////////////
app.post('/orgmsg', (req, res) => {
    const { username,med_id,msg} = req.body;
    console.log('Received data:', {  username,med_id,msg});
    const currentDate1 = new Date();
            const hoursToAdd = 5.5; // Adjust for daylight saving time if needed  
            const indianTime = new Date(currentDate1.getTime() + (hoursToAdd * 60 * 60 * 1000));

            const currentDate=indianTime;
    const formattedDate = currentDate.toISOString().slice(0, 10);
    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }
            const query = `INSERT INTO message ( username,med_id,msg,send_on ) VALUES (?, ?, ?, ?)`;
            connection.query(query, [ username,med_id,msg,formattedDate ], (error, results) => {
                connection.release();
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
                else{
                    //res.status(200).send({ message: 'User created successfully', redirectTo: 'localhost:5000/index.html' });
                    res.redirect('/orgmessage-details');
                }
            });
        
    });
});
//////////////////////////////////////////////////////////////////////////////////

//org message details//////////////////////////////////////////////////////////////////////
async function getOrgmsgData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql1 = `SELECT * FROM message `;
        const [rows] = await connection.query(sql1);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching orgmsg data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/orgmessage-details', async (req, res) => {
    const orgmsgData = await getOrgmsgData();
    if (orgmsgData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching orgmsg data');
      }
    res.render('orgmessage', { orgmsgData});
});
////////////////////////////////////////////////////////////////////////////////////////////////

//pharmacy message details//////////////////////////////////////////////////////////////////////
async function getPharmsgData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql1 = `SELECT * FROM message where username='${usernameconst1}'`;
        const [rows] = await connection.query(sql1);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching pharmsg data:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/pharmessage-details', async (req, res) => {
    const pharmsgData = await getPharmsgData();
    if (pharmsgData.error) { // Check for error from getPharmsgData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching pharmsg data');
      }
    res.render('pharmessage', { pharmsgData,usernameconst1});
});
////////////////////////////////////////////////////////////////////////////////////////////////

//form of supplier order status////////////////////////////////////////////////////////////////
app.post('/ordersupstatus', (req, res) => {
    const { order_id,status } = req.body;
    console.log('Received data:', { order_id,status });
    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }
            const query = `UPDATE med_order set status=? where order_id=?`;
            connection.query(query, [ status,order_id ], (error, results) => {
                connection.release();
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
                else{
                    res.redirect('/supplier-details');
                }
            });
        
    });
});
//////////////////////////////////////////////////////////////////////////////////

//form of pharmacy order status////////////////////////////////////////////////////////////////
app.post('/orderpharstatus', (req, res) => {
    const { order_id,status } = req.body;
    console.log('Received data:', { order_id,status });
    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }
            const query = `UPDATE med_order set status=? where order_id=?`;
            connection.query(query, [ status,order_id ], (error, results) => {
               // connection.release();
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
            });

            const query1 = `SELECT med_id,quantity FROM med_order where order_id=?`;
            connection.query(query1, [order_id ], (error, results) => {
               // connection.release();
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
                const med_id1 = results.map(row => row.med_id);
                const quantity1 = results.map(row => row.quantity);
                processResults(med_id1, quantity1);
                processResults1(med_id1, quantity1);
                
            });
            //for pharmacy
            function processResults(med_id1, quantity1) {
            const query2 = `UPDATE ${usernameconst1} set quantity=quantity+? where med_id=?`;
            connection.query(query2, [ quantity1,med_id1 ], (error, results) => {
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
            });
            }
            //for admin
            function processResults1(med_id1, quantity1) {
                const query3 = `UPDATE admin set quantity=quantity+? where med_id=? and username=?`;
                connection.query(query3, [ quantity1,med_id1,usernameconst1 ], (error, results) => {
                   connection.release();
                    if (error) {
                        console.error('Error executing query: ' + error.message);
                        res.status(500).send('Internal Server Error');
                        return;
                    }
                    else{
                        res.redirect('/pharorder-details');
                    }
                });
                }
        
    });
});
//////////////////////////////////////////////////////////////////////////////////

//form of supplier list////////////////////////////////////////////////////////////////////
app.post('/listsup', (req, res) => {
    const { med_id,descrip,availability } = req.body;
    console.log('Received data:', { med_id,descrip,availability});

    pool.getConnection((err, connection) => {
        if (err) {
            console.error('Error connecting to database: ' + err.message);
            res.status(500).send('Internal Server Error');
            return;
        }
        if(req.body.descrip)
        {
            const query = `INSERT INTO medsup ( username,med_id,descrip,availability ) VALUES (?, ?, ?, ?)`;
            connection.query(query, [ supplierconst1,med_id,descrip,availability ], (error, results) => {
                connection.release();
                if (error) {
                    console.error('Error executing query: ' + error.message);
                    res.status(500).send('Internal Server Error');
                    return;
                }
                else{
                    res.redirect('/suplist-details');
                }
            });
            
        }
        else{
                const query = `UPDATE medsup set availability=? where med_id= ? and username=?`;
                connection.query(query, [ availability,med_id,supplierconst1 ], (error, results) => {
                    connection.release();
                    if (error) {
                        console.error('Error executing query: ' + error.message);
                        res.status(500).send('Internal Server Error');
                        return;
                    }
                    else{
                        res.redirect('/suplist-details');
                    }
                });

        }
    
    });
});
/////////////////////////////////////////////////////////////////////////////////

//supllier list handle//////////////////////////////////////////////////////////////////////
async function getSuplistData() {
    try {
        const connection = await mysql2.createConnection({
            host: 'localhost', // Replace with your database server hostname/IP
            user: 'root', // Replace with your database username
            password: '', // Replace with your database password
            database: 'medisup1' // Optional - Replace with database name if needed
        });
        const sql = `SELECT * FROM medsup where username='${supplierconst1}'`;
        const [rows] = await connection.query(sql);
        connection.end(); // Close the connection (optional - connection pool might handle it)
        if (!rows.length) { // Check for empty results
            return []; // Return empty array if no rows
          }
        return rows;
    } catch (error) {
        console.error('Error fetching supplier list:', error.message);
        return { error: 'Database error' };
        // Handle errors appropriately (e.g., return an error object)
    }
}

app.get('/suplist-details', async (req, res) => {
    const suplistData = await getSuplistData();
    if (suplistData.error) { // Check for error from getStockData
        // Handle error - e.g., display an error message to the user
        return res.status(500).send('Error fetching supplier list');
      }
    res.render('suplist', { suplistData,supplierconst1 });
});
//////////////////////////////////////////////////////////////////////////////////////

// Listen on environment port or 5000
app.listen(port, () => console.log(`Server listening on port ${port}`));
