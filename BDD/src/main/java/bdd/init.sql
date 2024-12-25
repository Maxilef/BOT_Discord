CREATE TABLE users_test_init (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100),
                       email VARCHAR(100)
);

INSERT INTO users_test_init (name, email) VALUES
                                    ('John Doe', 'john@example.com'),
                                    ('Jane Doe', 'jane@example.com');
