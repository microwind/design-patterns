package user

import (
	"context"
	"errors"
	"gin-ddd/internal/application/dto/user"
	userModel "gin-ddd/internal/domain/model/user"
	"gin-ddd/internal/domain/repository/user"
)

// UserService 用户应用服务
type UserService struct {
	userRepo user.UserRepository
}

// NewUserService 创建用户应用服务
func NewUserService(userRepo user.UserRepository) *UserService {
	return &UserService{
		userRepo: userRepo,
	}
}

// CreateUser 创建用户
func (s *UserService) CreateUser(ctx context.Context, username, email, password string) (*user.UserDTO, error) {
	// 检查用户名是否已存在
	existingUser, _ := s.userRepo.FindByUsername(ctx, username)
	if existingUser != nil {
		return nil, errors.New("用户名已存在")
	}

	// 检查邮箱是否已存在
	existingUser, _ = s.userRepo.FindByEmail(ctx, email)
	if existingUser != nil {
		return nil, errors.New("邮箱已被使用")
	}

	// 创建用户实体
	newUser, err := userModel.NewUser(username, email, password)
	if err != nil {
		return nil, err
	}

	// 持久化用户
	if err := s.userRepo.Create(ctx, newUser); err != nil {
		return nil, err
	}

	return user.ToDTO(newUser), nil
}

// GetUserByID 根据ID获取用户
func (s *UserService) GetUserByID(ctx context.Context, id int64) (*user.UserDTO, error) {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if u == nil {
		return nil, errors.New("用户不存在")
	}
	return user.ToDTO(u), nil
}

// GetUserByUsername 根据用户名获取用户
func (s *UserService) GetUserByUsername(ctx context.Context, username string) (*user.UserDTO, error) {
	u, err := s.userRepo.FindByUsername(ctx, username)
	if err != nil {
		return nil, err
	}
	if u == nil {
		return nil, errors.New("用户不存在")
	}
	return user.ToDTO(u), nil
}

// GetAllUsers 获取所有用户
func (s *UserService) GetAllUsers(ctx context.Context) ([]*user.UserDTO, error) {
	users, err := s.userRepo.FindAll(ctx)
	if err != nil {
		return nil, err
	}
	return user.ToDTOs(users), nil
}

// UpdateEmail 更新用户邮箱
func (s *UserService) UpdateEmail(ctx context.Context, id int64, email string) error {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return errors.New("用户不存在")
	}

	// 检查邮箱是否已被其他用户使用
	existingUser, _ := s.userRepo.FindByEmail(ctx, email)
	if existingUser != nil && existingUser.ID != id {
		return errors.New("邮箱已被使用")
	}

	if err := u.UpdateEmail(email); err != nil {
		return err
	}

	return s.userRepo.Update(ctx, u)
}

// UpdatePassword 更新用户密码
func (s *UserService) UpdatePassword(ctx context.Context, id int64, newPassword string) error {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return errors.New("用户不存在")
	}

	if err := u.UpdatePassword(newPassword); err != nil {
		return err
	}

	return s.userRepo.Update(ctx, u)
}

// ActivateUser 激活用户
func (s *UserService) ActivateUser(ctx context.Context, id int64) error {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return errors.New("用户不存在")
	}

	if err := u.Activate(); err != nil {
		return err
	}

	return s.userRepo.Update(ctx, u)
}

// DeactivateUser 停用用户
func (s *UserService) DeactivateUser(ctx context.Context, id int64) error {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return errors.New("用户不存在")
	}

	u.Deactivate()
	return s.userRepo.Update(ctx, u)
}

// BlockUser 封禁用户
func (s *UserService) BlockUser(ctx context.Context, id int64) error {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return errors.New("用户不存在")
	}

	u.Block()
	return s.userRepo.Update(ctx, u)
}

// DeleteUser 删除用户
func (s *UserService) DeleteUser(ctx context.Context, id int64) error {
	return s.userRepo.Delete(ctx, id)
}
