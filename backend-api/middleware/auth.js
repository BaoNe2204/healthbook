const { getAuth } = require('firebase-admin/auth');
const { db } = require('../firebase-config');

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
            const uid = req.user.uid;
            const docRef = db.collection('Users').doc(uid);
            const doc = await docRef.get();
            
            if (!doc.exists) {
                return res.status(403).json({ error: 'User record not found in database' });
            }
            
            const userData = doc.data();
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
