/** 领域事件基础接口 */
export interface DomainEvent {
  readonly eventName: string;
  readonly occurredAt: Date;
}

export abstract class BaseDomainEvent implements DomainEvent {
  public readonly occurredAt: Date;
  abstract readonly eventName: string;

  constructor() {
    this.occurredAt = new Date();
  }
}
