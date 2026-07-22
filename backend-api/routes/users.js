const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middleware/auth');
const multer = require('multer');
const path = require('path');

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'public/uploads');
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, 'avatar-' + uniqueSuffix + path.extname(file.originalname));
    }
});
const upload = multer({ storage: storage });

// Lấy tham chiếu tới Firestore từ req.db (đã được cấu hình ở server.js)

// GET /api/users/profile
// Lấy thông tin chi tiết user
router.get('/profile', verifyToken, async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        
        const docRef = db.collection('Users').doc(uid);
        const doc = await docRef.get();
        
        if (!doc.exists) {
            return res.status(404).json({ error: 'User not found in database' });
        }
        const data = doc.data();
        data.uid = uid; // Ensure uid is included
        res.json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/users/profile
// Cập nhật thông tin cá nhân
router.put('/profile', verifyToken, async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        const { displayName, phone, dob, gender, address } = req.body;
        
        console.log(`[PUT /profile] User UID: ${uid}`);
        console.log(`[PUT /profile] Data received:`, req.body);
        
        const docRef = db.collection('Users').doc(uid);
        
        const updateData = {
            role: 'PATIENT' // Default role for new profiles just in case
        };
        if (req.body.displayName !== undefined) updateData.displayName = req.body.displayName;
        if (req.body.phone !== undefined) updateData.phone = req.body.phone;
        if (req.body.dob !== undefined) updateData.dob = req.body.dob;
        if (req.body.gender !== undefined) updateData.gender = req.body.gender;
        if (req.body.address !== undefined) updateData.address = req.body.address;
        if (req.body.insuranceCode !== undefined) updateData.insuranceCode = req.body.insuranceCode;
        if (req.body.hospitalRegister !== undefined) updateData.hospitalRegister = req.body.hospitalRegister;
        if (req.body.insuranceExpiry !== undefined) updateData.insuranceExpiry = req.body.insuranceExpiry;
        if (req.body.relativeName !== undefined) updateData.relativeName = req.body.relativeName;
        if (req.body.relativeRelation !== undefined) updateData.relativeRelation = req.body.relativeRelation;
        if (req.body.relativePhone !== undefined) updateData.relativePhone = req.body.relativePhone;
        if (req.body.weight !== undefined) updateData.weight = req.body.weight;
        if (req.body.height !== undefined) updateData.height = req.body.height;

        await docRef.set(updateData, { merge: true });
        
        // Cập nhật luôn displayName trên Firebase Auth để đồng bộ với App
        if (req.body.displayName !== undefined) {
            const { getAuth } = require('firebase-admin/auth');
            try {
                await getAuth().updateUser(uid, {
                    displayName: req.body.displayName
                });
            } catch (err) {
                console.error('Lỗi khi update Firebase Auth:', err);
            }
        }
        // Sinh thông báo
        await db.collection('Notifications').add({
            user_id: uid,
            title: 'Cập nhật hồ sơ',
            body: 'Thông tin hồ sơ cá nhân của bạn đã được cập nhật thành công.',
            type: 'PROFILE_UPDATE',
            created_at: new Date().toISOString()
        });
            
        res.json({ message: 'Profile updated successfully' });
    } catch (error) {
        console.error(`[PUT /profile] Error:`, error);
        res.status(500).json({ error: error.message });
    }
});

// POST /api/users/register
// Gọi API này ngay sau khi App vừa đăng ký tài khoản Firebase Auth thành công
router.post('/register', verifyToken, async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        const email = req.user.email;
        const { displayName, isDoctorRegistration } = req.body; 
        
        const assignedRole = isDoctorRegistration ? 'DOCTOR' : 'PATIENT';
        const name = displayName || email.split('@')[0];

        const docRef = db.collection('Users').doc(uid);
        const doc = await docRef.get();
        
        if (doc.exists) {
            return res.status(400).json({ error: 'User already registered in database' });
        }

        const userData = {
            email: email,
            displayName: name,
            role: assignedRole,
            createdAt: new Date().toISOString()
        };

        await docRef.set(userData);

        if (isDoctorRegistration) {
            try {
                // Thêm một bản ghi vào collection Doctors
                await db.collection('Doctors').add({
                    name: name,
                    user_id: uid,
                    rating: 5.0,
                    reviewCount: 0,
                    imageResId: 0
                });
            } catch (err) {
                console.error("Error creating Doctor profile link:", err);
            }
        }

        // Sinh thông báo
        await db.collection('Notifications').add({
            user_id: uid,
            title: 'Đăng ký thành công',
            body: 'Chào mừng bạn đến với HealthBook! Tài khoản của bạn đã được khởi tạo thành công.',
            type: 'REGISTER',
            created_at: new Date().toISOString()
        });

        userData.uid = uid;
        res.status(201).json(userData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/users/upload-avatar
router.post('/upload-avatar', verifyToken, upload.single('avatar'), async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({ error: 'No file uploaded' });
        }
        const db = req.db;
        const uid = req.user.uid;
        
        // Construct the URL. In production, this would be a proper domain.
        // For local development on Android emulator or same WiFi, we use the server IP.
        const protocol = req.protocol;
        const host = req.get('host'); // Will be something like 192.168.1.2:3000
        const avatarUrl = `${protocol}://${host}/uploads/${req.file.filename}`;

        const docRef = db.collection('Users').doc(uid);
        await docRef.set({ avatarUrl: avatarUrl }, { merge: true });

        // Update Firebase Auth photoURL
        const { getAuth } = require('firebase-admin/auth');
        try {
            await getAuth().updateUser(uid, { photoURL: avatarUrl });
        } catch (err) {
            console.error('Error updating Firebase Auth photoURL:', err);
        }

        res.json({ message: 'Avatar uploaded successfully', avatarUrl: avatarUrl });
    } catch (error) {
        console.error('Error uploading avatar:', error);
        res.status(500).json({ error: error.message });
    }
});

// POST /api/users/reviews
// Đánh giá bác sĩ
router.post('/reviews', verifyToken, async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        const doctorId = req.body.doctorId || req.body.doctor_id;
        const appointmentId = req.body.appointmentId || req.body.appointment_id;
        const rating = req.body.rating;
        const comment = req.body.comment;
        
        if (!doctorId || !rating) {
            return res.status(400).json({ error: 'doctorId and rating are required' });
        }

        const reviewData = {
            patient_id: uid,
            doctor_id: doctorId,
            appointment_id: appointmentId || null,
            rating: rating,
            comment: comment || '',
            created_at: new Date().toISOString()
        };

        const docRef = await db.collection('Reviews').add(reviewData);

        // Calculate new average rating for the doctor
        const snapshot = await db.collection('Reviews').where('doctor_id', '==', doctorId).get();
        let totalRating = 0;
        let count = 0;
        snapshot.forEach(doc => {
            totalRating += doc.data().rating;
            count++;
        });
        const avgRating = count > 0 ? (totalRating / count).toFixed(1) : 5.0;

        await db.collection('Doctors').doc(doctorId).update({
            rating: parseFloat(avgRating),
            reviewCount: count
        });

        reviewData.id = docRef.id;
        res.status(201).json(reviewData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET /api/users/notifications
// Lấy danh sách thông báo của user
router.get('/notifications', verifyToken, async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        
        const snapshot = await db.collection('Notifications')
            .where('user_id', '==', uid)
            .get();
            
        let notifications = [];
        snapshot.forEach(doc => {
            notifications.push({ id: doc.id, ...doc.data() });
        });
        
        // Sắp xếp giảm dần theo thời gian
        notifications.sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
        
        res.json(notifications);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
