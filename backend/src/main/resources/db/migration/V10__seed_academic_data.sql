DO $$
DECLARE
    v_inst RECORD;
    v_idx INTEGER := 0;

    v_password VARCHAR(255) := '$2b$12$ydJhchCeDXyP0kRPeInIeu67omNCZRqc1rAvdFjK8g092e3Acg4d.';
    v_now TIMESTAMP WITH TIME ZONE := NOW();

    v_sec_role UUID := (SELECT id FROM roles WHERE name = 'SECRETARY');
    v_teach_role UUID := (SELECT id FROM roles WHERE name = 'TEACHER');
    v_stud_role UUID := (SELECT id FROM roles WHERE name = 'STUDENT');

    v_eval_type1 UUID := (SELECT id FROM evaluation_types WHERE name = 'Examen Parcial 1');
    v_eval_type2 UUID := (SELECT id FROM evaluation_types WHERE name = 'Examen Final');

    v_user_id UUID;
    v_teacher_id UUID;
    v_student_id UUID;
    v_section_id UUID;

    v_t INTEGER;
    v_s INTEGER;
    v_e INTEGER;

    v_courses_arr UUID[];
    v_classrooms_arr UUID[];

    v_teacher_ids UUID[];
    v_student_ids UUID[];
    v_eval_ids UUID[];

    v_teacher_names TEXT[30][2] := ARRAY[
        ARRAY['Jorge', 'Torres'],    ARRAY['María', 'García'],
        ARRAY['Carlos', 'Ramírez'],  ARRAY['Lucía', 'López'],
        ARRAY['Pedro', 'Fernández'], ARRAY['Rosa', 'Martínez'],
        ARRAY['Diego', 'Rodríguez'], ARRAY['Elena', 'Vargas'],
        ARRAY['Luis', 'Castillo'],   ARRAY['Sofía', 'Reyes'],
        ARRAY['Ricardo', 'Mendoza'], ARRAY['Patricia', 'Castro'],
        ARRAY['Fernando', 'Delgado'],ARRAY['Diana', 'Rivas'],
        ARRAY['Alberto', 'Herrera'], ARRAY['Gloria', 'Campos'],
        ARRAY['Raúl', 'Flores'],     ARRAY['Teresa', 'Morales'],
        ARRAY['Héctor', 'Acosta'],   ARRAY['Lorena', 'Paredes'],
        ARRAY['Marco', 'Zambrano'],  ARRAY['Adriana', 'Montoya'],
        ARRAY['Óscar', 'Peña'],      ARRAY['Claudia', 'Guerrero'],
        ARRAY['Javier', 'Soto'],     ARRAY['Verónica', 'Cáceres'],
        ARRAY['Pablo', 'Valdivia'],  ARRAY['Mónica', 'Ojeda'],
        ARRAY['César', 'Quispe'],    ARRAY['Ruth', 'Mamani']
    ];

    v_student_names TEXT[60][2] := ARRAY[
        ARRAY['Mateo', 'Quispe'],    ARRAY['Valentina', 'Huamán'],
        ARRAY['Sebastián', 'Cruz'],  ARRAY['Isabella', 'Mamani'],
        ARRAY['Nicolás', 'Chávez'],  ARRAY['Camila', 'Pérez'],
        ARRAY['Alejandro', 'Gutiérrez'], ARRAY['Luciana', 'Romero'],
        ARRAY['Santiago', 'Álvarez'],ARRAY['Gabriela', 'Morales'],
        ARRAY['Gabriel', 'Navarro'], ARRAY['Ximena', 'Vega'],
        ARRAY['Adrián', 'Ramos'],    ARRAY['Daniela', 'Ortiz'],
        ARRAY['Daniel', 'Silva'],    ARRAY['Andrea', 'Medina'],
        ARRAY['Fernando', 'Carrillo'],ARRAY['Samantha', 'Aguilar'],
        ARRAY['Andrés', 'Campos'],   ARRAY['Mariana', 'Pineda'],
        ARRAY['Diego', 'Tello'],     ARRAY['Valeria', 'Vidal'],
        ARRAY['Emilio', 'Barrios'],  ARRAY['Renata', 'Ortega'],
        ARRAY['Joaquín', 'Ríos'],    ARRAY['Antonella', 'Salazar'],
        ARRAY['Matías', 'Linares'],  ARRAY['Fabiana', 'Beltrán'],
        ARRAY['Benjamín', 'Durán'],  ARRAY['Alessandra', 'Villegas'],
        ARRAY['Leonardo', 'Escobar'],ARRAY['Micaela', 'Bustamante'],
        ARRAY['Maximiliano', 'Córdova'], ARRAY['María José', 'Zavala'],
        ARRAY['Thiago', 'Gallegos'], ARRAY['Bianca', 'Miranda'],
        ARRAY['Samuel', 'Amador'],   ARRAY['Julieta', 'Sierra'],
        ARRAY['Emmanuel', 'Coronel'],ARRAY['Alma', 'Méndez'],
        ARRAY['Ángel', 'Ponce'],     ARRAY['Regina', 'Trujillo'],
        ARRAY['Derek', 'Palacios'],  ARRAY['Aitana', 'Suárez'],
        ARRAY['Liam', 'Cabrera'],    ARRAY['Antonia', 'Roldán'],
        ARRAY['Luciano', 'Toledo'],  ARRAY['Ayelen', 'Farfán'],
        ARRAY['Ian', 'Zúñiga'],      ARRAY['Mía', 'Bermúdez'],
        ARRAY['Bruno', 'Maldonado'], ARRAY['Abril', 'Rosales'],
        ARRAY['Martín', 'Villar'],   ARRAY['Catalina', 'Fuentes'],
        ARRAY['Francisco', 'Cano'],  ARRAY['Emma', 'Rangel'],
        ARRAY['Tomás', 'Aguirre'],   ARRAY['Jimena', 'Rey'],
        ARRAY['Felipe', 'Carbajal'], ARRAY['Zoe', 'Palomino']
    ];

    v_secr_names TEXT[10][2] := ARRAY[
        ARRAY['Ana', 'Paredes'],    ARRAY['Carmen', 'Mendoza'],
        ARRAY['Laura', 'Castro'],   ARRAY['Patricia', 'Delgado'],
        ARRAY['Gabriela', 'Rivas'], ARRAY['Verónica', 'Herrera'],
        ARRAY['Mónica', 'Campos'],  ARRAY['Silvia', 'Flores'],
        ARRAY['Rosa', 'Morales'],   ARRAY['Isabel', 'Acosta']
    ];

    v_enrollment_uuid UUID;
    v_ev_uuid UUID;
    v_score NUMERIC(10,2);

BEGIN

    FOR v_idx IN 1..10 LOOP

        SELECT * INTO v_inst FROM institutions WHERE code != 'DEFAULT-000' ORDER BY code LIMIT 1 OFFSET v_idx - 1;

        v_courses_arr := ARRAY(SELECT id FROM courses WHERE institution_id = v_inst.id ORDER BY name);
        v_classrooms_arr := ARRAY(SELECT id FROM classrooms WHERE institution_id = v_inst.id ORDER BY name);

        v_teacher_ids := '{}';
        v_student_ids := '{}';

        -- ============ SECRETARY ============
        v_user_id := gen_random_uuid();
        INSERT INTO users (id, email, password_hash, first_name, last_name, phone, status, institution_id, created_at, updated_at)
        VALUES (v_user_id,
                'secretary.inst' || v_idx || '@academia.com',
                v_password,
                v_secr_names[v_idx][1],
                v_secr_names[v_idx][2],
                '999-666-' || LPAD(v_idx::TEXT, 3, '0'),
                'ACTIVE', v_inst.id, v_now, v_now)
        ON CONFLICT (email) DO NOTHING;

        INSERT INTO user_roles (user_id, role_id)
        SELECT v_user_id, v_sec_role
        WHERE NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = v_user_id AND role_id = v_sec_role);

        -- ============ TEACHERS (3 per institution) ============
        FOR v_t IN 1..3 LOOP
            v_user_id := gen_random_uuid();
            INSERT INTO users (id, email, password_hash, first_name, last_name, phone, status, institution_id, created_at, updated_at)
            VALUES (v_user_id,
                    'teacher.inst' || v_idx || '.' || v_t || '@academia.com',
                    v_password,
                    v_teacher_names[(v_idx - 1) * 3 + v_t][1],
                    v_teacher_names[(v_idx - 1) * 3 + v_t][2],
                    '999-555-' || LPAD(((v_idx - 1) * 3 + v_t)::TEXT, 3, '0'),
                    'ACTIVE', v_inst.id, v_now, v_now)
            ON CONFLICT (email) DO NOTHING;

            INSERT INTO user_roles (user_id, role_id)
            SELECT v_user_id, v_teach_role
            WHERE NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = v_user_id AND role_id = v_teach_role);

            v_teacher_id := gen_random_uuid();
            INSERT INTO teachers (id, user_id, specialty, hire_date, created_at, updated_at)
            VALUES (v_teacher_id, v_user_id,
                    CASE v_t
                        WHEN 1 THEN 'Matemáticas'
                        WHEN 2 THEN 'Comunicación'
                        WHEN 3 THEN 'Ciencias'
                    END,
                    '2024-03-01'::date, v_now, v_now);

            v_teacher_ids := v_teacher_ids || v_teacher_id;
        END LOOP;

        -- ============ STUDENTS (6 per institution) ============
        FOR v_s IN 1..6 LOOP
            v_user_id := gen_random_uuid();
            INSERT INTO users (id, email, password_hash, first_name, last_name, phone, status, institution_id, created_at, updated_at)
            VALUES (v_user_id,
                    'student.inst' || v_idx || '.' || v_s || '@academia.com',
                    v_password,
                    v_student_names[(v_idx - 1) * 6 + v_s][1],
                    v_student_names[(v_idx - 1) * 6 + v_s][2],
                    '999-444-' || LPAD(((v_idx - 1) * 6 + v_s)::TEXT, 3, '0'),
                    'ACTIVE', v_inst.id, v_now, v_now)
            ON CONFLICT (email) DO NOTHING;

            INSERT INTO user_roles (user_id, role_id)
            SELECT v_user_id, v_stud_role
            WHERE NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = v_user_id AND role_id = v_stud_role);

            v_student_id := gen_random_uuid();
            INSERT INTO students (id, user_id, enrollment_code, birth_date, guardian_name, guardian_contact, created_at, updated_at)
            VALUES (v_student_id, v_user_id,
                    'MAT-' || v_inst.code || '-' || LPAD(v_s::TEXT, 2, '0'),
                    ('2008-01-01'::date + (v_s * 30 || ' days')::INTERVAL)::date,
                    'Padre de ' || v_student_names[(v_idx - 1) * 6 + v_s][1],
                    '999-333-' || LPAD(((v_idx - 1) * 6 + v_s)::TEXT, 3, '0'),
                    v_now, v_now)
            ON CONFLICT (enrollment_code) DO NOTHING;

            v_student_ids := v_student_ids || v_student_id;
        END LOOP;

        -- ============ SECTIONS (3, one per course) ============
        FOR v_t IN 1..3 LOOP
            v_section_id := gen_random_uuid();
            INSERT INTO course_sections (id, course_id, academic_period_id, teacher_id, classroom_id, name, capacity, created_at, updated_at)
            VALUES (v_section_id,
                    v_courses_arr[v_t],
                    (SELECT id FROM academic_periods WHERE institution_id = v_inst.id LIMIT 1),
                    v_teacher_ids[v_t],
                    v_classrooms_arr[v_t],
                    v_teacher_names[(v_idx - 1) * 3 + v_t][1] || ' - ' || (SELECT name FROM courses WHERE id = v_courses_arr[v_t]),
                    30, v_now, v_now);

            -- Evaluations (2 per section, created ONCE)
            v_eval_ids := '{}';
            v_ev_uuid := gen_random_uuid();
            INSERT INTO evaluations (id, section_id, evaluation_type_id, name, date, max_score, created_at, updated_at)
            VALUES (v_ev_uuid, v_section_id, v_eval_type1,
                    (SELECT name FROM evaluation_types WHERE id = v_eval_type1),
                    '2025-05-15'::date, 20.00, v_now, v_now);
            v_eval_ids := v_eval_ids || v_ev_uuid;

            v_ev_uuid := gen_random_uuid();
            INSERT INTO evaluations (id, section_id, evaluation_type_id, name, date, max_score, created_at, updated_at)
            VALUES (v_ev_uuid, v_section_id, v_eval_type2,
                    (SELECT name FROM evaluation_types WHERE id = v_eval_type2),
                    '2025-07-15'::date, 20.00, v_now, v_now);
            v_eval_ids := v_eval_ids || v_ev_uuid;

            -- Enroll 2 students per section, create grades + attendances
            FOR v_s IN 1..2 LOOP
                v_enrollment_uuid := gen_random_uuid();
                INSERT INTO enrollments (id, student_id, section_id, status, enrolled_at, created_at, updated_at)
                VALUES (v_enrollment_uuid,
                        v_student_ids[(v_t - 1) * 2 + v_s],
                        v_section_id,
                        'ACTIVE', v_now, v_now, v_now)
                ON CONFLICT ON CONSTRAINT uq_enrollment_student_section DO NOTHING;

                -- Grade per evaluation
                FOREACH v_ev_uuid IN ARRAY v_eval_ids LOOP
                    v_score := round((10 + random() * 10)::numeric, 2);
                    INSERT INTO grades (id, evaluation_id, student_id, score, graded_by, graded_at, created_at, updated_at)
                    VALUES (gen_random_uuid(), v_ev_uuid,
                            v_student_ids[(v_t - 1) * 2 + v_s],
                            v_score,
                            (SELECT user_id FROM teachers WHERE id = v_teacher_ids[v_t]),
                            v_now, v_now, v_now);
                END LOOP;

                -- Attendances (3 distinct dates)
                INSERT INTO attendances (id, enrollment_id, date, status, created_at, updated_at)
                VALUES (gen_random_uuid(), v_enrollment_uuid, '2025-04-01'::date,
                        CASE floor(random() * 4)::int WHEN 0 THEN 'PRESENT' WHEN 1 THEN 'PRESENT' WHEN 2 THEN 'LATE' ELSE 'PRESENT' END,
                        v_now, v_now)
                ON CONFLICT (enrollment_id, date) DO NOTHING;

                INSERT INTO attendances (id, enrollment_id, date, status, created_at, updated_at)
                VALUES (gen_random_uuid(), v_enrollment_uuid, '2025-05-01'::date,
                        CASE floor(random() * 4)::int WHEN 0 THEN 'PRESENT' WHEN 1 THEN 'ABSENT' WHEN 2 THEN 'PRESENT' ELSE 'PRESENT' END,
                        v_now, v_now)
                ON CONFLICT (enrollment_id, date) DO NOTHING;

                INSERT INTO attendances (id, enrollment_id, date, status, created_at, updated_at)
                VALUES (gen_random_uuid(), v_enrollment_uuid, '2025-06-01'::date,
                        CASE floor(random() * 4)::int WHEN 0 THEN 'PRESENT' WHEN 1 THEN 'JUSTIFIED' WHEN 2 THEN 'ABSENT' ELSE 'PRESENT' END,
                        v_now, v_now)
                ON CONFLICT (enrollment_id, date) DO NOTHING;
            END LOOP;
        END LOOP;
    END LOOP;
END $$;
