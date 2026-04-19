export class CreateUserCommand {
  constructor(
    public readonly name: string,
    public readonly email: string,
    public readonly phone?: string,
    public readonly address?: string,
  ) {}
}

export class UpdateEmailCommand {
  constructor(
    public readonly userId: number,
    public readonly email: string,
  ) {}
}

export class UpdatePhoneCommand {
  constructor(
    public readonly userId: number,
    public readonly phone: string,
  ) {}
}
