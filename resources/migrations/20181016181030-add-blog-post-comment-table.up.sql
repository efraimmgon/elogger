CREATE TABLE blog_post_comment (
	blog_post_id INT REFERENCES blog_post (id) ON DELETE CASCADE,
	comment_id INT REFERENCES threaded_comment (id) ON DELETE CASCADE,
	PRIMARY KEY (blog_post_id, comment_id)
);