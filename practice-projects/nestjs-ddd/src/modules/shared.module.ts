import { Global, Module } from '@nestjs/common';
import { EVENT_PUBLISHER } from '../domain/shared/events/event-publisher';
import { InMemoryEventPublisher } from '../infrastructure/messaging/in-memory-event.publisher';

/**
 * 共享基础设施模块（跨多个业务模块使用的基础设施）
 *
 * 如事件发布器、全局缓存等。标记为 @Global() 让其他模块可直接使用，
 * 无需重复 imports。
 */
@Global()
@Module({
  providers: [
    {
      provide: EVENT_PUBLISHER,
      useClass: InMemoryEventPublisher,
    },
  ],
  exports: [EVENT_PUBLISHER],
})
export class SharedModule {}
