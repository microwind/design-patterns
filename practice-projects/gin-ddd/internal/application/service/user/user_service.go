package user

import (
	"context"
	"errors"
	"gin-ddd/internal/application/dto/user"
	userModel "gin-ddd/internal/domain/model/user"
	userDomain "gin-ddd/internal/domain/repository/user"
	"gin-ddd/pkg/utils"
)

// UserService 用户应用服务
type UserService struct {
	userRepo userDomain.UserRepository
}

// NewUserService 创建用户应用服务
func NewUserService(userRepo userDomain.UserRepository) *UserService {
	return &UserService{
		userRepo: userRepo,
	}
}

// CreateUser 创建用户
func (s *UserService) CreateUser(ctx context.Context, name, email, phone, address string) (*user.UserDTO, error) {
	utils.GetLogger().Info("UserService.CreateUser 开始: name=%s, email=%s", name, email)

	// 检查用户名是否已存在
	existingUser, _ := s.userRepo.FindByName(ctx, name)
	if existingUser != nil {
		utils.GetLogger().Warn("用户创建失败: 用户名已存在, name=%s", name)
		return nil, errors.New("用户名已存在")
	}

	// 检查邮箱是否已存在
	existingUser, _ = s.userRepo.FindByEmail(ctx, email)
	if existingUser != nil {
		utils.GetLogger().Warn("用户创建失败: 邮箱已被使用, email=%s", email)
		return nil, errors.New("邮箱已被使用")
	}

	// 创建用户实体
	newUser, err := userModel.NewUser(name, email, phone, address)
	if err != nil {
		utils.GetLogger().Error("用户实体创建失败: %v, name=%s, email=%s", err, name, email)
		return nil, err
	}

	// 持久化用户
	if err := s.userRepo.Create(ctx, newUser); err != nil {
		utils.GetLogger().Error("用户持久化失败: %v, name=%s, email=%s", err, name, email)
		return nil, err
	}

	utils.GetLogger().Info("用户创建成功: id=%d, name=%s, email=%s", newUser.ID, name, email)
	return user.ToDTO(newUser), nil
}

// GetUserByID 根据ID获取用户
func (s *UserService) GetUserByID(ctx context.Context, id int64) (*user.UserDTO, error) {
	utils.GetLogger().Debug("UserService.GetUserByID: id=%d", id)
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		utils.GetLogger().Error("查询用户失败: %v, id=%d", err, id)
		return nil, err
	}
	if u == nil {
		utils.GetLogger().Warn("用户不存在: id=%d", id)
		return nil, errors.New("用户不存在")
	}
	utils.GetLogger().Debug("用户查询成功: id=%d, name=%s", u.ID, u.Name)
	return user.ToDTO(u), nil
}

// GetUserByName 根据用户名获取用户
func (s *UserService) GetUserByName(ctx context.Context, name string) (*user.UserDTO, error) {
	utils.GetLogger().Debug("UserService.GetUserByName: name=%s", name)
	u, err := s.userRepo.FindByName(ctx, name)
	if err != nil {
		utils.GetLogger().Error("查询用户失败: %v, name=%s", err, name)
		return nil, err
	}
	if u == nil {
		utils.GetLogger().Warn("用户不存在: name=%s", name)
		return nil, errors.New("用户不存在")
	}
	utils.GetLogger().Debug("用户查询成功: id=%d, name=%s", u.ID, name)
	return user.ToDTO(u), nil
}

// GetAllUsers 获取所有用户
func (s *UserService) GetAllUsers(ctx context.Context) ([]*user.UserDTO, error) {
	utils.GetLogger().Debug("UserService.GetAllUsers")
	users, err := s.userRepo.FindAll(ctx)
	if err != nil {
		utils.GetLogger().Error("查询所有用户失败: %v", err)
		return nil, err
	}
	utils.GetLogger().Info("查询所有用户成功: 共%d条记录", len(users))
	return user.ToDTOs(users), nil
}

// UpdateEmail 更新用户邮箱
func (s *UserService) UpdateEmail(ctx context.Context, id int64, email string) error {
	utils.GetLogger().Info("UserService.UpdateEmail 开始: id=%d, newEmail=%s", id, email)
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		utils.GetLogger().Error("查询用户失败: %v, id=%d", err, id)
		return err
	}
	if u == nil {
		utils.GetLogger().Warn("用户不存在: id=%d", id)
		return errors.New("用户不存在")
	}

	// 检查邮箱是否已被其他用户使用
	existingUser, _ := s.userRepo.FindByEmail(ctx, email)
	if existingUser != nil && existingUser.ID != id {
		utils.GetLogger().Warn("邮箱已被使用: email=%s", email)
		return errors.New("邮箱已被使用")
	}

	if err := u.UpdateEmail(email); err != nil {
		utils.GetLogger().Error("邮箱更新失败: %v, id=%d, newEmail=%s", err, id, email)
		return err
	}

	if err := s.userRepo.Update(ctx, u); err != nil {
		utils.GetLogger().Error("用户持久化失败: %v, id=%d", err, id)
		return err
	}

	utils.GetLogger().Info("用户邮箱更新成功: id=%d, newEmail=%s", id, email)
	return nil
}

// UpdatePhone 更新用户手机
func (s *UserService) UpdatePhone(ctx context.Context, id int64, newPhone string) error {
	utils.GetLogger().Info("UserService.UpdatePhone 开始: id=%d, newPhone=%s", id, newPhone)
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		utils.GetLogger().Error("查询用户失败: %v, id=%d", err, id)
		return err
	}
	if u == nil {
		utils.GetLogger().Warn("用户不存在: id=%d", id)
		return errors.New("用户不存在")
	}

	if err := u.UpdatePhone(newPhone); err != nil {
		utils.GetLogger().Error("手机号更新失败: %v, id=%d, newPhone=%s", err, id, newPhone)
		return err
	}

	if err := s.userRepo.Update(ctx, u); err != nil {
		utils.GetLogger().Error("用户持久化失败: %v, id=%d", err, id)
		return err
	}

	utils.GetLogger().Info("用户手机号更新成功: id=%d", id)
	return nil
}

// DeleteUser 删除用户
func (s *UserService) DeleteUser(ctx context.Context, id int64) error {
	utils.GetLogger().Info("UserService.DeleteUser 开始: id=%d", id)
	err := s.userRepo.Delete(ctx, id)
	if err != nil {
		utils.GetLogger().Error("用户删除失败: %v, id=%d", err, id)
		return err
	}
	utils.GetLogger().Info("用户删除成功: id=%d", id)
	return nil
}
