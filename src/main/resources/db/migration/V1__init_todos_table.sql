-- Create todos table
CREATE TABLE IF NOT EXISTS todos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN NOT NULL DEFAULT false,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    due_date TIMESTAMP,
    tags VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on completed status for faster queries
CREATE INDEX IF NOT EXISTS idx_todos_completed ON todos(completed);

-- Create index on priority for filtering
CREATE INDEX IF NOT EXISTS idx_todos_priority ON todos(priority);

-- Create index on due_date for sorting and filtering
CREATE INDEX IF NOT EXISTS idx_todos_due_date ON todos(due_date);