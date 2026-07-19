const express = require('express');
const cors = require('cors');
const { sql, poolPromise } = require('./db-config');
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
        const pool = await poolPromise;
        const result = await pool.request().query('SELECT * FROM Doctors');
        res.json(result.recordset);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Specialties
app.get('/api/specialties', async (req, res) => {
    try {
        const pool = await poolPromise;
        const result = await pool.request().query('SELECT * FROM Specialties');
        res.json(result.recordset);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Hospitals
app.get('/api/hospitals', async (req, res) => {
    try {
        const pool = await poolPromise;
        const result = await pool.request().query('SELECT * FROM Hospitals');
        res.json(result.recordset);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Appointments (Only for the logged in user)
app.get('/api/appointments', verifyToken, async (req, res) => {
    try {
        const pool = await poolPromise;
        const patientId = req.user.uid; // Get UID from the verified token
        
        // Return appointments for this user, joining with Doctors to get doctor name
        const result = await pool.request()
            .input('patient_id', sql.NVarChar, patientId)
            .query(`
                SELECT a.*, d.name as doctorName, d.specialty, d.hospital 
                FROM Appointments a
                LEFT JOIN Doctors d ON a.doctor_id = d.id
                WHERE a.patient_id = @patient_id
                ORDER BY a.appointment_date DESC, a.appointment_time DESC
            `);
        res.json(result.recordset);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST Appointments (Create new appointment) - Requires Auth & Patient Role (or just Auth for now)
app.post('/api/appointments', verifyToken, async (req, res) => {
    try {
        const pool = await poolPromise;
        const data = req.body;
        const patientId = req.user.uid;
        
        const result = await pool.request()
            .input('patient_id', sql.NVarChar, patientId)
            .input('doctor_id', sql.Int, data.doctor_id)
            .input('appointment_date', sql.NVarChar, data.appointment_date)
            .input('appointment_time', sql.NVarChar, data.appointment_time)
            .input('status', sql.NVarChar, data.status || 'Sắp tới')
            .input('type', sql.NVarChar, data.type)
            .input('patient_name', sql.NVarChar, data.patient_name || null)
            .input('patient_phone', sql.NVarChar, data.patient_phone || null)
            .input('patient_dob', sql.NVarChar, data.patient_dob || null)
            .input('patient_gender', sql.NVarChar, data.patient_gender || null)
            .query(`
                INSERT INTO Appointments (patient_id, doctor_id, appointment_date, appointment_time, status, type, patient_name, patient_phone, patient_dob, patient_gender)
                OUTPUT INSERTED.id
                VALUES (@patient_id, @doctor_id, @appointment_date, @appointment_time, @status, @type, @patient_name, @patient_phone, @patient_dob, @patient_gender)
            `);
            
        data.id = result.recordset[0].id;
        data.patient_id = patientId;
        res.status(201).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on port ${PORT}`);
    console.log(`Test URL: http://localhost:${PORT}/api/health`);
});
