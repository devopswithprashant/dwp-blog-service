CREATE TABLE blog_post (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    published_at TIMESTAMP
);

CREATE TABLE blog_post_content (
    post_id BIGINT PRIMARY KEY,
    content TEXT NOT NULL,
    format VARCHAR(20) NOT NULL DEFAULT 'MARKDOWN',
    CONSTRAINT fk_post_content
        FOREIGN KEY (post_id) REFERENCES blog_post(id)
        ON DELETE CASCADE
);

CREATE TABLE blog_post_version (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    version INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_version
        FOREIGN KEY (post_id) REFERENCES blog_post(id)
        ON DELETE CASCADE
);