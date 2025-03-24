package request

import (
  "strconv"

  "github.com/gin-gonic/gin"
)

// 在models包中定义分页参数
type PageRequest struct {
  Page     int `form:"page" json:"page" binding:"required,min=1"`                 // 当前页码（从1开始）
  PageSize int `form:"pageSize" json:"pageSize" binding:"required,min=5,max=100"` // 每页数据量
}

// NewPageRequest 创建一个新的分页请求
func NewPageRequest(page, pageSize int) PageRequest {
  if page <= 0 {
    page = 1
  }
  if pageSize <= 0 {
    pageSize = 10
  }
  return PageRequest{
    Page:     page,
    PageSize: pageSize,
  }
}

// GetPaginationParams 获取分页参数，若为空则使用默认值
func GetPaginationParams(c *gin.Context) PageRequest {
  page := c.DefaultQuery("page", "1")          // 默认值为第1页
  pageSize := c.DefaultQuery("pageSize", "10") // 默认值为每页10条

  pageInt, _ := strconv.Atoi(page)
  pageSizeInt, _ := strconv.Atoi(pageSize)

  // 返回分页参数
  return PageRequest{
    Page:     pageInt,
    PageSize: pageSizeInt,
  }
}
