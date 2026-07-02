INSERT INTO roles (id, name, description) VALUES
    (gen_random_uuid(), 'DIRECTOR', 'Director académico — acceso completo al sistema'),
    (gen_random_uuid(), 'SECRETARY', 'Secretaria — gestión de matrículas y documentos'),
    (gen_random_uuid(), 'TEACHER', 'Profesor — gestión de cursos y notas'),
    (gen_random_uuid(), 'STUDENT', 'Alumno — consulta de notas y horarios');

INSERT INTO permissions (id, code, description) VALUES
    (gen_random_uuid(), 'USER_CREATE', 'Crear usuarios'),
    (gen_random_uuid(), 'USER_READ', 'Consultar usuarios'),
    (gen_random_uuid(), 'USER_UPDATE', 'Actualizar usuarios'),
    (gen_random_uuid(), 'USER_DELETE', 'Eliminar usuarios'),
    (gen_random_uuid(), 'COURSE_CREATE', 'Crear cursos'),
    (gen_random_uuid(), 'COURSE_READ', 'Consultar cursos'),
    (gen_random_uuid(), 'COURSE_UPDATE', 'Actualizar cursos'),
    (gen_random_uuid(), 'COURSE_DELETE', 'Eliminar cursos'),
    (gen_random_uuid(), 'ENROLLMENT_CREATE', 'Crear matrículas'),
    (gen_random_uuid(), 'ENROLLMENT_READ', 'Consultar matrículas'),
    (gen_random_uuid(), 'ENROLLMENT_UPDATE', 'Actualizar matrículas'),
    (gen_random_uuid(), 'GRADE_CREATE', 'Registrar notas'),
    (gen_random_uuid(), 'GRADE_READ', 'Consultar notas'),
    (gen_random_uuid(), 'GRADE_UPDATE', 'Actualizar notas'),
    (gen_random_uuid(), 'ATTENDANCE_CREATE', 'Registrar asistencias'),
    (gen_random_uuid(), 'ATTENDANCE_READ', 'Consultar asistencias'),
    (gen_random_uuid(), 'REPORT_READ', 'Generar reportes'),
    (gen_random_uuid(), 'SETTINGS_UPDATE', 'Actualizar configuración del sistema');

DO $$
DECLARE
    v_dir_id UUID;
    v_sec_id UUID;
    v_tea_id UUID;
    v_stu_id UUID;
BEGIN
    SELECT id INTO v_dir_id FROM roles WHERE name = 'DIRECTOR';
    SELECT id INTO v_sec_id FROM roles WHERE name = 'SECRETARY';
    SELECT id INTO v_tea_id FROM roles WHERE name = 'TEACHER';
    SELECT id INTO v_stu_id FROM roles WHERE name = 'STUDENT';

    INSERT INTO role_permissions (role_id, permission_id)
    SELECT v_dir_id, id FROM permissions;

    INSERT INTO role_permissions (role_id, permission_id)
    SELECT v_sec_id, id FROM permissions
    WHERE code IN ('USER_CREATE', 'USER_READ', 'USER_UPDATE', 'COURSE_READ',
                   'ENROLLMENT_CREATE', 'ENROLLMENT_READ', 'ENROLLMENT_UPDATE',
                   'ATTENDANCE_READ', 'GRADE_READ');

    INSERT INTO role_permissions (role_id, permission_id)
    SELECT v_tea_id, id FROM permissions
    WHERE code IN ('USER_READ', 'COURSE_READ', 'GRADE_CREATE', 'GRADE_READ', 'GRADE_UPDATE',
                   'ATTENDANCE_CREATE', 'ATTENDANCE_READ');

    INSERT INTO role_permissions (role_id, permission_id)
    SELECT v_stu_id, id FROM permissions
    WHERE code IN ('COURSE_READ', 'GRADE_READ', 'ATTENDANCE_READ');
END $$;
