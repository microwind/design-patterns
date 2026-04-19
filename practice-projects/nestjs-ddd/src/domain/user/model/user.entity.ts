/**
 * 用户聚合根 - 领域模型
 *
 * 领域对象只依赖纯业务规则，不依赖任何框架（NestJS、TypeORM 等）。
 * 所有对用户状态的修改都通过聚合根方法执行，保证业务不变量。
 *
 * 字段命名与 gin-ddd 对齐：createdTime / updatedTime。
 */
export class User {
  private _id?: number;
  private _name: string;
  private _email: string;
  private _phone?: string;
  private _address?: string;
  private _createdTime: Date;
  private _updatedTime: Date;

  private constructor(props: {
    id?: number;
    name: string;
    email: string;
    phone?: string;
    address?: string;
    createdTime: Date;
    updatedTime: Date;
  }) {
    this._id = props.id;
    this._name = props.name;
    this._email = props.email;
    this._phone = props.phone;
    this._address = props.address;
    this._createdTime = props.createdTime;
    this._updatedTime = props.updatedTime;
  }

  /** 工厂方法：创建新用户（聚合根的主要入口） */
  static create(params: {
    name: string;
    email: string;
    phone?: string;
    address?: string;
  }): User {
    if (!params.name || params.name.trim() === '') {
      throw new Error('用户名不能为空');
    }
    if (!params.email || params.email.trim() === '') {
      throw new Error('邮箱不能为空');
    }
    if (!User.isValidEmail(params.email)) {
      throw new Error('邮箱格式不正确');
    }
    if (params.phone && !User.isValidPhone(params.phone)) {
      throw new Error('手机号格式不正确');
    }

    const now = new Date();
    return new User({
      name: params.name.trim(),
      email: params.email.trim(),
      phone: params.phone?.trim(),
      address: params.address?.trim(),
      createdTime: now,
      updatedTime: now,
    });
  }

  /** 从持久化层重建聚合（仓储专用） */
  static restore(props: {
    id: number;
    name: string;
    email: string;
    phone?: string;
    address?: string;
    createdTime: Date;
    updatedTime: Date;
  }): User {
    return new User(props);
  }

  updateEmail(email: string): void {
    if (!email || email.trim() === '') {
      throw new Error('邮箱不能为空');
    }
    if (!User.isValidEmail(email)) {
      throw new Error('邮箱格式不正确');
    }
    this._email = email.trim();
    this._updatedTime = new Date();
  }

  updatePhone(phone: string): void {
    if (phone && !User.isValidPhone(phone)) {
      throw new Error('手机号格式不正确');
    }
    this._phone = phone?.trim() || undefined;
    this._updatedTime = new Date();
  }

  updateAddress(address: string): void {
    this._address = address?.trim() || undefined;
    this._updatedTime = new Date();
  }

  assignId(id: number): void {
    if (this._id) {
      throw new Error('用户 ID 已存在，不能重复赋值');
    }
    this._id = id;
  }

  private static isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  private static isValidPhone(phone: string): boolean {
    return /^1[3-9]\d{9}$/.test(phone);
  }

  get id(): number | undefined {
    return this._id;
  }
  get name(): string {
    return this._name;
  }
  get email(): string {
    return this._email;
  }
  get phone(): string | undefined {
    return this._phone;
  }
  get address(): string | undefined {
    return this._address;
  }
  get createdTime(): Date {
    return this._createdTime;
  }
  get updatedTime(): Date {
    return this._updatedTime;
  }
}
