INSERT INTO public.app_user(username, password, role, enabled, created_at, updated_at)
VALUES ('${admin.username}', '${admin.password}', 'ROLE_ADMIN', true, NOW()::timestamp, NOW()::timestamp);

COMMIT;
