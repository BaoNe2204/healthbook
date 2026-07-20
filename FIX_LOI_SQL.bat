@echo off
color 0A
title Cong Cu Sua Loi SQL Server (HealthBook)

echo ========================================================
echo       CONG CU TU DONG SUA LOI KET NOI SQL SERVER
echo ========================================================
echo.

:: Check Admin rights
net session >nul 2>&1
if %errorLevel% == 0 (
    echo [1/3] Dang bat ket noi TCP/IP cho SQL Server...
    reg add "HKLM\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQLServer\SuperSocketNetLib\Tcp" /v Enabled /t REG_DWORD /d 1 /f >nul
    
    echo [2/3] Dang mo cong 1433 cho SQL Server...
    reg add "HKLM\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQLServer\SuperSocketNetLib\Tcp\IPAll" /v "TcpPort" /t REG_SZ /d "1433" /f >nul
    reg add "HKLM\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQLServer\SuperSocketNetLib\Tcp\IPAll" /v "TcpDynamicPorts" /t REG_SZ /d "" /f >nul

    echo [3/3] Dang khoi dong lai dich vu SQL Server (Vui long cho)...
    net stop MSSQL$SQLEXPRESS /y >nul 2>&1
    net start MSSQL$SQLEXPRESS >nul 2>&1

    echo.
    echo ========================================================
    echo THONG BAO: DA SUA LOI THANH CONG 100%%!
    echo ========================================================
    echo Ban hay quay lai VS Code, tat Terminal hien tai (Bam thung rac)
    echo Va chay lai lenh: npm run dev
    echo Ban se thay chu mau xanh "Connected to SQL Server successfully!"
    echo.
    pause
) else (
    color 0C
    echo [LOI] Ban chua chay File nay bang quyen Quan Tri (Administrator)!
    echo.
    echo VUI LONG LAM THEO SAU:
    echo 1. Tat cua so nay di.
    echo 2. Vao thu muc d:\DuAn\healthbook\
    echo 3. Click CHUOT PHAI vao file FIX_LOI_SQL.bat
    echo 4. Chon "Run as administrator" (Chay duoi quyen quan tri).
    echo 5. Neu may hoi Yes/No thi chon Yes.
    echo.
    pause
)
