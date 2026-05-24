// Package user 用户应用服务。
package user

import (
	"context"

	userDTO "gin-ddd/internal/application/dto/user"
	domainErrors "gin-ddd/internal/domain/errors"
	"gin-ddd/internal/domain/event"
	userModel "gin-ddd/internal/domain/model/user"
	userDomain "gin-ddd/internal/domain/repository/user"
	userService "gin-ddd/internal/domain/service/user"
	"gin-ddd/pkg/utils"
)

// UserService 用户应用服务。
type UserService struct {
	userRepo       userDomain.UserRepository
	checker        userService.UserUniquenessChecker
	eventPublisher event.EventPublisher
	userTopic      string
}

// NewUserService 构造用户应用服务。
func NewUserService(
	userRepo userDomain.UserRepository,
	checker userService.UserUniquenessChecker,
	eventPublisher event.EventPublisher,
	userTopic string,
) *UserService {
	return &UserService{
		userRepo:       userRepo,
		checker:        checker,
		eventPublisher: eventPublisher,
		userTopic:      userTopic,
	}
}

// CreateUser 创建用户。唯一性校验下沉到聚合根 user.Register。
func (s *UserService) CreateUser(ctx context.Context, name, email, phone, address string) (*userDTO.UserDTO, error) {
	utils.GetLogger().Info("UserService.CreateUser 开始: name=%s, email=%s", name, email)

	newUser, err := userModel.Register(ctx, s.checker, name, email, phone, address)
	if err != nil {
		utils.GetLogger().Warn("用户注册失败: %v, name=%s, email=%s", err, name, email)
		return nil, err
	}

	if err := s.userRepo.Create(ctx, newUser); err != nil {
		utils.GetLogger().Error("用户持久化失败: %v, name=%s, email=%s", err, name, email)
		return nil, err
	}

	s.publishEvents(ctx, newUser.PullEvents())
	utils.GetLogger().Info("用户创建成功: id=%d, name=%s, email=%s", newUser.ID, name, email)
	return userDTO.ToDTO(newUser), nil
}

// GetUserByID 根据 ID 获取用户。
func (s *UserService) GetUserByID(ctx context.Context, id int64) (*userDTO.UserDTO, error) {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if u == nil {
		return nil, domainErrors.NewNotFound("用户", "id", id)
	}
	return userDTO.ToDTO(u), nil
}

// GetUserByName 根据用户名获取用户。
func (s *UserService) GetUserByName(ctx context.Context, name string) (*userDTO.UserDTO, error) {
	u, err := s.userRepo.FindByName(ctx, name)
	if err != nil {
		return nil, err
	}
	if u == nil {
		return nil, domainErrors.NewNotFound("用户", "name", name)
	}
	return userDTO.ToDTO(u), nil
}

// GetAllUsers 获取所有用户。
func (s *UserService) GetAllUsers(ctx context.Context) ([]*userDTO.UserDTO, error) {
	users, err := s.userRepo.FindAll(ctx)
	if err != nil {
		return nil, err
	}
	return userDTO.ToDTOs(users), nil
}

// UpdateEmail 更新用户邮箱。唯一性校验由 user.UpdateEmail 通过领域服务完成。
func (s *UserService) UpdateEmail(ctx context.Context, id int64, email string) error {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return domainErrors.NewNotFound("用户", "id", id)
	}
	if err := u.UpdateEmail(ctx, s.checker, email); err != nil {
		return err
	}
	if err := s.userRepo.Update(ctx, u); err != nil {
		return err
	}
	s.publishEvents(ctx, u.PullEvents())
	return nil
}

// UpdatePhone 更新用户手机号。
func (s *UserService) UpdatePhone(ctx context.Context, id int64, newPhone string) error {
	u, err := s.userRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return domainErrors.NewNotFound("用户", "id", id)
	}
	if err := u.UpdatePhone(newPhone); err != nil {
		return err
	}
	if err := s.userRepo.Update(ctx, u); err != nil {
		return err
	}
	s.publishEvents(ctx, u.PullEvents())
	return nil
}

// DeleteUser 删除用户。
func (s *UserService) DeleteUser(ctx context.Context, id int64) error {
	if err := s.userRepo.Delete(ctx, id); err != nil {
		utils.GetLogger().Error("用户删除失败: %v, id=%d", err, id)
		return err
	}
	utils.GetLogger().Info("用户删除成功: id=%d", id)
	return nil
}

func (s *UserService) publishEvents(ctx context.Context, events []event.DomainEvent) {
	for _, e := range events {
		if err := s.eventPublisher.Publish(ctx, s.userTopic, e); err != nil {
			utils.GetLogger().Error("[UserService] 发布领域事件失败: type=%s, err=%v", e.EventType(), err)
		}
	}
}
