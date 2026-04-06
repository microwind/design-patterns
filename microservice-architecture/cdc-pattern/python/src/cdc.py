from dataclasses import dataclass
from typing import List


@dataclass
class ChangeRecord:
    change_id: str
    aggregate_id: str
    change_type: str
    processed: bool


class DataStore:
    def __init__(self) -> None:
        self.changes: List[ChangeRecord] = []

    def create_order(self, order_id: str) -> None:
        self.changes.append(ChangeRecord(f"CHG-{order_id}", order_id, "order_created", False))

    def relay_changes(self, broker: "Broker") -> None:
        for change in self.changes:
            if not change.processed:
                broker.published.append(change.change_id)
                change.processed = True


class Broker:
    def __init__(self) -> None:
        self.published: List[str] = []
