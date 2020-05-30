CREATE TABLE tag (
	id SERIAL PRIMARY KEY,
 	title TEXT NOT NULL,
	description TEXT,
	created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);