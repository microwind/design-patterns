package mail

import (
	"html/template"
)

// OrderConfirmationMailData 订单确认邮件数据
type OrderConfirmationMailData struct {
	UserName    string
	OrderNo     string
	OrderID     int64
	TotalAmount float64
	Status      string
}

// GetOrderConfirmationTemplate 获取订单确认邮件模板
func GetOrderConfirmationTemplate() *template.Template {
	tmpl, _ := template.New("order_confirmation").Parse(`
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body {
            font-family: Arial, sans-serif;
            color: #333;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .header {
            background-color: #4CAF50;
            color: white;
            padding: 20px;
            text-align: center;
            border-radius: 5px 5px 0 0;
        }
        .content {
            padding: 20px;
        }
        .order-info {
            margin: 20px 0;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .order-info table {
            width: 100%;
            border-collapse: collapse;
        }
        .order-info table tr {
            border-bottom: 1px solid #ddd;
        }
        .order-info table td {
            padding: 10px;
        }
        .order-info table td:first-child {
            font-weight: bold;
            width: 40%;
        }
        .footer {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
            text-align: center;
            font-size: 12px;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>订单确认</h1>
        </div>
        <div class="content">
            <p>亲爱的 {{.UserName}}，</p>
            <p>感谢您的订单！您的订单已成功创建，以下是订单详情：</p>

            <div class="order-info">
                <table>
                    <tr>
                        <td>订单号</td>
                        <td>{{.OrderNo}}</td>
                    </tr>
                    <tr>
                        <td>订单金额</td>
                        <td>¥{{.TotalAmount}}</td>
                    </tr>
                    <tr>
                        <td>订单状态</td>
                        <td>{{.Status}}</td>
                    </tr>
                </table>
            </div>

            <p>我们已经收到您的订单，订单号为 {{.OrderNo}}。</p>
            <p>您可以通过订单号查询订单状态。感谢您的购买！</p>

            <div class="footer">
                <p>这是一封自动生成的邮件，请勿直接回复。</p>
                <p>如有问题，请联系我们的客服团队。</p>
            </div>
        </div>
    </div>
</body>
</html>
`)
	return tmpl
}
