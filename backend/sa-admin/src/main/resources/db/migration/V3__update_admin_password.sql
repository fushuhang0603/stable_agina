-- 管理员初始密码调整为 admin123（BCrypt 密文，幂等）
UPDATE sys_user
SET password = '$2a$10$87yQ3SK8/FYG6Y5LExuqb.ZqDP6cULvdVDRUz2u9Zb2EG/jdojG4q'
WHERE username = 'admin'
  AND deleted = 0;
