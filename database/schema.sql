-- Create Users Table
CREATE TABLE Users (
    id NVARCHAR(100) PRIMARY KEY, -- Firebase Auth UID
    email NVARCHAR(100),
    name NVARCHAR(100),
    phone NVARCHAR(20),
    dob NVARCHAR(20),
    gender NVARCHAR(10),
    address NVARCHAR(255),
    role NVARCHAR(20) DEFAULT 'PATIENT',
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- Create Specialties Table
CREATE TABLE Specialties (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500)
);
GO

-- Create Hospitals Table
CREATE TABLE Hospitals (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    address NVARCHAR(255)
);
GO

-- Create Doctors Table
CREATE TABLE Doctors (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    specialty NVARCHAR(100),
    hospital NVARCHAR(100),
    rating FLOAT DEFAULT 0,
    reviewCount INT DEFAULT 0,
    imageResId INT DEFAULT 0
);
GO

-- Create Appointments Table
CREATE TABLE Appointments (
    id INT IDENTITY(1,1) PRIMARY KEY,
    patient_id NVARCHAR(100) FOREIGN KEY REFERENCES Users(id),
    doctor_id INT FOREIGN KEY REFERENCES Doctors(id),
    appointment_date NVARCHAR(20),
    appointment_time NVARCHAR(20),
    status NVARCHAR(50) DEFAULT 'Sắp tới',
    type NVARCHAR(50),
    patient_name NVARCHAR(100),
    patient_phone NVARCHAR(20),
    patient_dob NVARCHAR(20),
    patient_gender NVARCHAR(10)
);
GO

-- Insert Dummy Data for Doctors
INSERT INTO Doctors (name, specialty, hospital, rating, reviewCount, imageResId)
VALUES 
('TS.BS Nguyễn Văn A', 'Tim mạch', 'Bệnh viện Chợ Rẫy', 4.8, 120, 2131165300),
('ThS.BS Trần Thị B', 'Nhi khoa', 'Bệnh viện Nhi Đồng 1', 4.9, 200, 2131165301),
('BS Lê Văn C', 'Da liễu', 'Bệnh viện Da Liễu', 4.5, 85, 2131165302);
GO

/*
-- LỆNH CẬP NHẬT DATABASE (CHẠY NẾU DATABASE ĐÃ TỒN TẠI) --
ALTER TABLE Users ADD dob NVARCHAR(20);
ALTER TABLE Users ADD gender NVARCHAR(10);
ALTER TABLE Users ADD address NVARCHAR(255);

ALTER TABLE Appointments ADD patient_name NVARCHAR(100);
ALTER TABLE Appointments ADD patient_phone NVARCHAR(20);
ALTER TABLE Appointments ADD patient_dob NVARCHAR(20);
ALTER TABLE Appointments ADD patient_gender NVARCHAR(10);
*/
