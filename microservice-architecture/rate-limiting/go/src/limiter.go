package src

type FixedWindowLimiter struct {
	limit int
	count int
}

func NewFixedWindowLimiter(limit int) *FixedWindowLimiter {
	return &FixedWindowLimiter{limit: limit}
}

func (l *FixedWindowLimiter) Allow() bool {
	if l.count >= l.limit {
		return false
	}
	l.count++
	return true
}

func (l *FixedWindowLimiter) AdvanceWindow() {
	l.count = 0
}
