# Base de Datos - Hospital San Rafael

## 📋 Estruct de la Base de Datos

### Tablas Principales

#### 1. **persona**
Tabla base que contiene información común de estudiantes y doctores.
- `id`: Identificador único (PK)
- `nombre`, `apellido`: Datos personales
- `email`, `telefono`: Contacto
- `tipo_persona`: 'ESTUDIANTE' o 'DOCTOR'

#### 2. **estudiante**
Información específica de estudiantes.
- `id_persona`: FK → persona(id)
- `carrera`: Programa académico
- `semestre`: 1-10
- `turno`: Mañana/Tarde/Noche

#### 3. **doctor**
Información específica de doctores.
- `id_persona`: FK → persona(id)
- `especialidad`: Área médica
- `numero_colegiado`: Licencia profesional
- `area_asignada`: Departamento

#### 4. **materia**
Catálogo de asignaturas.
- `codigo`: Identificador único (PK)
- `nombre`: Nombre de la materia
- `creditos`: Valor académico
- `semestre_recomendado`: Semestre sugerido

#### 5. **horario**
Horarios de clases y atención.
- `id`: PK autoincremental
- `dia_semana`: Lunes-Domingo
- `hora_inicio`, `hora_fin`: Rango horario
- `tipo_horario`: CLASE/GUARDIA/CONSULTA/PRACTICA

#### 6. **estudiante_materia**
Inscripciones de estudiantes a materias.
- `id_estudiante`: FK → estudiante(id_persona)
- `codigo_materia`: FK → materia(codigo)
- `estado_inscripcion`: INSCRITO/APROBADA/REPROBADA

#### 7. **doctor_estudiante**
Relación de tutorías.
- `id_doctor`: FK → doctor(id_persona)
- `id_estudiante`: FK → estudiante(id_persona)
- `tipo_relacion`: TUTOR ACADÉMICO/DIRECTOR TESIS

#### 8. **registro_hospitalario**
Control de ingresos al hospital.
- `id_persona`: FK → persona(id)
- `tipo_registro`: INGRESO/SALIDA
- `fecha_registro`, `hora_registro`: Timestamp

### Vistas (Views)

#### v_estudiantes_carrera
Listado completo de estudiantes con sus carreras.

#### v_doctores_especialidad
Directorio de doctores por especialidad.

#### v_horario_estudiantes
Horario semanal de clases por estudiante.

## 📊 Diagrama Entidad-Relación

```
PERSONA (1) ──┬── (1) ESTUDIANTE
              │
              └── (1) DOCTOR

ESTUDIANTE (N) ──< ESTUDIANTE_MATERIA (N:N) >── (1) MATERIA

DOCTOR (1) ──< DOCTOR_ESTUDIANTE (1:N) >── ESTUDIANTE (N)

PERSONA (1) ──< HORARIO (N)
              │
              └──< REGISTRO_HOSPITALARIO (N)
```

## 🔄 Consultas Frecuentes

### Listar todos los estudiantes
```sql
SELECT p.id, p.nombre, p.apellido, e.carrera, e.semestre
FROM persona p
JOIN estudiante e ON p.id = e.id_persona
WHERE p.tipo_persona = 'ESTUDIANTE';
```

### Listar doctores por especialidad
```sql
SELECT p.nombre, p.apellido, d.especialidad, d.area_asignada
FROM persona p
JOIN doctor d ON p.id = d.id_persona
WHERE d.especialidad LIKE '%Medicina%'
ORDER BY d.especialidad;
```

### Horario de un estudiante específico
```sql
SELECT 
    h.dia_semana,
    h.hora_inicio,
    h.hora_fin,
    m.nombre AS materia,
    h.aula
FROM horario h
JOIN materia m ON h.id_materia = m.codigo
JOIN estudiante_materia em ON m.codigo = em.codigo_materia
WHERE em.id_estudiante = 'E001'
ORDER BY h.dia_semana, h.hora_inicio;
```

### Estudiantes inscritos en una materia
```sql
SELECT p.nombre, p.apellido, e.carrera
FROM persona p
JOIN estudiante e ON p.id = e.id_persona
JOIN estudiante_materia em ON e.id_persona = em.id_estudiante
WHERE em.codigo_materia = 'MED101';
```

### Doctores con sus estudiantes asignados
```sql
SELECT 
    p_doctor.nombre AS doctor_nombre,
    p_doctor.apellido AS doctor_apellido,
    d.especialidad,
    p_estudiante.nombre AS estudiante_nombre,
    p_estudiante.apellido AS estudiante_apellido,
    de.tipo_relacion
FROM doctor_estudiante de
JOIN persona p_doctor ON de.id_doctor = p_doctor.id
JOIN persona p_estudiante ON de.id_estudiante = p_estudiante.id
JOIN doctor d ON p_doctor.id = d.id_persona
ORDER BY p_doctor.nombre, p_estudiante.nombre;
```

## 📈 Estadísticas

### Total de registros por tabla
```sql
SELECT 
    'Personas' AS tabla, COUNT(*) AS cantidad FROM persona
UNION ALL
SELECT 'Estudiantes', COUNT(*) FROM estudiante
UNION ALL
SELECT 'Doctores', COUNT(*) FROM doctor
UNION ALL
SELECT 'Materias', COUNT(*) FROM materia
UNION ALL
SELECT 'Horarios', COUNT(*) FROM horario
UNION ALL
SELECT 'Inscripciones', COUNT(*) FROM estudiante_materia;
```

### Estudiantes por carrera
```sql
SELECT 
    e.carrera,
    COUNT(*) AS cantidad,
    AVG(e.semestre) AS semestre_promedio
FROM estudiante e
GROUP BY e.carrera
ORDER BY cantidad DESC;
```

### Doctores por especialidad
```sql
SELECT 
    d.especialidad,
    COUNT(*) AS cantidad,
    AVG(d.anios_experiencia) AS experiencia_promedio
FROM doctor d
GROUP BY d.especialidad
ORDER BY cantidad DESC;
```

## 🔧 Mantenimiento

### Limpiar datos de prueba
```sql
DELETE FROM registro_hospitalario;
DELETE FROM doctor_estudiante;
DELETE FROM estudiante_materia;
DELETE FROM horario;
DELETE FROM estudiante;
DELETE FROM doctor;
DELETE FROM materia;
DELETE FROM persona;
```

### Resetear secuencias
```sql
-- Reiniciar secuencias si es necesario
SELECT setval('horario_id_seq', (SELECT MAX(id) FROM horario));
SELECT setval('estudiante_materia_id_seq', (SELECT MAX(id) FROM estudiante_materia));
SELECT setval('doctor_estudiante_id_seq', (SELECT MAX(id) FROM doctor_estudiante));
SELECT setval('registro_hospitalario_id_seq', (SELECT MAX(id) FROM registro_hospitalario));
```

## 📝 Notas Importantes

1. **Integridad Referencial**: Todas las FK tienen ON DELETE CASCADE o RESTRICT
2. **Índices**: Creados automáticamente para PK y campos frecuentes
3. **Vistas**: Actualizadas automáticamente al cambiar datos
4. **Codificación**: UTF-8 para soportar caracteres especiales

---

**Hospital San Rafael**  
*PostgreSQL 15 | pgAdmin 4*
