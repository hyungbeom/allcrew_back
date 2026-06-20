-- PostgreSQL 등 기존 DB에 프로젝트 테이블 추가 (1회 실행)

CREATE TABLE IF NOT EXISTS admin_projects (
    id                            BIGSERIAL PRIMARY KEY,
    agency_id                     BIGINT       NOT NULL REFERENCES admin_agencies (id),
    project_code                  VARCHAR(20)  NOT NULL UNIQUE,
    name                          VARCHAR(200) NOT NULL,
    event_type                    VARCHAR(50)  NOT NULL,
    description                   TEXT,
    cover_image_url               VARCHAR(500),
    start_date                    DATE         NOT NULL,
    end_date                      DATE         NOT NULL,
    work_start_time               TIME         NOT NULL,
    work_end_time                 TIME         NOT NULL,
    address                       VARCHAR(500) NOT NULL,
    address_detail                VARCHAR(200),
    gps_radius                    INT          NOT NULL DEFAULT 100,
    break_minutes                 INT          NOT NULL DEFAULT 60,
    welfare                       VARCHAR(200),
    recruitment_deadline          DATE         NOT NULL,
    preferred_qualifications      TEXT,
    start_recruitment_immediately BOOLEAN      NOT NULL DEFAULT TRUE,
    status                        VARCHAR(20)  NOT NULL DEFAULT 'RECRUITING',
    crew_current                  INT          NOT NULL DEFAULT 0,
    crew_total                    INT          NOT NULL DEFAULT 0,
    budget                        BIGINT       NOT NULL DEFAULT 0,
    progress                      INT          NOT NULL DEFAULT 0,
    created_by_member_id          BIGINT       NOT NULL REFERENCES admin_members (id),
    created_at                    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT admin_projects_status_check
        CHECK (status IN ('RECRUITING', 'IN_PROGRESS', 'COMPLETED'))
);

CREATE INDEX IF NOT EXISTS idx_admin_projects_agency_id ON admin_projects (agency_id);

CREATE TABLE IF NOT EXISTS admin_project_positions (
    id          BIGSERIAL PRIMARY KEY,
    project_id  BIGINT       NOT NULL REFERENCES admin_projects (id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    headcount   INT          NOT NULL,
    pay_type    VARCHAR(10)  NOT NULL,
    amount      INT          NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT admin_project_positions_pay_type_check
        CHECK (pay_type IN ('hourly', 'daily'))
);

CREATE INDEX IF NOT EXISTS idx_admin_project_positions_project_id ON admin_project_positions (project_id);
