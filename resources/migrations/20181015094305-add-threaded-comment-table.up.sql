CREATE TABLE threaded_comment (
  id SERIAL PRIMARY KEY,
  -- The comment author may be a registered user,
  author_id INT REFERENCES users (id) ON DELETE SET NULL,
  -- or he may not be registered or logged in, and use whatever name
  -- he wishes.
  author_name TEXT,
  title TEXT NOT NULL,
  body TEXT NOT NULL,
  parent INT REFERENCES threaded_comment (id) ON DELETE SET NULL,
  is_approved BOOLEAN NOT NULL DEFAULT FALSE,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
