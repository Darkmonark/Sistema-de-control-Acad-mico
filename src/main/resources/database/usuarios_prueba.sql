-- ============================================
-- Usuarios de Prueba - Hospital San Rafael
-- ============================================

-- Usuario Administrador
INSERT INTO usuarios (username, email, password_hash, full_name, role)
VALUES ('admin', 'admin@hospital.com', 'admin123', 'Administrador', 'Administrador')
ON CONFLICT (username) DO NOTHING;

-- Usuario Doctor
INSERT INTO persona (id, nombre, apellido, email, telefono, fecha_nacimiento, genero, direccion, tipo_persona)
VALUES ('D001', 'Carlos', 'Mendoza', 'carlos.mendoza@hospital.com', '555-0101', '1985-03-15', 'M', 'Av. Principal 123', 'DOCTOR')
ON CONFLICT (id) DO NOTHING;

INSERT INTO doctor (id_persona, especialidad, numero_colegiado, area_asignada, anios_experiencia)
VALUES ('D001', 'Medicina Interna', 'COL-12345', 'Consulta Externa', 10)
ON CONFLICT (id_persona) DO NOTHING;

INSERT INTO usuarios (username, email, password_hash, full_name, role)
VALUES ('doctor1', 'carlos.mendoza@hospital.com', 'doctor123', 'Carlos Mendoza', 'Doctor')
ON CONFLICT (username) DO NOTHING;

-- Usuario Estudiante
INSERT INTO persona (id, nombre, apellido, email, telefono, fecha_nacimiento, genero, direccion, tipo_persona)
VALUES ('E001', 'Juan', 'Pérez', 'juan.perez@estudiante.com', '555-1001', '2000-01-15', 'M', 'Calle 10 #20-30', 'ESTUDIANTE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO estudiante (id_persona, carrera, semestre, turno)
VALUES ('E001', 'Medicina', 5, 'Mañana')
ON CONFLICT (id_persona) DO NOTHING;

INSERT INTO usuarios (username, email, password_hash, full_name, role)
VALUES ('estudiante1', 'juan.perez@estudiante.com', 'estudiante123', 'Juan Pérez', 'Estudiante')
ON CONFLICT (username) DO NOTHING;

-- Mostrar usuarios creados
SELECT username, email, full_name, role FROM usuarios;
