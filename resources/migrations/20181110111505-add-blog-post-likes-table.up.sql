CREATE TABLE blog_post_likes (
	blog_post_id INTEGER REFERENCES blog_post (id) ON DELETE CASCADE,
	like_id INTEGER REFERENCES likes (id) ON DELETE CASCADE,
	PRIMARY KEY (blog_post_id, like_id)
);