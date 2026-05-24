// Package user 提供用户聚合根的持久化适配。
package user

import (
	"database/sql"
	"time"
)

// UserDO 用户数据对象,所有 db tag 集中于此。
// Phone/Address 用 sql.NullString 适配数据库 NULL 列,与 domain 层使用的
// *string 解耦,转换由 UserConverter 完成。
type UserDO struct {
	ID          int64          `db:"id"`
	Name        string         `db:"name"`
	Email       string         `db:"email"`
	Phone       sql.NullString `db:"phone"`
	Address     sql.NullString `db:"address"`
	CreatedTime time.Time      `db:"created_time"`
	UpdatedTime time.Time      `db:"updated_time"`
}
