CREATE TABLE office_hours (
	id SERIAL PRIMARY KEY,
	user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
	status TEXT NOT NULL,
	user_agent TEXT,
	lat REAL,
	lng REAL,
	created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);