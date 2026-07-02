CREATE TABLE report_snapshots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(100) NOT NULL,
    generated_by UUID REFERENCES users(id),
    parameters JSONB,
    file_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_report_snapshots_type ON report_snapshots(type);
CREATE INDEX idx_report_snapshots_created ON report_snapshots(created_at);
