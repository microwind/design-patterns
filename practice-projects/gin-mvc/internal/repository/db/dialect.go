package db

import "fmt"

func Placeholder(driver string, index int) string {
	if driver == "postgres" {
		return fmt.Sprintf("$%d", index)
	}
	return "?"
}
