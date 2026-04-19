import { Injectable, Logger } from '@nestjs/common';
import { EventEmitter2 } from '@nestjs/event-emitter';
import { DomainEvent } from '../../domain/shared/events/domain-event';
import { EventPublisher } from '../../domain/shared/events/event-publisher';

/**
 * 基于 @nestjs/event-emitter 的内存事件发布器
 *
 * 开箱即用。生产环境可扩展为 Kafka / RabbitMQ / RocketMQ 版本，
 * 只要实现 EventPublisher 接口即可，应用层代码无需改动。
 */
@Injectable()
export class InMemoryEventPublisher implements EventPublisher {
  private readonly logger = new Logger(InMemoryEventPublisher.name);

  constructor(private readonly emitter: EventEmitter2) {}

  async publish<T extends DomainEvent>(event: T): Promise<void> {
    this.logger.debug(`发布领域事件: ${event.eventName}`);
    this.emitter.emit(event.eventName, event);
  }

  async publishAll<T extends DomainEvent>(events: T[]): Promise<void> {
    for (const event of events) {
      await this.publish(event);
    }
  }
}
