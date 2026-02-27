APP_NAME=gin-mvc

.PHONY: run fmt test tidy

run:
	go run cmd/main.go

fmt:
	gofmt -w $(shell find . -name '*.go')

test:
	go test ./...

tidy:
	go mod tidy
