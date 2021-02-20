CREATE TABLE users
(id SERIAL PRIMARY KEY,
 username TEXT NOT NULL,
 email TEXT UNIQUE,
 admin BOOLEAN NOT NULL DEFAULT FALSE,
 last_login TIMESTAMP,
 is_active BOOLEAN NOT NULL DEFAULT TRUE,
 is_checkedin BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
 updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
 password VARCHAR(300) NOT NULL);

--;;
-- create a system account for which the stock entries will be created
-- username: admin; password: admin 
INSERT INTO users (username, admin, is_active, is_checkedin, password) 
VALUES ('admin', TRUE, TRUE, FALSE, 'bcrypt+sha512$86186fc28f83b3e3db78bcf8350a3a57$12$8f215420e68fd7922561167b07354f05d8db6d49e212689e');
