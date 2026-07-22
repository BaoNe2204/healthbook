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

        // Tìm cả theo uid bác sĩ hoặc user_id liên kết trong Doctors
        let doctorIds = [uid];
        const doctorDoc = await db.collection('Doctors').where('user_id', '==', uid).get();
        if (!doctorDoc.empty) {
            doctorDoc.forEach(doc => doctorIds.push(doc.id));
        }

        const snapshot = await db.collection('Appointments').get();
        const appointments = [];
        
        snapshot.forEach(doc => {
            const data = doc.data();
            if (doctorIds.includes(data.doctor_id)) {
                if (!date || data.appointment_date === date) {
                    appointments.push({ id: doc.id, ...data });
                }
            }
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

        // Sinh thông báo cho bệnh nhân
        const appointmentData = doc.data();
        if (appointmentData.patient_id) {
            await db.collection('Notifications').add({
                user_id: appointmentData.patient_id,
                title: 'Trạng thái lịch khám',
                body: `Lịch khám lúc ${appointmentData.appointment_time} ngày ${appointmentData.appointment_date} đã chuyển thành: ${statusVN}`,
                type: 'APPOINTMENT_APPROVED',
                created_at: new Date().toISOString()
            });
        }

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

// GET /api/doctor/patients/:patientId/medical-records
// Xem lịch sử khám của bệnh nhân
router.get('/patients/:patientId/medical-records', async (req, res) => {
    try {
        const db = req.db;
        const { patientId } = req.params;
        const snapshot = await db.collection('MedicalRecords').where('patient_id', '==', patientId).get();
        const records = [];
        snapshot.forEach(doc => {
            records.push({ id: doc.id, ...doc.data() });
        });
        records.sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
        res.json(records);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET /api/doctor/dashboard
// Thống kê & doanh thu tháng này
router.get('/dashboard', async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        
        let doctorId = null;
        let fee = 200000;
        const doctorDoc = await db.collection('Doctors').where('user_id', '==', uid).get();
        if (!doctorDoc.empty) {
            doctorId = doctorDoc.docs[0].id;
            fee = doctorDoc.docs[0].data().consultationFee || 200000;
        }

        const currentMonth = new Date().getMonth();
        const currentYear = new Date().getFullYear();
        let monthCount = 0;
        let totalCount = 0;

        // Tính 7 ngày gần nhất
        const last7Days = [];
        const last7DaysCounts = {};
        for (let i = 6; i >= 0; i--) {
            const d = new Date();
            d.setDate(d.getDate() - i);
            const dateStr = `${d.getDate().toString().padStart(2, '0')}/${(d.getMonth() + 1).toString().padStart(2, '0')}/${d.getFullYear()}`;
            last7Days.push(dateStr);
            last7DaysCounts[dateStr] = 0;
        }

        const snapshot = await db.collection('Appointments').where('doctor_id', 'in', [uid, doctorId].filter(Boolean)).where('status', '==', 'Đã qua').get();
        snapshot.forEach(doc => {
            const data = doc.data();
            totalCount++;
            // Parse date (dd/MM/yyyy)
            const parts = data.appointment_date.split('/');
            if (parts.length === 3) {
                const month = parseInt(parts[1]) - 1;
                const year = parseInt(parts[2]);
                if (month === currentMonth && year === currentYear) {
                    monthCount++;
                }
            }
            
            // Check for last 7 days
            if (last7DaysCounts.hasOwnProperty(data.appointment_date)) {
                last7DaysCounts[data.appointment_date]++;
            }
        });

        res.json({
            thisMonthAppointments: monthCount,
            thisMonthRevenue: monthCount * fee,
            totalAppointments: totalCount,
            consultationFee: fee,
            last7DaysData: {
                dates: last7Days.map(d => d.substring(0, 5)), // Chỉ lấy dd/MM
                counts: last7Days.map(d => last7DaysCounts[d])
            }
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/doctor/profile/fee
// Cập nhật giá khám
router.put('/profile/fee', async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        const { fee } = req.body;
        
        const snapshot = await db.collection('Doctors').where('user_id', '==', uid).get();
        if (snapshot.empty) {
            return res.status(404).json({ error: 'Doctor profile not found' });
        }
        
        const docId = snapshot.docs[0].id;
        await db.collection('Doctors').doc(docId).update({ consultationFee: fee });
        
        res.json({ message: 'Consultation fee updated' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET /api/doctor/reviews
// Lấy danh sách đánh giá của bác sĩ này
router.get('/reviews', async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        let doctorId = null;
        const doctorDoc = await db.collection('Doctors').where('user_id', '==', uid).get();
        if (!doctorDoc.empty) doctorId = doctorDoc.docs[0].id;

        const snapshot = await db.collection('Reviews').where('doctor_id', 'in', [uid, doctorId].filter(Boolean)).get();
        const reviews = [];
        snapshot.forEach(doc => reviews.push({ id: doc.id, ...doc.data() }));
        reviews.sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
        res.json(reviews);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET /api/doctor/my-patients
// Lấy danh sách bệnh nhân đã từng khám
router.get('/my-patients', async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        
        let doctorId = null;
        const doctorDoc = await db.collection('Doctors').where('user_id', '==', uid).get();
        if (!doctorDoc.empty) {
            doctorId = doctorDoc.docs[0].id;
        }

        const snapshot = await db.collection('Appointments').where('doctor_id', 'in', [uid, doctorId].filter(Boolean)).get();
        const patientMap = new Map();
        
        for (const doc of snapshot.docs) {
            const data = doc.data();
            const patientId = data.patient_id;
            
            if (patientId && !patientMap.has(patientId)) {
                // Lấy thông tin chi tiết bệnh nhân từ Users collection
                const patientDoc = await db.collection('Users').doc(patientId).get();
                if (patientDoc.exists) {
                    const patientData = patientDoc.data();
                    patientMap.set(patientId, {
                        id: patientId,
                        name: patientData.displayName || data.patient_name || 'Không rõ',
                        phone: patientData.phone || data.patient_phone || '',
                        gender: patientData.gender || data.patient_gender || '',
                        dob: patientData.dob || data.patient_dob || '',
                        avatarUrl: patientData.avatarUrl || ''
                    });
                }
            }
        }
        
        res.json(Array.from(patientMap.values()));
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
