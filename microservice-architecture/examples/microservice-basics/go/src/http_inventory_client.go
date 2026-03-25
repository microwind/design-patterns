package src

import (
	"fmt"
	"io"
	"net/http"
	"net/url"
)

type HttpInventoryClient struct {
	BaseURL string
	Client  *http.Client
}

func NewHttpInventoryClient(baseURL string) *HttpInventoryClient {
	return &HttpInventoryClient{
		BaseURL: baseURL,
		Client:  &http.Client{},
	}
}

func (c *HttpInventoryClient) Reserve(sku string, quantity int) bool {
	endpoint := fmt.Sprintf("%s/reserve?sku=%s&quantity=%d", c.BaseURL, url.QueryEscape(sku), quantity)
	resp, err := c.Client.Get(endpoint)
	if err != nil {
		return false
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return false
	}

	return resp.StatusCode == http.StatusOK && string(body) == "OK"
}
