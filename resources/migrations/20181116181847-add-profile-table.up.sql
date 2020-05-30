CREATE TABLE profile (
	id SERIAL PRIMARY KEY,
	user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE, 
	first_name TEXT,
	last_name TEXT,
	bio TEXT,
	profile_picture_id INT REFERENCES file (id),
	created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);

--;;
-- create a system account for which the stock entries will be created
INSERT INTO profile 
(user_id, bio) 
VALUES (1, 'I shall rule');
