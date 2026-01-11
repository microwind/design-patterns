package event

import "time"

// 用户事件类型常量
const (
	UserCreatedEvent     = "user.created"
	UserActivatedEvent   = "user.activated"
	UserDeactivatedEvent = "user.deactivated"
	UserBlockedEvent     = "user.blocked"
	UserDeletedEvent     = "user.deleted"
)

// UserEvent 用户事件
type UserEvent struct {
	BaseEvent
	UserID   int64  `json:"user_id"`
	Username string `json:"username"`
	Email    string `json:"email"`
	Status   string `json:"status"`
}

// EventData 返回事件数据
func (e *UserEvent) EventData() interface{} {
	return e
}

// NewUserCreatedEvent 创建用户创建事件
func NewUserCreatedEvent(userID int64, username, email string) *UserEvent {
	return &UserEvent{
		BaseEvent: BaseEvent{
			Type:      UserCreatedEvent,
			Timestamp: time.Now(),
		},
		UserID:   userID,
		Username: username,
		Email:    email,
		Status:   "ACTIVE",
	}
}

// NewUserBlockedEvent 创建用户封禁事件
func NewUserBlockedEvent(userID int64, username, email string) *UserEvent {
	return &UserEvent{
		BaseEvent: BaseEvent{
			Type:      UserBlockedEvent,
			Timestamp: time.Now(),
		},
		UserID:   userID,
		Username: username,
		Email:    email,
		Status:   "BLOCKED",
	}
}
