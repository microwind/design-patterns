import { DomainEvent } from './domain-event';

export const EVENT_PUBLISHER = Symbol('EVENT_PUBLISHER');

/** 事件发布器接口（由 infrastructure 层实现） */
export interface EventPublisher {
  publish<T extends DomainEvent>(event: T): Promise<void>;
  publishAll<T extends DomainEvent>(events: T[]): Promise<void>;
}
