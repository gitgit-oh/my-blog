-- 默认管理员账号密码: admin / admin123
-- 密码哈希需通过 BCryptPasswordEncoder 生成，请运行 GenHashMain 获取正确的哈希值
-- 生成哈希后，执行: UPDATE admin SET password_hash = '<生成的哈希>' WHERE username = 'admin';
INSERT IGNORE INTO admin (id, username, password_hash) VALUES (1, 'admin', 'REPLACE_WITH_GENERATED_HASH');
