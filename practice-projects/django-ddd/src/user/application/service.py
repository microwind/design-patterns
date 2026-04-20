"""User 应用服务：编排用例 + 发布事件。

应用层不含业务规则（业务规则在领域模型内），只负责协调。
"""
from __future__ import annotations

from shared.domain.publisher import EventPublisher
from shared.infrastructure.events import get_publisher
from shared.infrastructure.exceptions import NotFoundError, ValidationError
from user.application.dto import (
    CreateUserCommand,
    UpdateEmailCommand,
    UpdatePhoneCommand,
    UserDTO,
)
from user.domain.events import UserCreatedEvent, UserEmailUpdatedEvent
from user.domain.repository import UserRepository
from user.domain.user import User


class UserApplicationService:
    def __init__(
        self,
        repository: UserRepository,
        publisher: EventPublisher | None = None,
    ) -> None:
        self._repo = repository
        self._publisher = publisher or get_publisher()

    # ---- 写 -------------------------------------------------------------
    def create_user(self, cmd: CreateUserCommand) -> UserDTO:
        if self._repo.find_by_email(cmd.email):
            raise ValidationError(f"邮箱已存在: {cmd.email}")
        user = User.create(cmd.name, cmd.email, cmd.phone, cmd.address)
        user = self._repo.save(user)
        assert user.id is not None
        self._publisher.publish(
            UserCreatedEvent(user_id=user.id, name=user.name, email=user.email)
        )
        return _to_dto(user)

    def update_email(self, cmd: UpdateEmailCommand) -> UserDTO:
        user = self._require(cmd.user_id)
        existed = self._repo.find_by_email(cmd.email)
        if existed and existed.id != user.id:
            raise ValidationError(f"邮箱已存在: {cmd.email}")
        old_email = user.email
        user.update_email(cmd.email)
        user = self._repo.save(user)
        assert user.id is not None
        self._publisher.publish(
            UserEmailUpdatedEvent(user_id=user.id, old_email=old_email, new_email=user.email)
        )
        return _to_dto(user)

    def update_phone(self, cmd: UpdatePhoneCommand) -> UserDTO:
        user = self._require(cmd.user_id)
        user.update_phone(cmd.phone)
        user = self._repo.save(user)
        return _to_dto(user)

    def delete_user(self, user_id: int) -> None:
        self._require(user_id)
        self._repo.delete(user_id)

    # ---- 读 -------------------------------------------------------------
    def get_user(self, user_id: int) -> UserDTO:
        return _to_dto(self._require(user_id))

    def list_users(self, offset: int = 0, limit: int = 20) -> tuple[list[UserDTO], int]:
        users, total = self._repo.list_all(offset=offset, limit=limit)
        return [_to_dto(u) for u in users], total

    # ---- helper ---------------------------------------------------------
    def _require(self, user_id: int) -> User:
        user = self._repo.find_by_id(user_id)
        if user is None:
            raise NotFoundError(f"用户不存在: id={user_id}")
        return user


def _to_dto(user: User) -> UserDTO:
    assert user.id is not None
    return UserDTO(
        id=user.id,
        name=user.name,
        email=user.email,
        phone=user.phone,
        address=user.address,
        created_time=user.created_time,
        updated_time=user.updated_time,
    )
