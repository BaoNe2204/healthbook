const express = require('express');
const cors = require('cors');
const { db } = require('./firebase-config');
const { verifyToken, requireRole } = require('./middleware/auth');
const usersRouter = require('./routes/users');
const doctorRouter = require('./routes/doctor');
const adminRouter = require('./routes/admin');

const app = express();
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/users', usersRouter);
app.use('/api/doctor', doctorRouter);
app.use('/api/admin', adminRouter);

const PORT = process.env.PORT || 3000;

app.get('/api/health', (req, res) => {
    res.json({ status: 'ok', message: 'Backend is running' });
});

// GET Doctors
app.get('/api/doctors', async (req, res) => {
    try {
        if (!db) return res.status(500).json({ error: "Firebase DB not initialized" });
        const snapshot = await db.collection('doctors').get();
        const doctors = [];
        snapshot.forEach(doc => {
            doctors.push({ id: doc.id, ...doc.data() });
        });
        res.json(doctors);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Specialties
app.get('/api/specialties', async (req, res) => {
    try {
        if (!db) return res.status(500).json({ error: "Firebase DB not initialized" });
        const snapshot = await db.collection('specialties').get();
        const specialties = [];
        snapshot.forEach(doc => {
            specialties.push({ id: doc.id, ...doc.data() });
        });
        res.json(specialties);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Hospitals
app.get('/api/hospitals', async (req, res) => {
    try {
        if (!db) return res.status(500).json({ error: "Firebase DB not initialized" });
        const snapshot = await db.collection('hospitals').get();
        const hospitals = [];
        snapshot.forEach(doc => {
            hospitals.push({ id: doc.id, ...doc.data() });
        });
        res.json(hospitals);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Appointments
app.get('/api/appointments', async (req, res) => {
    try {
        if (!db) return res.status(500).json({ error: "Firebase DB not initialized" });
        const snapshot = await db.collection('appointments').get();
        const appointments = [];
        snapshot.forEach(doc => {
            appointments.push({ id: doc.id, ...doc.data() });
        });
        res.json(appointments);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST Appointments (Create new appointment) - Requires Auth & Patient Role (or just Auth for now)
app.post('/api/appointments', verifyToken, async (req, res) => {
    try {
        if (!db) return res.status(500).json({ error: "Firebase DB not initialized" });
        const data = req.body;
        // Attach the logged in user's ID to the appointment
        data.patientId = req.user.uid;
        
        const docRef = db.collection('appointments').doc(); 
        data.id = docRef.id;
        await docRef.set(data);
        res.status(201).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
    console.log(`Test URL: http://localhost:${PORT}/api/health`);
});
