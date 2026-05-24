// Package user 用户仓储实现。
//
// 仓储层只读写 UserDO,通过 UserConverter 与领域聚合根互转。
package user

import (
	"context"
	"database/sql"
	"errors"

	userModel "gin-ddd/internal/domain/model/user"
	"gin-ddd/pkg/utils"

	"github.com/jmoiron/sqlx"
)

// UserRepositoryImpl 用户仓储实现(基于 MySQL)。
type UserRepositoryImpl struct {
	db *sqlx.DB
}

// NewUserRepository 构造仓储实例。
func NewUserRepository(db *sqlx.DB) *UserRepositoryImpl {
	return &UserRepositoryImpl{db: db}
}

// Create 持久化新用户,回填主键后调用 User.MarkPersisted 记录创建事件。
func (r *UserRepositoryImpl) Create(ctx context.Context, u *userModel.User) error {
	do := toDO(u)
	query := `
		INSERT INTO users (name, email, phone, address, created_time, updated_time)
		VALUES (?, ?, ?, ?, ?, ?)
	`
	utils.GetLogger().Debug("执行INSERT用户SQL: name=%s, email=%s", do.Name, do.Email)
	result, err := r.db.ExecContext(ctx, query,
		do.Name, do.Email, do.Phone, do.Address, do.CreatedTime, do.UpdatedTime)
	if err != nil {
		utils.GetLogger().Error("插入用户失败: %v, name=%s, email=%s", err, do.Name, do.Email)
		return err
	}
	id, err := result.LastInsertId()
	if err != nil {
		utils.GetLogger().Error("获取插入ID失败: %v", err)
		return err
	}
	if err := u.MarkPersisted(id); err != nil {
		utils.GetLogger().Error("用户初始化标记失败: %v, id=%d", err, id)
		return err
	}
	utils.GetLogger().Debug("用户插入成功: id=%d, name=%s", id, do.Name)
	return nil
}

// Update 更新用户。
func (r *UserRepositoryImpl) Update(ctx context.Context, u *userModel.User) error {
	do := toDO(u)
	query := `
		UPDATE users
		SET name = ?, email = ?, phone = ?, address = ?, updated_time = ?
		WHERE id = ?
	`
	utils.GetLogger().Debug("执行UPDATE用户SQL: id=%d, name=%s, email=%s", do.ID, do.Name, do.Email)
	if _, err := r.db.ExecContext(ctx, query,
		do.Name, do.Email, do.Phone, do.Address, do.UpdatedTime, do.ID); err != nil {
		utils.GetLogger().Error("更新用户失败: %v, id=%d", err, do.ID)
		return err
	}
	utils.GetLogger().Debug("用户更新成功: id=%d", do.ID)
	return nil
}

// Delete 删除用户。
func (r *UserRepositoryImpl) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM users WHERE id = ?`
	utils.GetLogger().Debug("执行DELETE用户SQL: id=%d", id)
	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		utils.GetLogger().Error("删除用户失败: %v, id=%d", err, id)
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		utils.GetLogger().Warn("删除用户: 用户不存在, id=%d", id)
	} else {
		utils.GetLogger().Debug("用户删除成功: id=%d, rowsAffected=%d", id, rowsAffected)
	}
	return nil
}

// FindByID 根据 ID 查询用户。
func (r *UserRepositoryImpl) FindByID(ctx context.Context, id int64) (*userModel.User, error) {
	const query = `
		SELECT id, name, email, phone, address, created_time, updated_time
		FROM users WHERE id = ?
	`
	var do UserDO
	err := r.db.GetContext(ctx, &do, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		utils.GetLogger().Error("查询用户失败: %v, id=%d", err, id)
		return nil, err
	}
	return toModel(&do), nil
}

// FindByName 根据用户名查询用户。
func (r *UserRepositoryImpl) FindByName(ctx context.Context, name string) (*userModel.User, error) {
	const query = `
		SELECT id, name, email, phone, address, created_time, updated_time
		FROM users WHERE name = ?
	`
	var do UserDO
	err := r.db.GetContext(ctx, &do, query, name)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		utils.GetLogger().Error("查询用户失败: %v, name=%s", err, name)
		return nil, err
	}
	return toModel(&do), nil
}

// FindByEmail 根据邮箱查询用户。
func (r *UserRepositoryImpl) FindByEmail(ctx context.Context, email string) (*userModel.User, error) {
	const query = `
		SELECT id, name, email, phone, address, created_time, updated_time
		FROM users WHERE email = ?
	`
	var do UserDO
	err := r.db.GetContext(ctx, &do, query, email)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		utils.GetLogger().Error("查询用户失败: %v, email=%s", err, email)
		return nil, err
	}
	return toModel(&do), nil
}

// FindAll 查询所有用户。
func (r *UserRepositoryImpl) FindAll(ctx context.Context) ([]*userModel.User, error) {
	const query = `
		SELECT id, name, email, phone, address, created_time, updated_time
		FROM users ORDER BY created_time DESC
	`
	var dos []*UserDO
	if err := r.db.SelectContext(ctx, &dos, query); err != nil {
		utils.GetLogger().Error("查询所有用户失败: %v", err)
		return nil, err
	}
	return toModels(dos), nil
}
