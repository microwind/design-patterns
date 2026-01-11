package utils

import (
	"crypto/md5"
	"encoding/hex"
	"math/rand"
	"time"
)

// GenerateID 生成唯一ID
func GenerateID() string {
	rand.Seed(time.Now().UnixNano())
	return MD5(time.Now().String() + string(rune(rand.Intn(1000))))
}

// MD5 计算MD5哈希
func MD5(text string) string {
	hash := md5.Sum([]byte(text))
	return hex.EncodeToString(hash[:])
}

// Contains 判断切片是否包含元素
func Contains(slice []string, item string) bool {
	for _, s := range slice {
		if s == item {
			return true
		}
	}
	return false
}
