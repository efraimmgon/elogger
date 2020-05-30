CREATE TABLE tag_page (
	tag_id INTEGER REFERENCES tag (id) ON DELETE CASCADE,
	page_id INTEGER REFERENCES page (id) ON DELETE CASCADE,
	created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
	PRIMARY KEY (tag_id, page_id)
);