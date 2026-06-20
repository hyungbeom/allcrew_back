-- admin_crew_members.phone 은 숫자 11자리만 저장 (VARCHAR(11))
UPDATE admin_crew_members
SET phone = REGEXP_REPLACE(phone, '[^0-9]', '', 'g')
WHERE phone ~ '[^0-9]';
