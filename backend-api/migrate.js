const { sql, poolPromise } = require('./db-config');

async function fixEncoding() {
    try {
        const pool = await poolPromise;
        console.log('Connected to DB, fixing Vietnamese encoding...');
        
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
        console.error('Error fixing encoding:', err);
        process.exit(1);
    }
}

fixEncoding();
