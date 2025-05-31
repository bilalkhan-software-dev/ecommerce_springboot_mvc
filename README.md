# 🛒 E-Commerce Website

A full-featured e-commerce web application built using **Spring Boot 3.4.5**, **Thymeleaf**, **Spring Security with OAuth2**, **MySQL**, **TailwindCSS**, and **Cloudinary**. This platform supports customer purchases, real-time order tracking via email, and a full administrative backend for managing products and users.

---

## 📦 Tech Stack

- **Backend**: Spring Boot, Spring Data JPA, Spring Security (OAuth2)
- **Frontend**: Thymeleaf, TailwindCSS
- **Database**: MySQL
- **Email Service**: Spring Boot Mail
- **Image Storage**:  local file system
- **Dev Tools**: Lombok, Spring DevTools
- **Build Tool**: Maven
- **Java Version**: 21

---

## 🚀 Features

### 👤 User Features

- OAuth2 login with Google, or signup via email (with verification)
- Browse product catalog with **pagination** and **search filters**
- Add products to cart
- Place orders
- Receive **real-time email updates**:
  - Order placed
  - Order confirmed/shipped/delivered
- Secure session management
- Forgot Password
- Profile Update
- Password Change

### 🔐 Admin Features

- **CRUD operations** on:
  - Product categories
  - Products
Browse product and category catalog with **pagination** 
- **User management**:
  - Enable/disable users involved in suspicious activity
- **Order management**:
  - View and manage all orders
  - Browse order and order catalog with **pagination** and **search filters**
  - Update order status (pending, shipped, delivered, etc.)
  - Automatic email notifications on status updates

---

## 🛠 Setup Instructions

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL Server
- Cloudinary Account (optional – can also use local storage)

### Clone the Repository

```bash
git clone https://github.com/your-username/e-commerce.git
cd e-commerce
