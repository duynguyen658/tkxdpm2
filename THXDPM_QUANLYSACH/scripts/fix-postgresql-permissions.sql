-- Script SQL để cấp quyền cho PostgreSQL
-- Chạy script này với user postgres hoặc superuser
-- Thay đổi 'iot_user' thành username của bạn nếu khác

-- Kết nối với database quanlysach
\c quanlysach

-- Cấp quyền cho schema public
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
GRANT ALL ON SCHEMA public TO iot_user;

-- Cấp quyền tạo bảng
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO public;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO iot_user;

-- Nếu vẫn không được, thử cách này:
-- Revoke và grant lại quyền
REVOKE ALL ON SCHEMA public FROM public;
GRANT USAGE ON SCHEMA public TO postgres;
GRANT USAGE ON SCHEMA public TO iot_user;
GRANT CREATE ON SCHEMA public TO postgres;
GRANT CREATE ON SCHEMA public TO iot_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO iot_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO iot_user;

-- Đảm bảo user có quyền owner hoặc được cấp quyền
ALTER DATABASE quanlysach OWNER TO postgres;
-- Hoặc nếu muốn iot_user là owner:
-- ALTER DATABASE quanlysach OWNER TO iot_user;

