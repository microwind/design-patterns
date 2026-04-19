import {
  Column,
  CreateDateColumn,
  Entity,
  Index,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';

/**
 * 用户 ORM 实体
 *
 * 表结构与 gin-ddd/docs/init.sql 完全对齐：
 *   id BIGINT PK, name / email UNIQUE, phone, created_time, updated_time。
 * 此处只关心持久化映射，不承载业务规则。
 */
@Entity({ name: 'users' })
export class UserOrmEntity {
  @PrimaryGeneratedColumn({ name: 'id', type: 'bigint' })
  id!: number;

  @Index({ unique: true })
  @Column({ name: 'name', length: 50 })
  name!: string;

  @Index({ unique: true })
  @Column({ name: 'email', length: 100 })
  email!: string;

  @Column({ name: 'phone', length: 20, nullable: true, type: 'varchar' })
  phone!: string | null;

  @Column({ name: 'address', length: 255, nullable: true, type: 'varchar' })
  address!: string | null;

  @CreateDateColumn({ name: 'created_time' })
  createdTime!: Date;

  @UpdateDateColumn({ name: 'updated_time' })
  updatedTime!: Date;
}
