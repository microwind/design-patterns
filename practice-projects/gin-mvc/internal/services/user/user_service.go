package user

import (
	"context"
	"errors"

	model "gin-mvc/internal/models/user"
	userrepo "gin-mvc/internal/repository/user"
)

type Service struct {
	repo userrepo.Repository
}

func New(repo userrepo.Repository) *Service {
	return &Service{repo: repo}
}

func (s *Service) CreateUser(ctx context.Context, name, email, phone string) (*model.User, error) {
	if existing, _ := s.repo.FindByName(ctx, name); existing != nil {
		return nil, errors.New("username already exists")
	}
	if existing, _ := s.repo.FindByEmail(ctx, email); existing != nil {
		return nil, errors.New("email already exists")
	}

	u, err := model.New(name, email, phone)
	if err != nil {
		return nil, err
	}
	if err := s.repo.Create(ctx, u); err != nil {
		return nil, err
	}
	return u, nil
}

func (s *Service) GetUserByID(ctx context.Context, id int64) (*model.User, error) {
	u, err := s.repo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if u == nil {
		return nil, errors.New("user not found")
	}
	return u, nil
}

func (s *Service) GetAllUsers(ctx context.Context) ([]*model.User, error) {
	return s.repo.FindAll(ctx)
}

func (s *Service) UpdateEmail(ctx context.Context, id int64, email string) error {
	u, err := s.repo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return errors.New("user not found")
	}
	if existing, _ := s.repo.FindByEmail(ctx, email); existing != nil && existing.ID != id {
		return errors.New("email already exists")
	}
	if err := u.UpdateEmail(email); err != nil {
		return err
	}
	return s.repo.Update(ctx, u)
}

func (s *Service) UpdatePhone(ctx context.Context, id int64, newPhone string) error {
	u, err := s.repo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if u == nil {
		return errors.New("user not found")
	}
	u.UpdatePhone(newPhone)
	return s.repo.Update(ctx, u)
}

func (s *Service) DeleteUser(ctx context.Context, id int64) error {
	return s.repo.Delete(ctx, id)
}
