-- =====================================================================
-- SCRIPT DE SEMILLADO DE DATOS (SEED DATA) PARA CARHUB
-- Marcas Japonesas (OEM) + Preparadoras Legendarias (Tuning / Motorsport)
-- Se ejecuta DESPUÉS de que los microservicios hayan creado las tablas
-- via Hibernate ddl-auto=update
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. BASE DE DATOS: automotriz (Tabla: Automotriz - creada por Hibernate)
-- ---------------------------------------------------------------------
USE automotriz;

INSERT INTO Automotriz (Id_Automotriz, Nombre_automotriz, Direccion_automotriz) VALUES
(1, 'Honda', 'Minato, Tokio, Japón - Central Global Honda Motor Co.'),
(2, 'Spoon Sports', 'Suginami-ku, Tokio, Japón - Type One Tuning Facility'),
(3, 'Mugen Motorsports', 'Asaka, Saitama, Japón - M-TEC Co. Headquarters'),
(4, 'Nissan', 'Nishi-ku, Yokohama, Kanagawa, Japón - Nissan HQ'),
(5, 'Nismo', 'Tsurumi-ku, Yokohama, Kanagawa, Japón - Omori Factory'),
(6, 'Toyota', 'Toyota City, Aichi, Japón - Toyota Motor Corporation HQ'),
(7, 'Gazoo Racing', 'Fuji Speedway, Shizuoka, Japón - GR Motorsport Division'),
(8, 'TRD', 'Kouhoku-ku, Yokohama, Japón - Toyota Racing Development HQ'),
(9, 'Mazda', 'Aki-gun, Fuchu, Hiroshima, Japón - Mazda Motor Corp HQ'),
(10, 'Mazdaspeed', 'Minami-ku, Hiroshima, Japón - Mazdaspeed Performance HQ'),
(11, 'Subaru', 'Ebisu, Shibuya-ku, Tokio, Japón - Subaru Corporation HQ'),
(12, 'STI', 'Mitaka, Tokio, Japón - Subaru Tecnica International HQ'),
(13, 'Mitsubishi', 'Minato-ku, Tokio, Japón - Mitsubishi Motors HQ'),
(14, 'Ralliart', 'Minato-ku, Tokio, Japón - Mitsubishi Motorsport Division')
ON DUPLICATE KEY UPDATE
    Nombre_automotriz=VALUES(Nombre_automotriz),
    Direccion_automotriz=VALUES(Direccion_automotriz);


-- ---------------------------------------------------------------------
-- 2. BASE DE DATOS: vehiculos (Tabla: vehiculo_model - creada por Hibernate)
-- ---------------------------------------------------------------------
USE vehiculos;

INSERT INTO vehiculo_model (ID_Vehiculo, Marca_vehiculo, Modelo_vehiculo, Anio_vehiculo, Tipo_combustible, Tipo_transmision, Tipo_propulsion, Precio_vehiculo) VALUES
-- Honda OEM
(1, 'Honda', 'Civic Type R EK9', '1998', 'Bencina 97', 'Manual 5 velocidades', 'Delantera (FWD)', 22500000),
(2, 'Honda', 'S2000 AP1', '2001', 'Bencina 97', 'Manual 6 velocidades', 'Trasera (RWD)', 32000000),
(3, 'Honda', 'NSX NA1 VTEC', '1994', 'Bencina 97', 'Manual 5 velocidades', 'Motor Central - Trasera (MR)', 78000000),
(4, 'Honda', 'Integra Type R DC2', '1996', 'Bencina 97', 'Manual 5 velocidades', 'Delantera (FWD)', 26000000),

-- Spoon Sports
(5, 'Spoon Sports', 'Spoon Civic Type R EK9 Race Car', '1999', 'Bencina Racing 100', 'Manual 5V Close-Ratio', 'Delantera (FWD)', 48000000),
(6, 'Spoon Sports', 'Spoon S2000 GT1 Turbo', '2004', 'Bencina Racing 100', 'Secuencial 6 velocidades', 'Trasera (RWD)', 65000000),
(7, 'Spoon Sports', 'Spoon NSX-R GT Cup', '2003', 'Bencina 97', 'Manual 6 velocidades', 'Motor Central - Trasera (MR)', 115000000),

-- Mugen Motorsports
(8, 'Mugen Motorsports', 'Mugen Civic Type R RR (ABA-FD2)', '2007', 'Bencina 97', 'Manual 6 velocidades', 'Delantera (FWD)', 52000000),
(9, 'Mugen Motorsports', 'Mugen S2000 SS Aero Spec', '2003', 'Bencina 97', 'Manual 6 velocidades', 'Trasera (RWD)', 44000000),

-- Nissan OEM
(10, 'Nissan', 'Skyline GT-R R34 V-Spec II', '2001', 'Bencina 97', 'Manual 6 velocidades Getrag', 'Integral (ATTESA E-TS AWD)', 135000000),
(11, 'Nissan', 'Silvia S15 Spec-R Aero', '2002', 'Bencina 97', 'Manual 6 velocidades', 'Trasera (RWD)', 29500000),
(12, 'Nissan', 'Fairlady Z 370Z Nismo', '2019', 'Bencina 97', 'Manual 6 velocidades SynchroRev', 'Trasera (RWD)', 38000000),

-- Nismo
(13, 'Nismo', 'Skyline GT-R R34 Nismo Z-Tune', '2005', 'Bencina Racing 100', 'Manual 6 velocidades Reforzada', 'Integral (ATTESA E-TS Pro AWD)', 450000000),
(14, 'Nismo', 'Nismo 400R (BCNR33)', '1997', 'Bencina 97', 'Manual 5 velocidades Heavy Duty', 'Integral (AWD)', 280000000),

-- Toyota OEM
(15, 'Toyota', 'Supra RZ Twin Turbo (JZA80)', '1998', 'Bencina 97', 'Manual 6 velocidades Getrag V160', 'Trasera (RWD)', 85000000),
(16, 'Toyota', 'AE86 Sprinter Trueno GT-Apex', '1986', 'Bencina 95', 'Manual 5 velocidades T50', 'Trasera (RWD)', 24000000),
(17, 'Toyota', 'GR Supra 3.0 Pro', '2022', 'Bencina 97', 'Manual 6 velocidades iMT', 'Trasera (RWD)', 56000000),

-- Gazoo Racing
(18, 'Gazoo Racing', 'GR Yaris Circuit Pack Rally Spec', '2021', 'Bencina 97', 'Manual 6 velocidades con Torsen LSD', 'Integral (GR-FOUR AWD)', 42000000),
(19, 'Gazoo Racing', 'GR Corolla Morizo Edition', '2023', 'Bencina 97', 'Manual 6 velocidades Close-Ratio', 'Integral (GR-FOUR AWD)', 51000000),

-- TRD
(20, 'TRD', 'TRD 3000GT (Widebody JZA80)', '1995', 'Bencina 97', 'Manual 6 velocidades', 'Trasera (RWD)', 160000000),
(21, 'TRD', 'TRD Chaser Tourer V (JZX100 1JZ)', '1999', 'Bencina 97', 'Manual 5 velocidades R154', 'Trasera (RWD)', 31000000),

-- Mazda OEM
(22, 'Mazda', 'RX-7 Type R Bathurst R (FD3S)', '2001', 'Bencina 97', 'Manual 5 velocidades', 'Trasera (RWD)', 58000000),
(23, 'Mazda', 'Miata MX-5 NA 1.8 Special Edition', '1995', 'Bencina 95', 'Manual 5 velocidades', 'Trasera (RWD)', 14500000),

-- Mazdaspeed
(24, 'Mazdaspeed', 'Mazdaspeed RX-7 A-Spec LM', '1998', 'Bencina 97', 'Manual 5 velocidades', 'Trasera (RWD)', 68000000),
(25, 'Mazdaspeed', 'Mazdaspeed Atenza / 6 MPS Turbo AWD', '2007', 'Bencina 97', 'Manual 6 velocidades', 'Integral (AWD)', 17500000),

-- Subaru OEM
(26, 'Subaru', 'Impreza 22B STi Version', '1998', 'Bencina 97', 'Manual 5 velocidades DCCD', 'Integral (Symmetrical AWD)', 290000000),
(27, 'Subaru', 'WRX STI Type RA-R (EJ20 Final)', '2019', 'Bencina 97', 'Manual 6 velocidades Quick-Shift', 'Integral (Symmetrical AWD)', 59000000),

-- STI
(28, 'STI', 'STI S209 Widebody Limited', '2020', 'Bencina 97', 'Manual 6 velocidades STI Close-Ratio', 'Integral (DCCD AWD)', 79000000),

-- Mitsubishi OEM
(29, 'Mitsubishi', 'Lancer Evolution VI Tommi Makinen Edition', '2000', 'Bencina 97', 'Manual 5 velocidades Titanio', 'Integral (Super AYC AWD)', 69000000),
(30, 'Mitsubishi', 'Lancer Evolution IX MR GSR', '2006', 'Bencina 97', 'Manual 6 velocidades', 'Integral (ACD + Super AYC AWD)', 49000000),

-- Ralliart
(31, 'Ralliart', 'Lancer Evolution WRC Group A Homologation', '1999', 'Bencina Racing 100', 'Secuencial 6 velocidades Ralliart', 'Integral (Triple LSD AWD)', 120000000)
ON DUPLICATE KEY UPDATE
    Marca_vehiculo=VALUES(Marca_vehiculo),
    Modelo_vehiculo=VALUES(Modelo_vehiculo),
    Anio_vehiculo=VALUES(Anio_vehiculo),
    Tipo_combustible=VALUES(Tipo_combustible),
    Tipo_transmision=VALUES(Tipo_transmision),
    Tipo_propulsion=VALUES(Tipo_propulsion),
    Precio_vehiculo=VALUES(Precio_vehiculo);


-- ---------------------------------------------------------------------
-- 3. BASE DE DATOS: piezas (Tabla: Pieza - creada por Hibernate)
-- ---------------------------------------------------------------------
USE piezas;

INSERT INTO Pieza (ID_Pieza, Tipo_pieza, Nombre_pieza, Material_pieza, Precio_pieza, Tipo_marca_pieza, Stock_pieza, Descripcion_pieza, ID_Vehiculo, Marca_vehiculo, Modelo_vehiculo, ID_Usuario, Nombre_usuario, Correo_usuario) VALUES
-- Piezas para Honda / Spoon (EK9 y S2000)
(1, 'Motor', 'Spoon Sports Blue-Printed B16B Complete Engine Block', 'Aluminio', 7500000, 'ORIGINAL', 3, 'Motor B16B balanceado y optimizado por Spoon Sports a 11,000 RPM.', 1, 'Honda', 'Civic Type R EK9', 1, 'Admin CarHub', 'admin@carhub.cl'),
(2, 'Frenos', 'Spoon Monobloc 4-Pot Caliper Kit (Anodized Blue)', 'Aluminio', 1850000, 'ORIGINAL', 8, 'Kit de pinzas de freno de 4 pistones monobloque Spoon Sports.', 1, 'Honda', 'Civic Type R EK9', 1, 'Admin CarHub', 'admin@carhub.cl'),
(3, 'Frenos', 'Spoon Monobloc Twin-Block Calipers para S2000', 'Aluminio', 2100000, 'ORIGINAL', 5, 'Pinzas delanteras twin-block Spoon para buje AP1 y AP2.', 2, 'Honda', 'S2000 AP1', 1, 'Admin CarHub', 'admin@carhub.cl'),
(4, 'Parachoques', 'Spoon S-Tai Front Aero Bumper con Carbon Under-Tray', 'Fibra de carbono', 2600000, 'ORIGINAL', 2, 'Parachoques delantero aerodinámico Spoon con splitter de carbono.', 6, 'Spoon Sports', 'Spoon S2000 GT1 Turbo', 1, 'Admin CarHub', 'admin@carhub.cl'),
(5, 'Amortiguador', 'Mugen 5-Way Adjustable Damper Suspension Kit', 'Acero', 2900000, 'ORIGINAL', 4, 'Coilovers regulables desarrollados en Suzuka por Mugen.', 8, 'Mugen Motorsports', 'Mugen Civic Type R RR (ABA-FD2)', 1, 'Admin CarHub', 'admin@carhub.cl'),

-- Piezas para Nissan / Nismo (R34 y S15)
(6, 'Motor', 'Nismo RB26DETT R-Tune Carbon Air Intake Plenum', 'Fibra de carbono', 3400000, 'ORIGINAL', 4, 'Multiple de admision de carbono Nismo Omori Factory para RB26DETT.', 10, 'Nissan', 'Skyline GT-R R34 V-Spec II', 1, 'Admin CarHub', 'admin@carhub.cl'),
(7, 'Frenos', 'Brembo Nismo 6-Piston Monobloc Front Brake Kit', 'Aluminio', 4800000, 'ORIGINAL', 3, 'Sistema de frenos sobredimensionado Nismo Z-Tune con discos flotantes.', 13, 'Nismo', 'Skyline GT-R R34 Nismo Z-Tune', 1, 'Admin CarHub', 'admin@carhub.cl'),
(8, 'Llanta', 'Nismo LMGT4 Forged Wheels 18x9.5J by Rays', 'Aluminio', 3200000, 'ORIGINAL', 6, 'Juego de 4 llantas forjadas Rays Nismo en acabado bronce mate.', 10, 'Nissan', 'Skyline GT-R R34 V-Spec II', 1, 'Admin CarHub', 'admin@carhub.cl'),
(9, 'Radiador', 'Koyo Hyper V-Core Radiator para Silvia SR20DET', 'Aluminio', 680000, 'ALTERNATIVO', 12, 'Radiador de aluminio de doble panel para 450 HP.', 11, 'Nissan', 'Silvia S15 Spec-R Aero', 1, 'Admin CarHub', 'admin@carhub.cl'),

-- Piezas para Toyota / GR / TRD (Supra, AE86, GR Yaris)
(10, 'Motor', 'HKS T51R KAI Single Turbo Upgrade Kit para 2JZ-GTE', 'Acero', 5900000, 'ALTERNATIVO', 2, 'Kit completo de turbo HKS con colector T4 y wastegate de 60mm.', 15, 'Toyota', 'Supra RZ Twin Turbo (JZA80)', 1, 'Admin CarHub', 'admin@carhub.cl'),
(11, 'Amortiguador', 'TRD Circuit Spec Coilover Kit con muelles Eibach', 'Acero', 1950000, 'ORIGINAL', 5, 'Suspension TRD para AE86 con regulacion independiente de rebote.', 16, 'Toyota', 'AE86 Sprinter Trueno GT-Apex', 1, 'Admin CarHub', 'admin@carhub.cl'),
(12, 'Parachoques', 'GR Carbon Fiber Front Lip Spoiler & Canards', 'Fibra de carbono', 1450000, 'ORIGINAL', 7, 'Splitter frontal y canards Toyota Gazoo Racing para GR Yaris.', 18, 'Gazoo Racing', 'GR Yaris Circuit Pack Rally Spec', 1, 'Admin CarHub', 'admin@carhub.cl'),

-- Piezas para Mazda / Mazdaspeed (RX-7 Rotativo)
(13, 'Motor', 'Mazdaspeed 13B-REW Street Port Apex Seal Kit', 'Acero', 1250000, 'ORIGINAL', 10, 'Juego completo de sellos de vertice ceramicos Mazdaspeed.', 22, 'Mazda', 'RX-7 Type R Bathurst R (FD3S)', 1, 'Admin CarHub', 'admin@carhub.cl'),
(14, 'Parachoques', 'Mazdaspeed A-Spec Type II Front Aero Kit', 'Plastico ABS', 1750000, 'ORIGINAL', 3, 'Frontal aerodinamico con tomas para intercooler frontal de RX-7.', 24, 'Mazdaspeed', 'Mazdaspeed RX-7 A-Spec LM', 1, 'Admin CarHub', 'admin@carhub.cl'),

-- Piezas para Subaru STI & Mitsubishi Ralliart
(15, 'Frenos', 'STI Brembo Gold Calipers + Endless MX72 Brake Pads', 'Aluminio', 2300000, 'ORIGINAL', 4, 'Set de pinzas doradas STI de 4 pistones con pastillas Endless.', 26, 'Subaru', 'Impreza 22B STi Version', 1, 'Admin CarHub', 'admin@carhub.cl'),
(16, 'Radiador', 'Ralliart High-Pressure Titanium Radiator Cap + Intercooler', 'Aluminio', 1650000, 'ORIGINAL', 5, 'Intercooler frontal de competicion Ralliart para Lancer Evo VI/IX.', 29, 'Mitsubishi', 'Lancer Evolution VI Tommi Makinen Edition', 1, 'Admin CarHub', 'admin@carhub.cl')
ON DUPLICATE KEY UPDATE
    Tipo_pieza=VALUES(Tipo_pieza),
    Nombre_pieza=VALUES(Nombre_pieza),
    Material_pieza=VALUES(Material_pieza),
    Precio_pieza=VALUES(Precio_pieza),
    Tipo_marca_pieza=VALUES(Tipo_marca_pieza),
    Stock_pieza=VALUES(Stock_pieza),
    Descripcion_pieza=VALUES(Descripcion_pieza),
    ID_Vehiculo=VALUES(ID_Vehiculo),
    Marca_vehiculo=VALUES(Marca_vehiculo),
    Modelo_vehiculo=VALUES(Modelo_vehiculo),
    ID_Usuario=VALUES(ID_Usuario),
    Nombre_usuario=VALUES(Nombre_usuario),
    Correo_usuario=VALUES(Correo_usuario);
