CREATE TABLE notes (
    id STRING(36) NOT NULL,
    title STRING(MAX) NOT NULL,
    content STRING(MAX) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
) PRIMARY KEY (id);
