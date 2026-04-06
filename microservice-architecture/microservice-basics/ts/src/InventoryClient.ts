export interface InventoryClient {
  reserve(sku: string, quantity: number): boolean
}
