CREATE TABLE tag_post (
	tag_id INTEGER REFERENCES tag (id) ON DELETE CASCADE,
	blog_post_id INTEGER REFERENCES blog_post (id) ON DELETE CASCADE,
	created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	PRIMARY KEY (tag_id, blog_post_id)
);