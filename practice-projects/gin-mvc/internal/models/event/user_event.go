package event

import "time"

const (
	UserCreatedEvent = "user.created"
	UserDeletedEvent = "user.deleted"
)

type UserEvent struct {
	BaseEvent
	UserID int64  `json:"user_id"`
	Name   string `json:"name"`
	Email  string `json:"email"`
	Status string `json:"status"`
}

func (e *UserEvent) EventData() interface{} {
	return e
}

func NewUserCreated(userID int64, name, email string) *UserEvent {
	return &UserEvent{BaseEvent: BaseEvent{Type: UserCreatedEvent, Timestamp: time.Now()}, UserID: userID, Name: name, Email: email, Status: "ACTIVE"}
}
