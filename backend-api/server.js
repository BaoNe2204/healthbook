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

// Serve static files from public/uploads
const path = require('path');
app.use('/uploads', express.static(path.join(__dirname, 'public/uploads')));

// Set up db middleware so routers can access it if needed (optional, or they can require it directly)
app.use((req, res, next) => {
    req.db = db;
    next();
});

// Routes
app.use('/api/users', usersRouter);
app.use('/api/doctor', doctorRouter);
app.use('/api/admin', adminRouter);

// GET Vaccines
app.get('/api/vaccines', async (req, res) => {
    try {
        const snapshot = await db.collection('Vaccines').get();
        if (snapshot.empty) {
            // Seed mock data
            const mockVaccines = [
                { name: "Vắc xin 6 trong 1 (Pháp)", price: 1050000, disease: "Bạch hầu, ho gà, uốn ván, bại liệt, Hib, viêm gan B", requiredDoses: 3, ageGroup: "2-24 tháng" },
                { name: "Vắc xin Cúm tứ giá (Pháp)", price: 350000, disease: "Cúm mùa", requiredDoses: 1, ageGroup: "Từ 6 tháng trở lên" },
                { name: "Vắc xin Thủy đậu (Mỹ)", price: 900000, disease: "Thủy đậu", requiredDoses: 2, ageGroup: "Từ 12 tháng trở lên" },
                { name: "Vắc xin HPV (Mỹ)", price: 1800000, disease: "Ung thư cổ tử cung", requiredDoses: 3, ageGroup: "Nữ 9-26 tuổi" },
                { name: "Vắc xin Phế cầu (Bỉ)", price: 1050000, disease: "Viêm phổi, viêm màng não do phế cầu", requiredDoses: 3, ageGroup: "Từ 6 tuần tuổi" }
            ];
            const batch = db.batch();
            mockVaccines.forEach(v => {
                const docRef = db.collection('Vaccines').doc();
                batch.set(docRef, v);
            });
            await batch.commit();
            
            // Return seeded data
            const newSnapshot = await db.collection('Vaccines').get();
            const vaccines = [];
            newSnapshot.forEach(doc => vaccines.push({ id: doc.id, ...doc.data() }));
            return res.json(vaccines);
        }

        const vaccines = [];
        snapshot.forEach(doc => vaccines.push({ id: doc.id, ...doc.data() }));
        res.json(vaccines);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST Vaccine Bookings
app.post('/api/vaccine-bookings', verifyToken, async (req, res) => {
    try {
        const data = req.body;
        const patientId = req.user.uid;
        
        const bookingData = {
            patient_id: patientId,
            vaccine_id: data.vaccine_id,
            vaccine_name: data.vaccine_name,
            price: data.price,
            appointment_date: data.appointment_date,
            appointment_time: data.appointment_time,
            status: 'Sắp tới',
            patient_name: data.patient_name || null,
            patient_phone: data.patient_phone || null,
            created_at: new Date().toISOString()
        };
        
        const docRef = await db.collection('VaccineBookings').add(bookingData);
        res.json({ id: docRef.id, ...bookingData });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Vaccine Bookings
app.get('/api/vaccine-bookings', verifyToken, async (req, res) => {
    try {
        const patientId = req.user.uid;
        const snapshot = await db.collection('VaccineBookings')
            .where('patient_id', '==', patientId)
            .get();
            
        const bookings = [];
        snapshot.forEach(doc => {
            bookings.push({ id: doc.id, ...doc.data() });
        });
        
        bookings.sort((a, b) => {
            return new Date(b.created_at || 0) - new Date(a.created_at || 0);
        });
        
        res.json(bookings);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

const PORT = process.env.PORT || 3000;

app.get('/api/health', (req, res) => {
    res.json({ status: 'ok', message: 'Firebase Backend is running' });
});

// GET Doctors
app.get('/api/doctors', async (req, res) => {
    try {
        const snapshot = await db.collection('Doctors').get();
        const doctors = [];
        const seen = new Set();
        snapshot.forEach(doc => {
            const data = doc.data();
            const key = data.name || data.email;
            if (key) {
                if (seen.has(key)) return;
                seen.add(key);
            }
            doctors.push({ id: doc.id, ...data });
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
        const seen = new Set();
        snapshot.forEach(doc => {
            const data = doc.data();
            const key = data.name;
            if (key) {
                if (seen.has(key)) return;
                seen.add(key);
            }
            specialties.push({ id: doc.id, ...data });
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
        const seen = new Set();
        snapshot.forEach(doc => {
            const data = doc.data();
            const key = data.name;
            if (key) {
                if (seen.has(key)) return;
                seen.add(key);
            }
            hospitals.push({ id: doc.id, ...data });
        });
        res.json(hospitals);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET Clinics
app.get('/api/clinics', async (req, res) => {
    try {
        const snapshot = await db.collection('Clinics').get();
        const clinics = [];
        const seen = new Set();
        snapshot.forEach(doc => {
            const data = doc.data();
            const key = data.name;
            if (key) {
                if (seen.has(key)) return;
                seen.add(key);
            }
            clinics.push({ id: doc.id, ...data });
        });
        res.json(clinics);
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
            specialty: data.specialty || '',
            hospital: data.hospital || '',
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
        
        // Sinh thông báo
        await db.collection('Notifications').add({
            user_id: patientId,
            title: 'Đặt lịch khám thành công',
            body: `Yêu cầu đặt lịch khám với ${data.doctorName || 'Bác sĩ'} lúc ${data.appointment_time} ngày ${data.appointment_date} đã được gửi. Đang chờ xác nhận.`,
            type: 'APPOINTMENT_BOOKED',
            created_at: new Date().toISOString()
        });
        
        res.status(201).json(appointmentData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT Appointments (Cancel appointment)
app.put('/api/appointments/:id/cancel', verifyToken, async (req, res) => {
    try {
        const appointmentId = req.params.id;
        const patientId = req.user.uid;
        
        const docRef = db.collection('Appointments').doc(appointmentId);
        const doc = await docRef.get();
        if (!doc.exists || doc.data().patient_id !== patientId) {
            return res.status(403).json({ error: 'Unauthorized' });
        }

        await docRef.update({ status: 'Đã hủy' });
        res.json({ message: 'Appointment cancelled successfully' });
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
server.listen(PORT, '0.0.0.0', () => {
    console.log(`🚀 Firebase Server is running on port ${PORT}`);
    console.log(`Test URL: http://localhost:${PORT}/api/health`);
});
