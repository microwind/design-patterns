package user

import (
	"database/sql"

	userModel "gin-ddd/internal/domain/model/user"
)

// toDO 将用户领域模型转换为数据对象。
func toDO(u *userModel.User) *UserDO {
	if u == nil {
		return nil
	}
	return &UserDO{
		ID:          u.ID,
		Name:        u.Name,
		Email:       u.Email,
		Phone:       nullStringFromPtr(u.Phone),
		Address:     nullStringFromPtr(u.Address),
		CreatedTime: u.CreatedTime,
		UpdatedTime: u.UpdatedTime,
	}
}

// toModel 将数据对象还原为领域模型,经聚合根 Restore 工厂构建。
func toModel(do *UserDO) *userModel.User {
	if do == nil {
		return nil
	}
	return userModel.Restore(
		do.ID,
		do.Name,
		do.Email,
		ptrFromNullString(do.Phone),
		ptrFromNullString(do.Address),
		do.CreatedTime,
		do.UpdatedTime,
	)
}

// toModels 批量转换。
func toModels(dos []*UserDO) []*userModel.User {
	if dos == nil {
		return nil
	}
	out := make([]*userModel.User, 0, len(dos))
	for _, do := range dos {
		out = append(out, toModel(do))
	}
	return out
}

func nullStringFromPtr(p *string) sql.NullString {
	if p == nil {
		return sql.NullString{Valid: false}
	}
	return sql.NullString{String: *p, Valid: true}
}

func ptrFromNullString(ns sql.NullString) *string {
	if !ns.Valid {
		return nil
	}
	s := ns.String
	return &s
}
