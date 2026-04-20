"""UserRepository 的 Django ORM 实现。"""
from __future__ import annotations

from django.core.paginator import Paginator

from user.domain.repository import UserRepository
from user.domain.user import User
from user.infrastructure.models import UserModel


def _to_domain(row: UserModel) -> User:
    return User(
        id=row.pk,
        name=row.name,
        email=row.email,
        phone=row.phone or None,
        address=row.address or None,
        created_time=row.created_time,
        updated_time=row.updated_time,
    )


class DjangoUserRepository(UserRepository):
    """使用 Django ORM 的仓储实现。"""

    def save(self, user: User) -> User:
        if user.id is None:
            row = UserModel.objects.create(
                name=user.name,
                email=user.email,
                phone=user.phone,
                address=user.address,
            )
        else:
            row = UserModel.objects.get(pk=user.id)
            row.name = user.name
            row.email = user.email
            row.phone = user.phone
            row.address = user.address
            row.save()
        return _to_domain(row)

    def find_by_id(self, user_id: int) -> User | None:
        row = UserModel.objects.filter(pk=user_id).first()
        return _to_domain(row) if row else None

    def find_by_email(self, email: str) -> User | None:
        row = UserModel.objects.filter(email=email).first()
        return _to_domain(row) if row else None

    def list_all(self, offset: int = 0, limit: int = 20) -> tuple[list[User], int]:
        qs = UserModel.objects.all().order_by("-id")
        total = qs.count()
        rows = list(qs[offset : offset + limit])
        return [_to_domain(r) for r in rows], total

    def delete(self, user_id: int) -> None:
        UserModel.objects.filter(pk=user_id).delete()
