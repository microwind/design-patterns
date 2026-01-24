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
		INSERT INTO users (name, email, phone, created_time, updated_time)
		VALUES (?, ?, ?, ?, ?)
	`
	result, err := r.db.ExecContext(ctx, query,
		u.Name, u.Email, u.Phone, u.CreatedTime, u.UpdatedTime)
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
		SET name = ?, email = ?, phone = ?, updated_time = ?
		WHERE id = ?
	`
	_, err := r.db.ExecContext(ctx, query,
		u.Name, u.Email, u.Phone, u.UpdatedTime, u.ID)
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
		SELECT id, name, email, phone, created_time, updated_time
		FROM users WHERE id = ?
	`
	var u user.User
	err := r.db.QueryRowContext(ctx, query, id).Scan(
		&u.ID, &u.Name, &u.Email, &u.Phone, &u.CreatedTime, &u.UpdatedTime)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}
	return &u, nil
}

// FindByName 根据用户名查询用户
func (r *UserRepositoryImpl) FindByName(ctx context.Context, name string) (*user.User, error) {
	query := `
		SELECT id, name, email, phone, created_time, updated_time
		FROM users WHERE name = ?
	`
	var u user.User
	err := r.db.QueryRowContext(ctx, query, name).Scan(
		&u.ID, &u.Name, &u.Email, &u.Phone, &u.CreatedTime, &u.UpdatedTime)
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
		SELECT id, name, email, phone, created_time, updated_time
		FROM users WHERE email = ?
	`
	var u user.User
	err := r.db.QueryRowContext(ctx, query, email).Scan(
		&u.ID, &u.Name, &u.Email, &u.Phone, &u.CreatedTime, &u.UpdatedTime)
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
		SELECT id, name, email, phone, created_time, updated_time
		FROM users ORDER BY created_time DESC
	`
	rows, err := r.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []*user.User
	for rows.Next() {
		var u user.User
		if err := rows.Scan(&u.ID, &u.Name, &u.Email, &u.Phone, &u.CreatedTime, &u.UpdatedTime); err != nil {
			return nil, err
		}
		users = append(users, &u)
	}
	return users, nil
}
