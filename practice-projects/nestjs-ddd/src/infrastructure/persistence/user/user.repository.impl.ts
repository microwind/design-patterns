import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from '../../../domain/user/model/user.entity';
import { UserRepository } from '../../../domain/user/repository/user.repository';
import { USER_DATASOURCE } from '../../config/typeorm-config.service';
import { UserOrmEntity } from './user.orm-entity';

/**
 * 用户仓储实现（基础设施层）
 *
 * 负责领域对象 User 与 ORM 实体 UserOrmEntity 的互相转换，
 * 业务层完全不感知 TypeORM。
 */
@Injectable()
export class UserRepositoryImpl implements UserRepository {
  constructor(
    @InjectRepository(UserOrmEntity, USER_DATASOURCE)
    private readonly repo: Repository<UserOrmEntity>,
  ) {}

  async create(user: User): Promise<User> {
    const saved = await this.repo.save(this.toOrm(user));
    return this.toDomain(saved);
  }

  async update(user: User): Promise<User> {
    if (!user.id) {
      throw new Error('更新用户时 id 不能为空');
    }
    const saved = await this.repo.save(this.toOrm(user));
    return this.toDomain(saved);
  }

  async delete(id: number): Promise<void> {
    await this.repo.delete(id);
  }

  async findById(id: number): Promise<User | null> {
    const orm = await this.repo.findOne({ where: { id } });
    return orm ? this.toDomain(orm) : null;
  }

  async findByName(name: string): Promise<User | null> {
    const orm = await this.repo.findOne({ where: { name } });
    return orm ? this.toDomain(orm) : null;
  }

  async findByEmail(email: string): Promise<User | null> {
    const orm = await this.repo.findOne({ where: { email } });
    return orm ? this.toDomain(orm) : null;
  }

  async findAll(): Promise<User[]> {
    const list = await this.repo.find({ order: { id: 'ASC' } });
    return list.map((o) => this.toDomain(o));
  }

  private toDomain(orm: UserOrmEntity): User {
    return User.restore({
      id: Number(orm.id),
      name: orm.name,
      email: orm.email,
      phone: orm.phone ?? undefined,
      address: orm.address ?? undefined,
      createdTime: orm.createdTime,
      updatedTime: orm.updatedTime,
    });
  }

  private toOrm(user: User): UserOrmEntity {
    const orm = new UserOrmEntity();
    if (user.id) {
      orm.id = user.id;
    }
    orm.name = user.name;
    orm.email = user.email;
    orm.phone = user.phone ?? null;
    orm.address = user.address ?? null;
    orm.createdTime = user.createdTime;
    orm.updatedTime = user.updatedTime;
    return orm;
  }
}
