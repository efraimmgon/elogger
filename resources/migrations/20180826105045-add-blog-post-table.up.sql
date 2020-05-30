CREATE TABLE blog_post (
	id SERIAL PRIMARY KEY,
	title TEXT NOT NULL,
	subtitle TEXT,
	status TEXT NOT NULL,
	content TEXT,
	author_id INTEGER REFERENCES users (id) ON DELETE SET NULL,
	featured_img_id INTEGER REFERENCES file (id) ON DELETE SET NULL,
	created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	published_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	expires_at TIMESTAMP
);