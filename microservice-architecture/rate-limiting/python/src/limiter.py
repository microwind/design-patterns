class FixedWindowLimiter:
    def __init__(self, limit: int) -> None:
        self.limit = limit
        self.count = 0

    def allow(self) -> bool:
        if self.count >= self.limit:
            return False
        self.count += 1
        return True

    def advance_window(self) -> None:
        self.count = 0
