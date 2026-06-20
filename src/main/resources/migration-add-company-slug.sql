-- PostgreSQL 등 기존 DB에 company_slug 컬럼 추가 (1회 실행)
ALTER TABLE admin_agencies
    ADD COLUMN IF NOT EXISTS company_slug VARCHAR(50);

UPDATE admin_agencies
SET company_slug = 'allcrew'
WHERE company_slug IS NULL;

ALTER TABLE admin_agencies
    ALTER COLUMN company_slug SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_admin_agencies_company_slug
    ON admin_agencies (company_slug);
