const sql = require('mssql');
require('dotenv').config();

const dbServer = process.env.DB_SERVER || 'localhost';
const serverParts = dbServer.split('\\');

const config = {
    user: process.env.DB_USER || 'sa', 
    password: process.env.DB_PASSWORD || 'YourPassword123!', 
    server: serverParts[0], 
    database: process.env.DB_NAME || 'HealthbookDB',
    options: {
        encrypt: true, // Use this if you're on Azure
        trustServerCertificate: true, // True for local dev / self-signed certs
        instanceName: serverParts[1] || undefined
    }
};

const poolPromise = new sql.ConnectionPool(config)
  .connect()
  .then(pool => {
    console.log('Connected to SQL Server successfully!');
    return pool;
  })
  .catch(err => {
      console.log('Database Connection Failed! Bad Config: ', err);
      process.exit(1);
  });

module.exports = {
  sql, poolPromise
};
