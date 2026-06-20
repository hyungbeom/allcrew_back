-- 로컬 개발용 시드: 테스트 에이전시 + 직원 초대코드
-- admin@allcrew.com 계정은 LocalAuthDataInitializer에서 생성 (password123)

INSERT INTO admin_agencies (company_name, company_slug, business_number, address, address_detail)
VALUES ('올크루 테스트 에이전시', 'allcrew', '123-45-67890', '서울특별시 강남구', '101호');

INSERT INTO admin_invite_codes (agency_id, code, expires_at)
VALUES (1, 'CREW-INVITE-001', TIMESTAMP '2099-12-31 23:59:59');
