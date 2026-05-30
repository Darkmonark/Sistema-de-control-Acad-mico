-- ============================================
-- Hospital San Rafael - Base de Datos PostgreSQL
-- Creado para: Proyecto Integrador
-- ============================================

-- Eliminar tablas si existen (en orden inverso de dependencias)
DROP TABLE IF EXISTS registro_hospitalario CASCADE;
DROP TABLE IF EXISTS horario CASCADE;
DROP TABLE IF EXISTS materia CASCADE;
DROP TABLE IF EXISTS estudiante_materia CASCADE;
DROP TABLE IF EXISTS doctor_estudiante CASCADE;
DROP TABLE IF EXISTS estudiante CASCADE;
DROP TABLE IF EXISTS doctor CASCADE;
DROP TABLE IF EXISTS persona CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- ============================================
-- Tabla Persona (Tabla padre para Estudiante y Doctor)
-- ============================================
CREATE TABLE persona (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    telefono VARCHAR(20),
    fecha_nacimiento DATE,
    genero VARCHAR(10),
    direccion VARCHAR(200),
    tipo_persona VARCHAR(20) NOT NULL CHECK (tipo_persona IN ('ESTUDIANTE', 'DOCTOR')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Tabla Usuarios (login credentials)
-- ============================================
CREATE TABLE usuarios (
    username VARCHAR(50) PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('Administrador', 'Doctor', 'Estudiante')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Tabla Estudiante
-- ============================================
CREATE TABLE estudiante (
    id_persona VARCHAR(20) PRIMARY KEY REFERENCES persona(id) ON DELETE CASCADE,
    carrera VARCHAR(100) NOT NULL,
    semestre INTEGER NOT NULL CHECK (semestre BETWEEN 1 AND 10),
    turno VARCHAR(20) CHECK (turno IN ('Mañana', 'Tarde', 'Noche')),
    estado VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'INACTIVO', 'GRADUADO')),
    fecha_ingreso DATE DEFAULT CURRENT_DATE
);

-- ============================================
-- Tabla Doctor
-- ============================================
CREATE TABLE doctor (
    id_persona VARCHAR(20) PRIMARY KEY REFERENCES persona(id) ON DELETE CASCADE,
    especialidad VARCHAR(100) NOT NULL,
    numero_colegiado VARCHAR(50) UNIQUE NOT NULL,
    area_asignada VARCHAR(100),
    anios_experiencia INTEGER DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'INACTIVO', 'VACACIONES')),
    fecha_ingreso DATE DEFAULT CURRENT_DATE
);

-- ============================================
-- Tabla Materia
-- ============================================
CREATE TABLE materia (
    codigo VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    creditos INTEGER NOT NULL CHECK (creditos > 0),
    semestre_recomendado INTEGER CHECK (semestre_recomendado BETWEEN 1 AND 10),
    profesor_responsable VARCHAR(150),
    aula VARCHAR(50),
    cupo_maximo INTEGER DEFAULT 30,
    estado VARCHAR(20) DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'INACTIVA'))
);

-- ============================================
-- Tabla Horario
-- ============================================
CREATE TABLE horario (
    id SERIAL PRIMARY KEY,
    dia_semana VARCHAR(15) NOT NULL CHECK (dia_semana IN ('Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo')),
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    actividad VARCHAR(100),
    responsable VARCHAR(150),
    aula VARCHAR(50),
    id_materia VARCHAR(20) REFERENCES materia(codigo) ON DELETE CASCADE,
    id_persona VARCHAR(20) REFERENCES persona(id) ON DELETE CASCADE,
    tipo_horario VARCHAR(20) CHECK (tipo_horario IN ('CLASE', 'GUARDIA', 'CONSULTA', 'PRACTICA'))
);

-- ============================================
-- Tabla intermedia Estudiante-Materia (Inscripciones)
-- ============================================
CREATE TABLE estudiante_materia (
    id SERIAL PRIMARY KEY,
    id_estudiante VARCHAR(20) REFERENCES estudiante(id_persona) ON DELETE CASCADE,
    codigo_materia VARCHAR(20) REFERENCES materia(codigo) ON DELETE CASCADE,
    estado_inscripcion VARCHAR(20) DEFAULT 'INSCRITO' CHECK (estado_inscripcion IN ('INSCRITO', 'APROBADA', 'REPROBADA', 'RETIRADA')),
    nota_final DECIMAL(5,2),
    periodo_academico VARCHAR(20),
    fecha_inscripcion DATE DEFAULT CURRENT_DATE,
    UNIQUE(id_estudiante, codigo_materia)
);

-- ============================================
-- Tabla intermedia Doctor-Estudiante (Tutorías/Supervisiones)
-- ============================================
CREATE TABLE doctor_estudiante (
    id SERIAL PRIMARY KEY,
    id_doctor VARCHAR(20) REFERENCES doctor(id_persona) ON DELETE CASCADE,
    id_estudiante VARCHAR(20) REFERENCES estudiante(id_persona) ON DELETE CASCADE,
    tipo_relacion VARCHAR(50) DEFAULT 'TUTOR',
    fecha_asignacion DATE DEFAULT CURRENT_DATE,
    estado VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'FINALIZADO')),
    UNIQUE(id_doctor, id_estudiante)
);

-- ============================================
-- Tabla Registro Hospitalario
-- ============================================
CREATE TABLE registro_hospitalario (
    id SERIAL PRIMARY KEY,
    id_persona VARCHAR(20) REFERENCES persona(id),
    tipo_registro VARCHAR(20) CHECK (tipo_registro IN ('INGRESO', 'SALIDA')),
    fecha_registro DATE DEFAULT CURRENT_DATE,
    hora_registro TIME DEFAULT CURRENT_TIME,
    area_acceso VARCHAR(100),
    observaciones TEXT,
    ip_origen VARCHAR(50)
);

-- ============================================
-- Índices para mejorar rendimiento
-- ============================================
CREATE INDEX idx_persona_tipo ON persona(tipo_persona);
CREATE INDEX idx_persona_email ON persona(email);
CREATE INDEX idx_estudiante_carrera ON estudiante(carrera);
CREATE INDEX idx_estudiante_semestre ON estudiante(semestre);
CREATE INDEX idx_doctor_especialidad ON doctor(especialidad);
CREATE INDEX idx_materia_semestre ON materia(semestre_recomendado);
CREATE INDEX idx_horario_dia ON horario(dia_semana);
CREATE INDEX idx_horario_materia ON horario(id_materia);
CREATE INDEX idx_estudiante_materia_estudiante ON estudiante_materia(id_estudiante);
CREATE INDEX idx_estudiante_materia_materia ON estudiante_materia(codigo_materia);
CREATE INDEX idx_registro_fecha ON registro_hospitalario(fecha_registro);

-- ============================================
-- Datos de Ejemplo
-- ============================================

-- Insertar Personas (Estudiantes)
INSERT INTO persona (id, nombre, apellido, email, telefono, fecha_nacimiento, genero, direccion, tipo_persona) VALUES
('E001', 'José', 'Martínez', 'jose.martinez@estudiante.com', '555-1001', '2000-01-15', 'M', 'Calle 10 #20-30', 'ESTUDIANTE'),
('E002', 'Sofía', 'Hernández', 'sofia.hernandez@estudiante.com', '555-1002', '2001-03-22', 'F', 'Carrera 5 #15-40', 'ESTUDIANTE'),
('E003', 'Diego', 'Torres', 'diego.torres@estudiante.com', '555-1003', '1999-07-10', 'M', 'Av. 68 #90-10', 'ESTUDIANTE'),
('E004', 'Valentina', 'Ramírez', 'valentina.ramirez@estudiante.com', '555-1004', '2000-11-05', 'F', 'Calle 72 #15-30', 'ESTUDIANTE'),
('E005', 'Andrés', 'Vargas', 'andres.vargas@estudiante.com', '555-1005', '1998-09-18', 'M', 'Transversal 15 #80-20', 'ESTUDIANTE');

-- Insertar Personas (Doctores)
INSERT INTO persona (id, nombre, apellido, email, telefono, fecha_nacimiento, genero, direccion, tipo_persona) VALUES
('D001', 'Carlos', 'Mendoza', 'carlos.mendoza@hospital.com', '555-0101', '1985-03-15', 'M', 'Av. Principal 123', 'DOCTOR'),
('D002', 'Ana', 'Rodríguez', 'ana.rodriguez@hospital.com', '555-0102', '1990-07-22', 'F', 'Calle 45 #678', 'DOCTOR'),
('D003', 'Luis', 'García', 'luis.garcia@hospital.com', '555-0103', '1982-11-30', 'M', 'Carrera 15 #90', 'DOCTOR'),
('D004', 'María', 'López', 'maria.lopez@hospital.com', '555-0104', '1988-05-18', 'F', 'Transversal 20 #45', 'DOCTOR'),
('D005', 'Roberto', 'Silva', 'roberto.silva@hospital.com', '555-0105', '1979-09-25', 'M', 'Calle 100 #15', 'DOCTOR');

-- Insertar Estudiantes
INSERT INTO estudiante (id_persona, carrera, semestre, turno) VALUES
('E001', 'Medicina General', 1, 'Mañana'),
('E002', 'Medicina General', 2, 'Mañana'),
('E003', 'Enfermería', 3, 'Tarde'),
('E004', 'Medicina General', 4, 'Mañana'),
('E005', 'Enfermería', 5, 'Tarde');

-- Insertar Doctores
INSERT INTO doctor (id_persona, especialidad, numero_colegiado, area_asignada, anios_experiencia) VALUES
('D001', 'Medicina Interna', 'COL-12345', 'Pabellón A', 10),
('D002', 'Pediatría', 'COL-67890', 'Pabellón B', 7),
('D003', 'Cirugía General', 'COL-11111', 'Quirófano', 12),
('D004', 'Ginecología', 'COL-22222', 'Pabellón C', 8),
('D005', 'Cardiología', 'COL-33333', 'Cardiología', 15);

-- Insertar Materias
INSERT INTO materia (codigo, nombre, descripcion, creditos, semestre_recomendado, profesor_responsable, aula) VALUES
('MED101', 'Anatomía Humana I', 'Estudio de la anatomía del cuerpo humano', 4, 1, 'Dr. Juan Pérez', 'Aula 101'),
('MED102', 'Fisiología', 'Principios de fisiología humana', 4, 1, 'Dra. Laura Gómez', 'Aula 102'),
('MED201', 'Patología General', 'Introducción a la patología', 5, 2, 'Dr. Pedro Sánchez', 'Aula 201'),
('MED202', 'Farmacología I', 'Fundamentos de farmacología', 4, 2, 'Dra. Carmen Díaz', 'Aula 202'),
('MED301', 'Medicina Interna I', 'Principios de medicina interna', 6, 3, 'Dr. Carlos Mendoza', 'Aula 301'),
('MED302', 'Pediatría I', 'Fundamentos de pediatría', 5, 3, 'Dra. Ana Rodríguez', 'Aula 302'),
('MED401', 'Cirugía General I', 'Principios de cirugía', 6, 4, 'Dr. Luis García', 'Quirófano 1'),
('MED402', 'Ginecología I', 'Fundamentos de ginecología', 5, 4, 'Dra. María López', 'Aula 402'),
('MED501', 'Cardiología Avanzada', 'Cardiología clínica', 6, 5, 'Dr. Roberto Silva', 'Cardiología'),
('MED502', 'Prácticas Hospitalarias', 'Prácticas en Hospital San Rafael', 8, 5, 'Varios', 'Hospital San Rafael');

-- Insertar Horarios de Clases
INSERT INTO horario (dia_semana, hora_inicio, hora_fin, actividad, responsable, aula, id_materia, tipo_horario) VALUES
('Lunes', '08:00', '10:00', 'Clase Teórica', 'Dr. Juan Pérez', 'Aula 101', 'MED101', 'CLASE'),
('Miércoles', '08:00', '10:00', 'Clase Teórica', 'Dr. Juan Pérez', 'Aula 101', 'MED101', 'CLASE'),
('Viernes', '08:00', '10:00', 'Clase Práctica', 'Dr. Juan Pérez', 'Laboratorio', 'MED101', 'CLASE'),
('Martes', '10:00', '12:00', 'Clase Teórica', 'Dra. Laura Gómez', 'Aula 102', 'MED102', 'CLASE'),
('Jueves', '10:00', '12:00', 'Clase Práctica', 'Dra. Laura Gómez', 'Laboratorio', 'MED102', 'CLASE');

-- Insertar Inscripciones Estudiante-Materia
INSERT INTO estudiante_materia (id_estudiante, codigo_materia, periodo_academico) VALUES
('E001', 'MED101', '2026-1'),
('E001', 'MED102', '2026-1'),
('E002', 'MED201', '2026-1'),
('E002', 'MED202', '2026-1'),
('E003', 'MED301', '2026-1'),
('E003', 'MED302', '2026-1');

-- Insertar Relación Doctor-Estudiante (Tutorías)
INSERT INTO doctor_estudiante (id_doctor, id_estudiante, tipo_relacion) VALUES
('D001', 'E001', 'TUTOR ACADÉMICO'),
('D001', 'E002', 'TUTOR ACADÉMICO'),
('D002', 'E003', 'TUTOR DE PRÁCTICAS'),
('D003', 'E004', 'DIRECTOR TESIS'),
('D005', 'E005', 'TUTOR ACADÉMICO');

-- Insertar Horarios de Atención (Doctores)
INSERT INTO horario (dia_semana, hora_inicio, hora_fin, actividad, responsable, aula, id_persona, tipo_horario) VALUES
('Lunes', '08:00', '12:00', 'Consulta Externa', 'Dr. Carlos Mendoza', 'Pabellón A', 'D001', 'CONSULTA'),
('Miércoles', '08:00', '12:00', 'Consulta Externa', 'Dr. Carlos Mendoza', 'Pabellón A', 'D001', 'CONSULTA'),
('Viernes', '08:00', '12:00', 'Consulta Externa', 'Dr. Carlos Mendoza', 'Pabellón A', 'D001', 'CONSULTA'),
('Martes', '09:00', '13:00', 'Atención Pediatría', 'Dra. Ana Rodríguez', 'Pabellón B', 'D002', 'CONSULTA'),
('Jueves', '09:00', '13:00', 'Atención Pediatría', 'Dra. Ana Rodríguez', 'Pabellón B', 'D002', 'CONSULTA');

-- ============================================
-- Consultas Útiles (Vistas)
-- ============================================

-- Vista: Estudiantes con sus carreras
CREATE OR REPLACE VIEW v_estudiantes_carrera AS
SELECT p.id, p.nombre, p.apellido, p.email, e.carrera, e.semestre, e.turno
FROM persona p
JOIN estudiante e ON p.id = e.id_persona
WHERE p.tipo_persona = 'ESTUDIANTE';

-- Vista: Doctores con especialidades
CREATE OR REPLACE VIEW v_doctores_especialidad AS
SELECT p.id, p.nombre, p.apellido, p.email, d.especialidad, d.numero_colegiado, d.area_asignada
FROM persona p
JOIN doctor d ON p.id = d.id_persona
WHERE p.tipo_persona = 'DOCTOR';

-- Vista: Horario semanal por estudiante
CREATE OR REPLACE VIEW v_horario_estudiantes AS
SELECT
    p.nombre AS estudiante,
    p.apellido,
    p.email,
    est.carrera,
    est.semestre,
    h.dia_semana,
    h.hora_inicio,
    h.hora_fin,
    m.nombre AS materia,
    h.aula
FROM persona p
JOIN estudiante est ON p.id = est.id_persona
JOIN estudiante_materia em ON est.id_persona = em.id_estudiante
JOIN materia m ON em.codigo_materia = m.codigo
JOIN horario h ON m.codigo = h.id_materia
WHERE p.tipo_persona = 'ESTUDIANTE'
ORDER BY est.semestre, h.dia_semana, h.hora_inicio;

-- ============================================
-- Verificar datos insertados
-- ============================================
SELECT 'Personas Registradas' AS concepto, COUNT(*) AS cantidad FROM persona
UNION ALL
SELECT 'Estudiantes', COUNT(*) FROM estudiante
UNION ALL
SELECT 'Doctores', COUNT(*) FROM doctor
UNION ALL
SELECT 'Materias', COUNT(*) FROM materia
UNION ALL
SELECT 'Horarios', COUNT(*) FROM horario
UNION ALL
SELECT 'Inscripciones', COUNT(*) FROM estudiante_materia
UNION ALL
SELECT 'Tutorías', COUNT(*) FROM doctor_estudiante;
