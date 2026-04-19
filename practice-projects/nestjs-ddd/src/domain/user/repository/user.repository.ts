import { User } from '../model/user.entity';

export const USER_REPOSITORY = Symbol('USER_REPOSITORY');

/**
 * 用户仓储接口（领域层）
 *
 * 仓储属于领域层的抽象，具体实现放在 infrastructure 层。
 * 这样领域层不依赖任何具体的数据库技术。
 */
export interface UserRepository {
  create(user: User): Promise<User>;
  update(user: User): Promise<User>;
  delete(id: number): Promise<void>;
  findById(id: number): Promise<User | null>;
  findByName(name: string): Promise<User | null>;
  findByEmail(email: string): Promise<User | null>;
  findAll(): Promise<User[]>;
}
