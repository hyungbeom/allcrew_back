-- 로컬 개발용 기본 관리자 계정 (H2 재시작 시마다 삽입, 이메일 중복은 스키마 UNIQUE로 방지)
INSERT INTO members (name, email, role)
VALUES ('관리자', 'admin@example.com', 'ADMIN');
