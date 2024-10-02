CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100),
                       email VARCHAR(100)
);

INSERT INTO users (name, email) VALUES
                                    ('John Doe', 'john@example.com'),
                                    ('Jane Doe', 'jane@example.com');
