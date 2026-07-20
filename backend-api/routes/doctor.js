const express = require('express');
const router = express.Router();
const { verifyToken, requireRole } = require('../middleware/auth');

// Tất cả các API trong file này đều yêu cầu quyền 'doctor'
router.use(verifyToken, requireRole('doctor'));

// GET /api/doctor/appointments
// Lấy danh sách lịch hẹn của bác sĩ này
router.get('/appointments', async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        const date = req.query.date;

        let query = db.collection('Appointments').where('doctor_id', '==', uid);
        if (date) {
            query = query.where('appointment_date', '==', date);
        }

        const snapshot = await query.get();
        const appointments = [];
        
        snapshot.forEach(doc => {
            appointments.push({ id: doc.id, ...doc.data() });
        });

        // Tự sắp xếp trong bộ nhớ
        appointments.sort((a, b) => {
            const dateA = new Date(a.appointment_date + ' ' + a.appointment_time);
            const dateB = new Date(b.appointment_date + ' ' + b.appointment_time);
            return dateB - dateA;
        });

        res.json(appointments);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/doctor/appointments/:id/status
// Cập nhật trạng thái lịch hẹn (Chấp nhận / Từ chối / Hoàn thành)
router.put('/appointments/:id/status', async (req, res) => {
    try {
        const db = req.db;
        const doctorId = req.user.uid;
        const { id } = req.params;
        const { status } = req.body; 

        let statusVN = status;
        if (status === 'confirmed') statusVN = 'Đã duyệt';
        else if (status === 'cancelled') statusVN = 'Đã hủy';
        else if (status === 'completed') statusVN = 'Đã qua';

        const docRef = db.collection('Appointments').doc(id);
        const doc = await docRef.get();

        if (!doc.exists) {
            return res.status(404).json({ error: 'Appointment not found' });
        }

        if (doc.data().doctor_id !== doctorId) {
            return res.status(403).json({ error: 'Forbidden: Not your appointment' });
        }

        await docRef.update({ status: statusVN });

        res.json({ message: 'Appointment status updated successfully', status: statusVN });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/doctor/medical-records
// Bác sĩ viết bệnh án và đơn thuốc sau khi khám xong
router.post('/medical-records', async (req, res) => {
    try {
        const db = req.db;
        const doctorId = req.user.uid;
        const { patientId, appointmentId, diagnosis, prescription, notes } = req.body;

        if (!patientId || !diagnosis) {
            return res.status(400).json({ error: 'patientId and diagnosis are required' });
        }

        const prescriptionStr = Array.isArray(prescription) ? prescription.join(', ') : (prescription || '');

        const recordData = {
            doctor_id: doctorId,
            patient_id: patientId,
            appointment_id: appointmentId || null,
            diagnosis: diagnosis,
            prescription: prescriptionStr,
            notes: notes || '',
            created_at: new Date().toISOString()
        };

        const docRef = await db.collection('MedicalRecords').add(recordData);

        if (appointmentId) {
            await db.collection('Appointments').doc(appointmentId).update({ status: 'Đã qua' });
        }

        recordData.id = docRef.id;
        res.status(201).json(recordData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/doctor/schedule
// Đặt lịch rảnh cho bác sĩ
router.post('/schedule', async (req, res) => {
    try {
        const db = req.db;
        const doctorId = req.user.uid;
        const { date, timeSlots } = req.body; 

        if (!date || !timeSlots || !Array.isArray(timeSlots)) {
            return res.status(400).json({ error: 'date and timeSlots array are required' });
        }

        const timeSlotsStr = timeSlots.join(',');

        // Check if schedule for date already exists
        const snapshot = await db.collection('Schedules')
            .where('doctor_id', '==', doctorId)
            .where('available_date', '==', date)
            .get();

        if (!snapshot.empty) {
            // Update
            const docId = snapshot.docs[0].id;
            await db.collection('Schedules').doc(docId).update({
                time_slots: timeSlotsStr,
                updated_at: new Date().toISOString()
            });
        } else {
            // Insert
            await db.collection('Schedules').add({
                doctor_id: doctorId,
                available_date: date,
                time_slots: timeSlotsStr,
                created_at: new Date().toISOString()
            });
        }

        res.json({ message: 'Schedule updated successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
