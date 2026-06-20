ALTER TABLE admin_chat_rooms
    ADD COLUMN IF NOT EXISTS crew_code VARCHAR(20);
