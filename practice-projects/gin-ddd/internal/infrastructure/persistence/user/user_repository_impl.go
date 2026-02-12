package user

import (
	"context"
	"database/sql"
	"errors"
	"gin-ddd/internal/domain/model/user"
	"gin-ddd/pkg/utils"
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
		INSERT INTO users (name, email, phone, address, created_time, updated_time)
		VALUES (?, ?, ?, ?, ?, ?)
	`
	utils.GetLogger().Debug("执行INSERT用户SQL: name=%s, email=%s", u.Name, u.Email)
	result, err := r.db.ExecContext(ctx, query,
		u.Name, u.Email, u.Phone, u.Address, u.CreatedTime, u.UpdatedTime)
	if err != nil {
		utils.GetLogger().Error("插入用户失败: %v, name=%s, email=%s", err, u.Name, u.Email)
		return err
	}

	id, err := result.LastInsertId()
	if err != nil {
		utils.GetLogger().Error("获取插入ID失败: %v", err)
		return err
	}
	u.ID = id
	utils.GetLogger().Debug("用户插入成功: id=%d, name=%s", id, u.Name)
	return nil
}

// Update 更新用户
func (r *UserRepositoryImpl) Update(ctx context.Context, u *user.User) error {
	query := `
		UPDATE users
		SET name = ?, email = ?, phone = ?, address = ?, updated_time = ?
		WHERE id = ?
	`
	utils.GetLogger().Debug("执行UPDATE用户SQL: id=%d, name=%s, email=%s", u.ID, u.Name, u.Email)
	_, err := r.db.ExecContext(ctx, query,
		u.Name, u.Email, u.Phone, u.Address, u.UpdatedTime, u.ID)
	if err != nil {
		utils.GetLogger().Error("更新用户失败: %v, id=%d", err, u.ID)
		return err
	}
	utils.GetLogger().Debug("用户更新成功: id=%d", u.ID)
	return nil
}

// Delete 删除用户
func (r *UserRepositoryImpl) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM users WHERE id = ?`
	utils.GetLogger().Debug("执行DELETE用户SQL: id=%d", id)
	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		utils.GetLogger().Error("删除用户失败: %v, id=%d", err, id)
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		utils.GetLogger().Error("获取删除行数失败: %v, id=%d", err, id)
		return err
	}

	if rowsAffected == 0 {
		utils.GetLogger().Warn("删除用户: 用户不存在, id=%d", id)
	} else {
		utils.GetLogger().Debug("用户删除成功: id=%d, rowsAffected=%d", id, rowsAffected)
	}
	return nil
}

// FindByID 根据ID查询用户
func (r *UserRepositoryImpl) FindByID(ctx context.Context, id int64) (*user.User, error) {
	query := `
		SELECT id, name, email, phone, address, created_time, updated_time
		FROM users WHERE id = ?
	`
	utils.GetLogger().Debug("执行SELECT用户SQL by ID: id=%d", id)
	var u user.User
	err := r.db.QueryRowContext(ctx, query, id).Scan(
		&u.ID, &u.Name, &u.Email, &u.Phone, &u.Address, &u.CreatedTime, &u.UpdatedTime)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			utils.GetLogger().Debug("查询用户: 用户不存在, id=%d", id)
			return nil, nil
		}
		utils.GetLogger().Error("查询用户失败: %v, id=%d", err, id)
		return nil, err
	}
	utils.GetLogger().Debug("用户查询成功: id=%d, name=%s", u.ID, u.Name)
	return &u, nil
}

// FindByName 根据用户名查询用户
func (r *UserRepositoryImpl) FindByName(ctx context.Context, name string) (*user.User, error) {
	query := `
		SELECT id, name, email, phone, address, created_time, updated_time
		FROM users WHERE name = ?
	`
	utils.GetLogger().Debug("执行SELECT用户SQL by Name: name=%s", name)
	var u user.User
	err := r.db.QueryRowContext(ctx, query, name).Scan(
		&u.ID, &u.Name, &u.Email, &u.Phone, &u.Address, &u.CreatedTime, &u.UpdatedTime)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			utils.GetLogger().Debug("查询用户: 用户不存在, name=%s", name)
			return nil, nil
		}
		utils.GetLogger().Error("查询用户失败: %v, name=%s", err, name)
		return nil, err
	}
	utils.GetLogger().Debug("用户查询成功: name=%s, id=%d", name, u.ID)
	return &u, nil
}

// FindByEmail 根据邮箱查询用户
func (r *UserRepositoryImpl) FindByEmail(ctx context.Context, email string) (*user.User, error) {
	query := `
		SELECT id, name, email, phone, address, created_time, updated_time
		FROM users WHERE email = ?
	`
	utils.GetLogger().Debug("执行SELECT用户SQL by Email: email=%s", email)
	var u user.User
	err := r.db.QueryRowContext(ctx, query, email).Scan(
		&u.ID, &u.Name, &u.Email, &u.Phone, &u.Address, &u.CreatedTime, &u.UpdatedTime)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			utils.GetLogger().Debug("查询用户: 用户不存在, email=%s", email)
			return nil, nil
		}
		utils.GetLogger().Error("查询用户失败: %v, email=%s", err, email)
		return nil, err
	}
	utils.GetLogger().Debug("用户查询成功: email=%s, id=%d", email, u.ID)
	return &u, nil
}

// FindAll 查询所有用户
func (r *UserRepositoryImpl) FindAll(ctx context.Context) ([]*user.User, error) {
	query := `
		SELECT id, name, email, phone, address, created_time, updated_time
		FROM users ORDER BY created_time DESC
	`
	utils.GetLogger().Debug("执行SELECT所有用户SQL")
	rows, err := r.db.QueryContext(ctx, query)
	if err != nil {
		utils.GetLogger().Error("查询所有用户失败: %v", err)
		return nil, err
	}
	defer rows.Close()

	var users []*user.User
	for rows.Next() {
		var u user.User
		if err := rows.Scan(&u.ID, &u.Name, &u.Email, &u.Phone, &u.Address, &u.CreatedTime, &u.UpdatedTime); err != nil {
			utils.GetLogger().Error("扫描用户行失败: %v", err)
			return nil, err
		}
		users = append(users, &u)
	}

	if err := rows.Err(); err != nil {
		utils.GetLogger().Error("遍历用户行失败: %v", err)
		return nil, err
	}

	utils.GetLogger().Debug("所有用户查询成功: 共%d条记录", len(users))
	return users, nil
}
