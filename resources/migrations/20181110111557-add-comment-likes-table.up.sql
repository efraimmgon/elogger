CREATE TABLE comment_likes (
	comment_id INTEGER REFERENCES threaded_comment (id) ON DELETE CASCADE,
	like_id INTEGER REFERENCES likes (id) ON DELETE CASCADE,
	PRIMARY KEY (comment_id, like_id)
);