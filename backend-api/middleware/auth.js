const { getAuth } = require('firebase-admin/auth');

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
        // We will fetch the role from Firestore based on req.user.uid
        const { db } = require('../firebase-config');
        try {
            const userDoc = await db.collection('users').doc(req.user.uid).get();
            if (!userDoc.exists) {
                return res.status(403).json({ error: 'User record not found in database' });
            }
            const userData = userDoc.data();
            if (userData.role !== role && userData.role !== 'admin') {
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
