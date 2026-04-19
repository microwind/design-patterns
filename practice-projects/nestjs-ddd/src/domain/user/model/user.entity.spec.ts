import { User } from './user.entity';

describe('User (领域模型)', () => {
  it('应该正确创建用户', () => {
    const user = User.create({
      name: 'jarry',
      email: 'jarry@example.com',
      phone: '13800138000',
      address: '北京',
    });

    expect(user.name).toBe('jarry');
    expect(user.email).toBe('jarry@example.com');
    expect(user.phone).toBe('13800138000');
    expect(user.address).toBe('北京');
    expect(user.id).toBeUndefined();
  });

  it('用户名为空时应该抛出异常', () => {
    expect(() =>
      User.create({ name: '', email: 'x@x.com' }),
    ).toThrow('用户名不能为空');
  });

  it('邮箱格式不正确时应该抛出异常', () => {
    expect(() =>
      User.create({ name: 'jarry', email: 'invalid-email' }),
    ).toThrow('邮箱格式不正确');
  });

  it('手机号格式不正确时应该抛出异常', () => {
    expect(() =>
      User.create({
        name: 'jarry',
        email: 'jarry@example.com',
        phone: '12345',
      }),
    ).toThrow('手机号格式不正确');
  });

  it('updateEmail 应该更新邮箱并刷新时间', async () => {
    const user = User.create({ name: 'jarry', email: 'old@example.com' });
    const before = user.updatedTime;
    await new Promise((r) => setTimeout(r, 5));

    user.updateEmail('new@example.com');

    expect(user.email).toBe('new@example.com');
    expect(user.updatedTime.getTime()).toBeGreaterThan(before.getTime());
  });
});
