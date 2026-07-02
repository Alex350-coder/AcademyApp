DO $$
DECLARE
    v_password_hash VARCHAR(255) := '$2b$12$ydJhchCeDXyP0kRPeInIeu67omNCZRqc1rAvdFjK8g092e3Acg4d.';
    v_i INTEGER;
    v_j INTEGER;
    v_k INTEGER;
    v_inst_id UUID;
    v_dir_id UUID;
    v_role_director_id UUID;
    v_role_teacher_id UUID;
    v_role_student_id UUID;
    v_course_id UUID;
    v_classroom_id UUID;
    v_period_id UUID;
    v_now TIMESTAMP WITH TIME ZONE := NOW();
BEGIN
    SELECT id INTO v_role_director_id FROM roles WHERE name = 'DIRECTOR';
    SELECT id INTO v_role_teacher_id FROM roles WHERE name = 'TEACHER';
    SELECT id INTO v_role_student_id FROM roles WHERE name = 'STUDENT';

    FOR v_i IN 1..10 LOOP
        v_inst_id := gen_random_uuid();
        INSERT INTO institutions (id, name, code, address, phone, email, is_active, created_at, updated_at)
        VALUES (v_inst_id,
                CASE v_i
                    WHEN 1 THEN 'Colegio San Martín'
                    WHEN 2 THEN 'I.E. Nuestra Señora del Rosario'
                    WHEN 3 THEN 'Colegio Alexander von Humboldt'
                    WHEN 4 THEN 'I.E. Santa María de la Merced'
                    WHEN 5 THEN 'Colegio Sagrados Corazones'
                    WHEN 6 THEN 'I.E. San Ignacio de Loyola'
                    WHEN 7 THEN 'Colegio La Recoleta'
                    WHEN 8 THEN 'I.E. Santo Domingo de Guzmán'
                    WHEN 9 THEN 'Colegio San José de Cluny'
                    WHEN 10 THEN 'I.E. Virgen del Carmen'
                END,
                CASE v_i
                    WHEN 1 THEN 'CSM-001' WHEN 2 THEN 'ROS-002'
                    WHEN 3 THEN 'AVH-003' WHEN 4 THEN 'MER-004'
                    WHEN 5 THEN 'SSC-005' WHEN 6 THEN 'SIL-006'
                    WHEN 7 THEN 'REC-007' WHEN 8 THEN 'SDG-008'
                    WHEN 9 THEN 'SJC-009' WHEN 10 THEN 'CAR-010'
                END,
                CASE v_i
                    WHEN 1 THEN 'Av. Los Olivos 150, Lima'
                    WHEN 2 THEN 'Jr. La Merced 320, Arequipa'
                    WHEN 3 THEN 'Av. La Marina 1050, Lima'
                    WHEN 4 THEN 'Calle Real 450, Huancayo'
                    WHEN 5 THEN 'Av. Benavides 850, Lima'
                    WHEN 6 THEN 'Jr. Junín 280, Cusco'
                    WHEN 7 THEN 'Av. Argentina 670, Trujillo'
                    WHEN 8 THEN 'Calle Comercio 155, Iquitos'
                    WHEN 9 THEN 'Av. Los Maestros 420, Chiclayo'
                    WHEN 10 THEN 'Jr. Amazonas 580, Piura'
                END,
                '999-888-' || LPAD(v_i::TEXT, 3, '0'),
                'info@inst' || v_i || '.edu',
                true, v_now, v_now);

        v_dir_id := gen_random_uuid();
        INSERT INTO users (id, email, password_hash, first_name, last_name, phone, status, institution_id, created_at, updated_at)
        VALUES (v_dir_id,
                'director.inst' || v_i || '@academia.com',
                v_password_hash,
                CASE v_i
                    WHEN 1 THEN 'Carlos' WHEN 2 THEN 'María'
                    WHEN 3 THEN 'Hans' WHEN 4 THEN 'Luis'
                    WHEN 5 THEN 'Ana' WHEN 6 THEN 'José'
                    WHEN 7 THEN 'Rosa' WHEN 8 THEN 'Pedro'
                    WHEN 9 THEN 'Sofía' WHEN 10 THEN 'Miguel'
                END,
                CASE v_i
                    WHEN 1 THEN 'López' WHEN 2 THEN 'García'
                    WHEN 3 THEN 'Müller' WHEN 4 THEN 'Rodríguez'
                    WHEN 5 THEN 'Martínez' WHEN 6 THEN 'Vargas'
                    WHEN 7 THEN 'Castillo' WHEN 8 THEN 'Sánchez'
                    WHEN 9 THEN 'Torres' WHEN 10 THEN 'Reyes'
                END,
                '999-777-' || LPAD(v_i::TEXT, 3, '0'),
                'ACTIVE', v_inst_id, v_now, v_now)
        ON CONFLICT (email) DO NOTHING;

        INSERT INTO user_roles (user_id, role_id)
        SELECT v_dir_id, v_role_director_id
        WHERE NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = v_dir_id AND role_id = v_role_director_id);

        v_period_id := gen_random_uuid();
        INSERT INTO academic_periods (id, name, start_date, end_date, status, institution_id, created_at, updated_at)
        VALUES (v_period_id, '2025-I', '2025-03-01'::date, '2025-07-31'::date, 'ACTIVE', v_inst_id, v_now, v_now);

        FOR v_j IN 1..3 LOOP
            v_course_id := gen_random_uuid();
            INSERT INTO courses (id, name, code, description, credits, institution_id, created_at, updated_at)
            VALUES (v_course_id,
                    CASE (v_i * 10 + v_j)
                        WHEN 11 THEN 'Matemáticas 1°'
                        WHEN 12 THEN 'Lenguaje 1°'
                        WHEN 13 THEN 'Ciencias 1°'
                        WHEN 21 THEN 'Álgebra'
                        WHEN 22 THEN 'Comunicación'
                        WHEN 23 THEN 'Biología'
                        WHEN 31 THEN 'Matemáticas Avanzadas'
                        WHEN 32 THEN 'Literatura'
                        WHEN 33 THEN 'Química'
                        WHEN 41 THEN 'Razonamiento Matemático'
                        WHEN 42 THEN 'Redacción'
                        WHEN 43 THEN 'Física'
                        WHEN 51 THEN 'Geometría'
                        WHEN 52 THEN 'Gramática'
                        WHEN 53 THEN 'Ciencia y Ambiente'
                        WHEN 61 THEN 'Aritmética'
                        WHEN 62 THEN 'Lectura Crítica'
                        WHEN 63 THEN 'Ecología'
                        WHEN 71 THEN 'Trigonometría'
                        WHEN 72 THEN 'Lingüística'
                        WHEN 73 THEN 'Anatomía'
                        WHEN 81 THEN 'Estadística'
                        WHEN 82 THEN 'Ortografía'
                        WHEN 83 THEN 'Astronomía'
                        WHEN 91 THEN 'Lógica Matemática'
                        WHEN 92 THEN 'Análisis Literario'
                        WHEN 93 THEN 'Bioquímica'
                        WHEN 101 THEN 'Matemática Básica'
                        WHEN 102 THEN 'Expresión Oral'
                        WHEN 103 THEN 'Geografía Física'
                    END,
                    CASE (v_i * 10 + v_j)
                        WHEN 11 THEN 'MAT-001' WHEN 12 THEN 'LEN-001' WHEN 13 THEN 'CIE-001'
                        WHEN 21 THEN 'ALG-002' WHEN 22 THEN 'COM-002' WHEN 23 THEN 'BIO-002'
                        WHEN 31 THEN 'MAV-003' WHEN 32 THEN 'LIT-003' WHEN 33 THEN 'QUI-003'
                        WHEN 41 THEN 'RMM-004' WHEN 42 THEN 'RED-004' WHEN 43 THEN 'FIS-004'
                        WHEN 51 THEN 'GEO-005' WHEN 52 THEN 'GRA-005' WHEN 53 THEN 'CYA-005'
                        WHEN 61 THEN 'ARI-006' WHEN 62 THEN 'LEC-006' WHEN 63 THEN 'ECO-006'
                        WHEN 71 THEN 'TRI-007' WHEN 72 THEN 'LIN-007' WHEN 73 THEN 'ANA-007'
                        WHEN 81 THEN 'EST-008' WHEN 82 THEN 'ORT-008' WHEN 83 THEN 'AST-008'
                        WHEN 91 THEN 'LMA-009' WHEN 92 THEN 'ALI-009' WHEN 93 THEN 'BQC-009'
                        WHEN 101 THEN 'MBA-010' WHEN 102 THEN 'EXO-010' WHEN 103 THEN 'GEF-010'
                    END,
                    'Curso del colegio',
                    4, v_inst_id, v_now, v_now)
            ON CONFLICT (code) DO NOTHING;

            v_classroom_id := gen_random_uuid();
            INSERT INTO classrooms (id, name, code, capacity, location, institution_id, created_at, updated_at)
            VALUES (v_classroom_id,
                    CASE v_j WHEN 1 THEN 'Aula A' WHEN 2 THEN 'Aula B' ELSE 'Aula C' END,
                    'AULA-' || v_i || '-' || CASE v_j WHEN 1 THEN 'A' WHEN 2 THEN 'B' ELSE 'C' END,
                    30,
                    'Piso ' || v_i || ' - Aula ' || CASE v_j WHEN 1 THEN 'A' WHEN 2 THEN 'B' ELSE 'C' END,
                    v_inst_id, v_now, v_now)
            ON CONFLICT (code) DO NOTHING;
        END LOOP;
    END LOOP;
END $$;
