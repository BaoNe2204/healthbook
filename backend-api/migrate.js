const { sql, poolPromise } = require('./db-config');

async function fixEncoding() {
    try {
        const pool = await poolPromise;
        console.log('Connected to DB, fixing Vietnamese encoding and adjusting columns...');
        
        // Ensure Users has status column
        await pool.request().query(`
            IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Users') AND name = 'status')
            BEGIN
                ALTER TABLE Users ADD status NVARCHAR(20) DEFAULT 'active';
            END
        `);
        console.log('Users table status column check/creation done.');

        // Ensure Doctors has user_id column
        await pool.request().query(`
            IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Doctors') AND name = 'user_id')
            BEGIN
                ALTER TABLE Doctors ADD user_id NVARCHAR(100) NULL;
            END
        `);
        console.log('Doctors table user_id column check/creation done.');

        // Ensure MedicalRecords table exists
        await pool.request().query(`
            IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[MedicalRecords]') AND type in (N'U'))
            BEGIN
                CREATE TABLE MedicalRecords (
                    id INT IDENTITY(1,1) PRIMARY KEY,
                    doctor_id INT,
                    patient_id NVARCHAR(100),
                    appointment_id INT,
                    diagnosis NVARCHAR(500),
                    prescription NVARCHAR(1000),
                    notes NVARCHAR(1000),
                    created_at DATETIME DEFAULT GETDATE()
                );
            END
        `);
        console.log('MedicalRecords table check/creation done.');

        // Ensure Schedules table exists
        await pool.request().query(`
            IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Schedules]') AND type in (N'U'))
            BEGIN
                CREATE TABLE Schedules (
                    id INT IDENTITY(1,1) PRIMARY KEY,
                    doctor_id INT,
                    available_date NVARCHAR(20),
                    time_slots NVARCHAR(500),
                    updated_at DATETIME DEFAULT GETDATE()
                );
            END
        `);
        console.log('Schedules table check/creation done.');

        // Update row 1
        await pool.request().query(`
            UPDATE Doctors 
            SET name = N'TS.BS Nguyễn Văn A', 
                specialty = N'Tim mạch', 
                hospital = N'Bệnh viện Bạch Mai',
                experience = 15,
                price = 500000,
                description = N'Bác sĩ chuyên khoa Tim mạch hàng đầu, có hơn 15 năm kinh nghiệm tu nghiệp tại Pháp.'
            WHERE id = 1;
        `);

        // Update row 2
        await pool.request().query(`
            UPDATE Doctors 
            SET name = N'BS.CKII Trần Thị Hằng', 
                specialty = N'Nhi khoa', 
                hospital = N'Bệnh viện Nhi Trung Ương',
                experience = 10,
                price = 300000,
                description = N'Bác sĩ tận tâm với trẻ nhỏ, điều trị dứt điểm các bệnh lý hô hấp, tiêu hóa.'
            WHERE id = 2;
        `);

        // Update row 3
        await pool.request().query(`
            UPDATE Doctors 
            SET name = N'BS.CKI Lê Văn Thành', 
                specialty = N'Cơ xương khớp', 
                hospital = N'Bệnh viện Việt Đức',
                experience = 8,
                price = 250000,
                description = N'Chuyên gia chẩn đoán và điều trị các bệnh cơ xương khớp, gout, thoái hóa khớp.'
            WHERE id = 3;
        `);

        console.log('Fixed encoding and updated data successfully!');
        process.exit(0);
    } catch (err) {
        console.error('Error during migration:', err);
        process.exit(1);
    }
}

fixEncoding();
