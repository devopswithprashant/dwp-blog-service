ALTER TABLE blog_post
    ALTER COLUMN author_id DROP NOT NULL;

ALTER TABLE blog_post
    ADD COLUMN author_uuid UUID;
