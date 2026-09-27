-- V3: Menu management table
CREATE TABLE IF NOT EXISTS menus (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 1,
    parent_id BIGINT,
    name VARCHAR(128) NOT NULL,
    path VARCHAR(256),
    icon VARCHAR(64),
    sort_no INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    permission_code VARCHAR(128),
    public_access BOOLEAN NOT NULL DEFAULT FALSE,
    public_token VARCHAR(128),
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp with time zone NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_menus_tenant ON menus(tenant_id);

-- V4: Alarm rule table for threshold-based auto alarm
CREATE TABLE IF NOT EXISTS alarm_rules (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 1,
    point_id BIGINT NOT NULL,
    rule_name VARCHAR(128) NOT NULL,
    condition VARCHAR(16) NOT NULL,
    threshold DECIMAL(20,6) NOT NULL,
    level VARCHAR(16) NOT NULL DEFAULT 'WARNING',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp with time zone NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_alarm_rules_point ON alarm_rules(point_id);
