#  Hospital San Rafael - Sistema de Gestión Académica

Sistema integral de gestión para estudiantes y doctores del Hospital San Rafael, desarrollado con **JavaFX** y conectado a base de datos **PostgreSQL**.

![Java](https://img.shields.io/badge/Java-17-blue?style=flat-square&logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-17-green?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13+-blue?style=flat-square&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.6+-red?style=flat-square&logo=apache-maven)

##  Descripción

![Vista del sistema](imagenes_readme/img.png)

Este sistema permite:
-  **Gestión de Estudiantes**: Registro, edición, eliminación y consulta
-  **Gestión de Doctores**: Control de personal médico y especialidades
-  **Catálogo de Materias**: Administración del plan de estudios
-  **Consulta de Horarios**: Visualización de horarios por estudiante
-  **Control de Registros**: Bitácora de ingresos al hospital
-  **Conexión a PostgreSQL**: Persistencia real de datos en base de datos

##  Requisitos Previos

| Recurso | Versión Mínima | Descarga |
|---------|---------------|----------|
| Java JDK | 17+ | [Descargar](https://adoptium.net/) |
| Maven | 3.6+ | [Descargar](https://maven.apache.org/) |
| PostgreSQL | 13+ | [Descargar](https://www.postgresql.org/) |
| pgAdmin 4 | Cualquier versión | Incluido con PostgreSQL |

##  Instalación

### 1. Clonar o descargar el proyecto
```bash
cd "C:\Users\danie\Documents\Proyectos\U\Proyecto Integrador 3er\HospitalSanRafael"
```

### 2. Configurar la base de datos

#### a) Crear la base de datos en PostgreSQL
Abre pgAdmin 4 y ejecuta:
```sql
CREATE DATABASE hospital_san_rafael WITH OWNER = postgres ENCODING = 'UTF8';
```

#### b) Ejecutar el script de creación
1. Abre el archivo `database/script.sql`
2. Copia todo el contenido
3. En pgAdmin, ve a `hospital_san_rafael` → Query Tool
4. Pega y ejecuta (F5)

#### c) Configurar la conexión
Edita `src/main/resources/database.properties`:
```properties
db.url=jdbc:postgresql://localhost:5433/hospital_san_rafael
db.username=postgres
db.password=admin  # Tu contraseña real
db.driver=org.postgresql.Driver
```

### 3. Compilar el proyecto
```bash
mvn clean compile
```

## ️ Ejecución

```bash
mvn clean compile javafx:run
```

O usa el script incluido:
- **Windows**: `run.bat`
- **Linux/Mac**: `./run.sh`

##  Uso Básico

### Vista Principal

![Vista principal del sistema](imagenes_readme/img_1.png)

### Registrar un Estudiante
1. Ve a **Gestión de Estudiantes**
2. Click en **Nuevo**
3. Llena el formulario:
   - **ID**: Código único (ej: `E001`)
   - **Nombre/Apellido**: Datos personales
   - **Fecha Nacimiento**: Formato `AAAA-MM-DD` (ej: `2000-01-15`)
   - **Carrera**: Programa académico
   - **Semestre**: Número (1-10)
   - **Turno**: `Mañana`, `Tarde` o `Noche` (exactamente así)
4. Click en **Guardar**

### Interfaz de cambios o dashboard

![Interfaz de cambios o dashboard](imagenes_readme/img_2.png)

Esta vista muestra el panel principal con las estadísticas y controles del sistema.




## ️ Estructura del Proyecto

```
HospitalSanRafael/
├── database/
│   ├── script.sql                 # Script de creación de BD
│   └── database.properties        # Configuración de conexión
├── src/main/java/com/hospital/sanrafael/
│   ├── Main.java                  # Punto de entrada
│   ├── controller/                # Controladores JavaFX
│   ├── model/                     # Modelos de datos
│   ├── service/                   # Lógica de negocio
│   ├── dao/                       # Acceso a datos (PostgreSQL)
│   └── view/                      # Fábricas de vistas
├── src/main/resources/css/
│   └── styles.css                 # Estilos de la interfaz
├── pom.xml                        # Dependencias Maven
└── README.md                      # Este archivo
```

## ️ Tecnologías

| Categoría | Tecnología |
|----------|-----------|
| **Lenguaje** | Java 17 |
| **UI Framework** | JavaFX 17 |
| **Base de Datos** | PostgreSQL 13+ |
| **Gestor BD** | pgAdmin 4 |
| **Build Tool** | Maven 3.6+ |
| **JDBC Driver** | PostgreSQL JDBC 42.6.0 |

##  Modelo de Datos

### Entidades Principales
- **Persona**: Clase base (Estudiante, Doctor)
- **Estudiante**: Información académica
- **Doctor**: Especialidad y área asignada
- **Materia**: Catálogo académico
- **Horario**: Distribución temporal
- **RegistroHospitalario**: Control de accesos

##  Características Destacadas

-  **Conexión Automática**: Detección y reconexión automática a PostgreSQL
-  **Manejo de Errores**: Mensajes descriptivos y logs en consola
-  **Transacciones**: Guardado atómico (todo o nada)
-  **Conversión de Fechas**: Acepta múltiples formatos
-  **Interfaz Moderna**: Diseño con gradientes y efectos visuales
-  **Datos de Ejemplo**: Incluye población inicial de prueba

##  Soporte

Para reportar errores o sugerencias, revisa la documentación en:
- `database/README.md` - Guía de base de datos
- `database/INSTALACION_POSTGRESQL.md` - Instalación de PostgreSQL
- `GUIA_USO.md` - Manual de usuario completo

##  Autor
Daniel Felipe Rodriguez Osorio,
Daniel Ramiro Galeano Tellez,
Andres Rojas Borja,
Juan Sebastian Rodriguez Daza
Desarrollado para el **Hospital San Rafael**  
Versión: 1.0.0  
Última actualización: 2026

---

**Hospital San Rafael** - *Comprometidos con la excelencia académica y médica*
