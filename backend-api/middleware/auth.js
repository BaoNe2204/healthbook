const { getAuth } = require('firebase-admin/auth');
const { sql, poolPromise } = require('../db-config');

async function verifyToken(req, res, next) {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ error: 'Missing or invalid Authorization header' });
    }

    const token = authHeader.split('Bearer ')[1];
    try {
        const decodedToken = await getAuth().verifyIdToken(token);
        req.user = decodedToken;
        next();
    } catch (error) {
        res.status(401).json({ error: "Invalid token" });
    }
}

function requireRole(role) {
    return async (req, res, next) => {
        try {
            const pool = await poolPromise;
            const result = await pool.request()
                .input('id', sql.NVarChar, req.user.uid)
                .query('SELECT * FROM Users WHERE id = @id');
            
            if (result.recordset.length === 0) {
                return res.status(403).json({ error: 'User record not found in database' });
            }
            
            const userData = result.recordset[0];
            const userRole = userData.role ? userData.role.toUpperCase() : 'PATIENT';
            const requiredRole = role.toUpperCase();
            
            if (userRole !== requiredRole && userRole !== 'ADMIN') {
                return res.status(403).json({ error: `Forbidden: Requires ${role} role` });
            }
            
            req.userData = userData; // attach db user data to request
            next();
        } catch (error) {
            return res.status(500).json({ error: 'Database error', details: error.message });
        }
    };
}

module.exports = { verifyToken, requireRole };
