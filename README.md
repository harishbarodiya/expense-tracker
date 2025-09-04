
# 💸 Expense Tracker - Spring Boot + MySQL + AWS

This is a full-stack **Expense Tracker** backend project built with **Spring Boot**, integrated with **AWS S3**, **AWS SES**, and **MySQL**. It allows users to track their expenses, upload receipt images, and receive monthly expense reports via email.

---

## 📦 Features

- 📋 **Add / Update / Delete Expenses**
- 📊 **Monthly Expense Summary Reports**
- 🧾 **Upload and Store Receipt Images** (AWS S3)
- 📥 **Download Receipts Anytime**
- 📅 **Scheduled Monthly Email Reports** (AWS SES + Cron Jobs)
- 🧠 **AI Receipt Parsing using AWS AnalyzeExpense**
- 🔐 **JWT-based Authentication**
- 🌐 **Deployed on AWS EC2**

---

## 🛠 Tech Stack

- **Java 17** / **Spring Boot**
- **MySQL**
- **Spring Security + JWT**
- **AWS S3** (for file storage)
- **AWS SES** (for emails)
- **AWS AnalyzeExpense** (for AI-based OCR parsing)
- **EC2** (for deployment)
- **Maven / Lombok / Swagger**

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/expense-tracker.git
cd expense-tracker
```

### 2. Configure the application

Update the following in `application.yml` or `application.properties`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/expense_tracker
    username: your_mysql_user
    password: your_mysql_password

aws:
  region: your-region
  s3:
    bucket: your-s3-bucket-name
  ses:
    from-email: your-verified-email@example.com
```

Also, set your AWS credentials in `~/.aws/credentials`:

```ini
[expense-tracker]
aws_access_key_id=YOUR_ACCESS_KEY
aws_secret_access_key=YOUR_SECRET_KEY
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Swagger UI will be available at:  
📍 `http://localhost:8080/swagger-ui/index.html`

---

## 🧪 API Highlights

| Endpoint | Method | Description |
|---------|--------|-------------|
| `/api/auth/signup` | POST | Register a new user |
| `/api/auth/login` | POST | Login and get JWT |
| `/api/expenses` | GET/POST/PUT/DELETE | Expense CRUD |
| `/api/receipts/analyze-receipt` | POST | Analyze and parse a receipt |
| `/api/expense/upload-receipt` | POST | Upload a receipt |
| `/api/expense/download/{expenseId}` | GET | Download receipt by ID |

---

## 📆 Upcoming Features

- 🏷️ Expense Categories with Auto-Tagging
- 📈 Data Visualizations (Charts API)
- 🌍 Multi-Currency Support
- 📱 Mobile App (React Native)

---

## 👨‍💻 Author

**Harish Barodiya**  
🔗 [LinkedIn](https://www.linkedin.com/in/harishbarodiya)  
📧 harishbarodiya111@gmail.com
