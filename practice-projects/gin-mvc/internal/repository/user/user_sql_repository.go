package user

import (
	"context"
	"database/sql"
	"errors"
	"fmt"

	"gin-mvc/internal/models/user"
	dbutil "gin-mvc/internal/repository/db"
)

type SQLRepository struct {
	db     *sql.DB
	driver string
}

func NewSQLRepository(db *sql.DB, driver string) *SQLRepository {
	return &SQLRepository{db: db, driver: driver}
}

func (r *SQLRepository) Create(ctx context.Context, u *user.User) error {
	if r.driver == "postgres" {
		query := fmt.Sprintf("INSERT INTO users (name, email, phone, created_time, updated_time) VALUES (%s, %s, %s, %s, %s) RETURNING id",
			dbutil.Placeholder(r.driver, 1), dbutil.Placeholder(r.driver, 2), dbutil.Placeholder(r.driver, 3), dbutil.Placeholder(r.driver, 4), dbutil.Placeholder(r.driver, 5))
		return r.db.QueryRowContext(ctx, query, u.Name, u.Email, u.Phone, u.CreatedTime, u.UpdatedTime).Scan(&u.ID)
	}

	query := "INSERT INTO users (name, email, phone, created_time, updated_time) VALUES (?, ?, ?, ?, ?)"
	res, err := r.db.ExecContext(ctx, query, u.Name, u.Email, u.Phone, u.CreatedTime, u.UpdatedTime)
	if err != nil {
		return err
	}
	id, err := res.LastInsertId()
	if err != nil {
		return err
	}
	u.ID = id
	return nil
}

func (r *SQLRepository) Update(ctx context.Context, u *user.User) error {
	query := fmt.Sprintf("UPDATE users SET name = %s, email = %s, phone = %s, updated_time = %s WHERE id = %s",
		dbutil.Placeholder(r.driver, 1), dbutil.Placeholder(r.driver, 2), dbutil.Placeholder(r.driver, 3), dbutil.Placeholder(r.driver, 4), dbutil.Placeholder(r.driver, 5))
	_, err := r.db.ExecContext(ctx, query, u.Name, u.Email, u.Phone, u.UpdatedTime, u.ID)
	return err
}

func (r *SQLRepository) Delete(ctx context.Context, id int64) error {
	query := fmt.Sprintf("DELETE FROM users WHERE id = %s", dbutil.Placeholder(r.driver, 1))
	_, err := r.db.ExecContext(ctx, query, id)
	return err
}

func (r *SQLRepository) FindByID(ctx context.Context, id int64) (*user.User, error) {
	query := fmt.Sprintf("SELECT id, name, email, phone, created_time, updated_time FROM users WHERE id = %s", dbutil.Placeholder(r.driver, 1))
	var u user.User
	err := r.db.QueryRowContext(ctx, query, id).Scan(&u.ID, &u.Name, &u.Email, &u.Phone, &u.CreatedTime, &u.UpdatedTime)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}
	return &u, nil
}

func (r *SQLRepository) FindByName(ctx context.Context, name string) (*user.User, error) {
	query := fmt.Sprintf("SELECT id, name, email, phone, created_time, updated_time FROM users WHERE name = %s", dbutil.Placeholder(r.driver, 1))
	var u user.User
	err := r.db.QueryRowContext(ctx, query, name).Scan(&u.ID, &u.Name, &u.Email, &u.Phone, &u.CreatedTime, &u.UpdatedTime)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}
	return &u, nil
}

func (r *SQLRepository) FindByEmail(ctx context.Context, email string) (*user.User, error) {
	query := fmt.Sprintf("SELECT id, name, email, phone, created_time, updated_time FROM users WHERE email = %s", dbutil.Placeholder(r.driver, 1))
	var u user.User
	err := r.db.QueryRowContext(ctx, query, email).Scan(&u.ID, &u.Name, &u.Email, &u.Phone, &u.CreatedTime, &u.UpdatedTime)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}
	return &u, nil
}

func (r *SQLRepository) FindAll(ctx context.Context) ([]*user.User, error) {
	query := "SELECT id, name, email, phone, created_time, updated_time FROM users ORDER BY created_time DESC"
	rows, err := r.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	users := make([]*user.User, 0)
	for rows.Next() {
		var u user.User
		if err := rows.Scan(&u.ID, &u.Name, &u.Email, &u.Phone, &u.CreatedTime, &u.UpdatedTime); err != nil {
			return nil, err
		}
		users = append(users, &u)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return users, nil
}
