package user

import (
	"context"
	"database/sql"
	"errors"
	"gin-ddd/internal/domain/model/user"
)

// UserRepositoryImpl 用户仓储实现
type UserRepositoryImpl struct {
	db *sql.DB
}

// NewUserRepository 创建用户仓储实例
func NewUserRepository(db *sql.DB) *UserRepositoryImpl {
	return &UserRepositoryImpl{
		db: db,
	}
}

// Create 创建用户
func (r *UserRepositoryImpl) Create(ctx context.Context, u *user.User) error {
	query := `
		INSERT INTO users (username, email, password, status, created_at, updated_at)
		VALUES (?, ?, ?, ?, ?, ?)
	`
	result, err := r.db.ExecContext(ctx, query,
		u.Username, u.Email, u.Password, u.Status, u.CreatedAt, u.UpdatedAt)
	if err != nil {
		return err
	}

	id, err := result.LastInsertId()
	if err != nil {
		return err
	}
	u.ID = id
	return nil
}

// Update 更新用户
func (r *UserRepositoryImpl) Update(ctx context.Context, u *user.User) error {
	query := `
		UPDATE users
		SET username = ?, email = ?, password = ?, status = ?, updated_at = ?
		WHERE id = ?
	`
	_, err := r.db.ExecContext(ctx, query,
		u.Username, u.Email, u.Password, u.Status, u.UpdatedAt, u.ID)
	return err
}

// Delete 删除用户
func (r *UserRepositoryImpl) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM users WHERE id = ?`
	_, err := r.db.ExecContext(ctx, query, id)
	return err
}

// FindByID 根据ID查询用户
func (r *UserRepositoryImpl) FindByID(ctx context.Context, id int64) (*user.User, error) {
	query := `
		SELECT id, username, email, password, status, created_at, updated_at
		FROM users WHERE id = ?
	`
	var u user.User
	err := r.db.QueryRowContext(ctx, query, id).Scan(
		&u.ID, &u.Username, &u.Email, &u.Password, &u.Status, &u.CreatedAt, &u.UpdatedAt)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}
	return &u, nil
}

// FindByUsername 根据用户名查询用户
func (r *UserRepositoryImpl) FindByUsername(ctx context.Context, username string) (*user.User, error) {
	query := `
		SELECT id, username, email, password, status, created_at, updated_at
		FROM users WHERE username = ?
	`
	var u user.User
	err := r.db.QueryRowContext(ctx, query, username).Scan(
		&u.ID, &u.Username, &u.Email, &u.Password, &u.Status, &u.CreatedAt, &u.UpdatedAt)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}
	return &u, nil
}

// FindByEmail 根据邮箱查询用户
func (r *UserRepositoryImpl) FindByEmail(ctx context.Context, email string) (*user.User, error) {
	query := `
		SELECT id, username, email, password, status, created_at, updated_at
		FROM users WHERE email = ?
	`
	var u user.User
	err := r.db.QueryRowContext(ctx, query, email).Scan(
		&u.ID, &u.Username, &u.Email, &u.Password, &u.Status, &u.CreatedAt, &u.UpdatedAt)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}
	return &u, nil
}

// FindAll 查询所有用户
func (r *UserRepositoryImpl) FindAll(ctx context.Context) ([]*user.User, error) {
	query := `
		SELECT id, username, email, password, status, created_at, updated_at
		FROM users ORDER BY created_at DESC
	`
	rows, err := r.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []*user.User
	for rows.Next() {
		var u user.User
		if err := rows.Scan(&u.ID, &u.Username, &u.Email, &u.Password, &u.Status, &u.CreatedAt, &u.UpdatedAt); err != nil {
			return nil, err
		}
		users = append(users, &u)
	}
	return users, nil
}

// FindByStatus 根据状态查询用户
func (r *UserRepositoryImpl) FindByStatus(ctx context.Context, status user.UserStatus) ([]*user.User, error) {
	query := `
		SELECT id, username, email, password, status, created_at, updated_at
		FROM users WHERE status = ? ORDER BY created_at DESC
	`
	rows, err := r.db.QueryContext(ctx, query, status)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []*user.User
	for rows.Next() {
		var u user.User
		if err := rows.Scan(&u.ID, &u.Username, &u.Email, &u.Password, &u.Status, &u.CreatedAt, &u.UpdatedAt); err != nil {
			return nil, err
		}
		users = append(users, &u)
	}
	return users, nil
}
