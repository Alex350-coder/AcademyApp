CREATE TABLE institutions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Insert a default institution to backfill existing records
INSERT INTO institutions (id, name, code, address, phone, email)
VALUES (gen_random_uuid(), 'Default Institution', 'DEFAULT-000', '', '', '')
RETURNING id;

-- Add columns as nullable first, backfill, then set NOT NULL
ALTER TABLE users ADD COLUMN institution_id UUID REFERENCES institutions(id);
ALTER TABLE courses ADD COLUMN institution_id UUID REFERENCES institutions(id);
ALTER TABLE classrooms ADD COLUMN institution_id UUID REFERENCES institutions(id);
ALTER TABLE academic_periods ADD COLUMN institution_id UUID REFERENCES institutions(id);

-- Backfill existing records with the default institution UUID
DO $$
DECLARE
    default_inst_id UUID;
BEGIN
    SELECT id INTO default_inst_id FROM institutions WHERE code = 'DEFAULT-000';
    UPDATE users SET institution_id = default_inst_id WHERE institution_id IS NULL;
    UPDATE courses SET institution_id = default_inst_id WHERE institution_id IS NULL;
    UPDATE classrooms SET institution_id = default_inst_id WHERE institution_id IS NULL;
    UPDATE academic_periods SET institution_id = default_inst_id WHERE institution_id IS NULL;
END $$;

-- Now enforce NOT NULL
ALTER TABLE users ALTER COLUMN institution_id SET NOT NULL;
ALTER TABLE courses ALTER COLUMN institution_id SET NOT NULL;
ALTER TABLE classrooms ALTER COLUMN institution_id SET NOT NULL;
ALTER TABLE academic_periods ALTER COLUMN institution_id SET NOT NULL;

CREATE INDEX idx_users_institution ON users(institution_id);
CREATE INDEX idx_courses_institution ON courses(institution_id);
CREATE INDEX idx_classrooms_institution ON classrooms(institution_id);
CREATE INDEX idx_academic_periods_institution ON academic_periods(institution_id);
