/**
 * @file communication.c - 服务间通信模式（Service Communication Pattern）的 C 语言实现
 *
 * 实现同步下单（sync_place_order）和异步事件驱动下单（async_place_order + async_drain）。
 */

#include "func.h"

#include <string.h>

int comm_reserve(CommInventoryService *inventory, const char *sku, int quantity)
{
    if (strcmp(sku, "SKU-BOOK") != 0 || quantity <= 0 || inventory->book_stock < quantity) {
        return 0;
    }
    inventory->book_stock -= quantity;
    return 1;
}

int comm_charge(CommPaymentService *payment, const char *order_id)
{
    return strcmp(payment->fail_order_id, order_id) != 0;
}

CommOrder sync_place_order(CommInventoryService *inventory, CommPaymentService *payment, const char *order_id, const char *sku, int quantity)
{
    CommOrder order = {0};
    strcpy(order.order_id, order_id);
    strcpy(order.sku, sku);
    order.quantity = quantity;

    if (!comm_reserve(inventory, sku, quantity)) {
        strcpy(order.status, "REJECTED");
        return order;
    }
    if (!comm_charge(payment, order_id)) {
        strcpy(order.status, "PAYMENT_FAILED");
        return order;
    }
    strcpy(order.status, "CREATED");
    return order;
}

void order_store_init(CommOrderStore *store)
{
    store->count = 0;
}

void order_store_save(CommOrderStore *store, const CommOrder *order)
{
    store->orders[store->count++] = *order;
}

CommOrder *order_store_get(CommOrderStore *store, const char *order_id)
{
    for (int i = 0; i < store->count; i++) {
        if (strcmp(store->orders[i].order_id, order_id) == 0) {
            return &store->orders[i];
        }
    }
    return NULL;
}

void event_queue_init(EventQueue *queue)
{
    queue->count = 0;
}

void async_place_order(EventQueue *queue, CommOrderStore *store, const char *order_id, const char *sku, int quantity)
{
    CommOrder order = {0};
    strcpy(order.order_id, order_id);
    strcpy(order.sku, sku);
    order.quantity = quantity;
    strcpy(order.status, "PENDING");
    order_store_save(store, &order);

    strcpy(queue->events[queue->count].order_id, order_id);
    strcpy(queue->events[queue->count].sku, sku);
    queue->events[queue->count].quantity = quantity;
    queue->count++;
}

void async_drain(EventQueue *queue, CommOrderStore *store, CommInventoryService *inventory, CommPaymentService *payment)
{
    for (int i = 0; i < queue->count; i++) {
        OrderPlacedEvent *event = &queue->events[i];
        CommOrder *order = order_store_get(store, event->order_id);
        if (!comm_reserve(inventory, event->sku, event->quantity)) {
            strcpy(order->status, "REJECTED");
            continue;
        }
        if (!comm_charge(payment, event->order_id)) {
            strcpy(order->status, "PAYMENT_FAILED");
            continue;
        }
        strcpy(order->status, "CREATED");
    }
    queue->count = 0;
}
