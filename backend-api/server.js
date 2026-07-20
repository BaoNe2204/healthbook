const express = require('express');
const cors = require('cors');
const { verifyToken } = require('./middleware/auth');
const { db } = require('./firebase-config');
const usersRouter = require('./routes/users');
const doctorRouter = require('./routes/doctor');
const adminRouter = require('./routes/admin');

const app = express();
app.use(cors());
app.use(express.json());

// Set up db middleware so routers can access it if needed (optional, or they can require it directly)
app.use((req, res, next) => {
    req.db = db;
    next();
});

// Routes
app.use('/api/users', usersRouter);
app.use('/api/doctor', doctorRouter);
app.use('/api/admin', adminRouter);

const PORT = process.env.PORT || 3000;

app.get('/api/health', (req, res) => {
    res.json({ status: 'ok', message: 'Firebase Backend is running' });
});

// GET Doctors
app.get('/api/doctors', async (req, res) => {
    try {
        const snapshot = await db.collection('Doctors').get();
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
        const snapshot = await db.collection('Specialties').get();
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
        const snapshot = await db.collection('Hospitals').get();
        const hospitals = [];
        snapshot.forEach(doc => {
            hospitals.push({ id: doc.id, ...doc.data() });
        });
        res.json(hospitals);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Appointments (Only for the logged in user)
app.get('/api/appointments', verifyToken, async (req, res) => {
    try {
        const patientId = req.user.uid;
        const snapshot = await db.collection('Appointments')
            .where('patient_id', '==', patientId)
            .get();
        
        const appointments = [];
        snapshot.forEach(doc => {
            appointments.push({ id: doc.id, ...doc.data() });
        });
        
        // Optionally sort them in memory if no composite index exists
        appointments.sort((a, b) => {
            return new Date(b.appointment_date) - new Date(a.appointment_date);
        });
        
        res.json(appointments);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Patient's Medical Records
app.get('/api/users/medical-records', verifyToken, async (req, res) => {
    try {
        const patientId = req.user.uid;
        const snapshot = await db.collection('MedicalRecords')
            .where('patient_id', '==', patientId)
            .get();
            
        const records = [];
        snapshot.forEach(doc => {
            records.push({ id: doc.id, ...doc.data() });
        });
        
        records.sort((a, b) => {
            return new Date(b.created_at || 0) - new Date(a.created_at || 0);
        });
        
        res.json(records);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST Appointments (Create new appointment)
app.post('/api/appointments', verifyToken, async (req, res) => {
    try {
        const data = req.body;
        const patientId = req.user.uid;
        
        const appointmentData = {
            patient_id: patientId,
            doctor_id: data.doctor_id,
            doctorName: data.doctorName || '',
            appointment_date: data.appointment_date,
            appointment_time: data.appointment_time,
            status: data.status || 'Sắp tới',
            type: data.type,
            patient_name: data.patient_name || null,
            patient_phone: data.patient_phone || null,
            patient_dob: data.patient_dob || null,
            patient_gender: data.patient_gender || null,
            created_at: new Date().toISOString()
        };
        
        const docRef = await db.collection('Appointments').add(appointmentData);
        appointmentData.id = docRef.id;
        
        res.status(201).json(appointmentData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Doctor's Schedule for a specific date
app.get('/api/doctors/:id/schedule', async (req, res) => {
    try {
        const doctorId = req.params.id;
        const date = req.query.date;

        if (!date) {
            return res.status(400).json({ error: 'date query parameter is required' });
        }

        const snapshot = await db.collection('Schedules')
            .where('doctor_id', '==', doctorId)
            .where('available_date', '==', date)
            .limit(1)
            .get();

        if (snapshot.empty) {
            res.json([]);
        } else {
            const data = snapshot.docs[0].data();
            const slots = data.time_slots ? data.time_slots.split(',') : [];
            res.json(slots);
        }
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

const http = require('http');
const server = http.createServer(app);
server.listen(PORT, '127.0.0.1', () => {
    console.log(`🚀 Firebase Server is running on port ${PORT}`);
    console.log(`Test URL: http://localhost:${PORT}/api/health`);
});
