
# 🍬 Sweet Haven

**Sweet Haven** is a comprehensive, full-stack Sweet Shop Management System designed to bridge the gap between traditional confectionery sales and modern e-commerce. It provides a seamless buying experience for customers and a robust, secure inventory management interface for administrators.

The application features a modern, responsive **React** frontend tailored with a custom "Sweet/Dessert" aesthetic, powered by a scalable, secure **Spring Boot** backend connected to a **PostgreSQL** database.

-----

## 🚀 Live Application

### **🔗 Access the App:** [https://Sweet-Haven.prakhar.life](https://Sweet-Haven.prakhar.life)

> **⚠️ IMPORTANT PERFORMANCE NOTE:**
> The backend of this application is deployed on **Render's Free Tier**.
> When you access the application for the first time, the server may be in a "sleeping" state. **Please wait 2 to 3 minutes** for the backend to wake up and process the first request. Once awake, the application will function at normal speed. If data doesn't load immediately, please refresh the page after a minute.

-----

## 🛠 Tech Stack

### **Frontend**

  * **Framework:** React (Vite)
  * **Styling:** Tailwind CSS (Custom Color Palette & Responsive Design)
  * **State Management:** React Context API & Session Storage
  * **Routing:** React Router DOM v6 (with Route Guards)
  * **HTTP Client:** Axios (Custom Interceptors for JWT)
  * **Deployment:** Vercel (Custom Domain)

### **Backend**

  * **Framework:** Java Spring Boot 3+
  * **Architecture:** MVC (Model-View-Controller) REST API
  * **Security:** Spring Security & JWT (JSON Web Tokens)
  * **Database:** PostgreSQL (Hosted on Neon Console)
  * **Testing:** JUnit 5 & Mockito
  * **Deployment:** Render (Dockerized)

-----

## ✨ Features

### **Public & User Features**

  * **Interactive Catalog:** Browse a wide variety of sweets with a beautiful, responsive grid layout.
  * **Smart Search & Filter:** Filter sweets by name, category, or price range using real-time search queries.
  * **Secure Authentication:** User Registration and Login protected by JWT authentication.
  * **Purchase System:** "Click to Buy" functionality that instantly updates stock levels and provides user feedback.
  * **Live Updates:** Immediate visual feedback on successful purchases or out-of-stock items.

### **Admin (Manager) Features**

  * **Dashboard:** A protected, specialized view for store managers to oversee operations.
  * **Inventory Control:** Add new sweets, update pricing details, or delete obsolete items.
  * **Stock Management:** One-click restocking feature to increase inventory levels efficiently.
  * **Role-Based Access Control (RBAC):** Strict security guards ensure only users with the `ADMIN` role can access sensitive endpoints.

-----

## 🤖 My AI Usage

In accordance with the project guidelines, I leveraged **Google Gemini** as a primary thought partner to enhance my development workflow. This allowed me to focus on architectural decisions and business logic while accelerating repetitive tasks.

### **1. Tools Used**

  * **Google Gemini:** Used for architecture planning, debugging complex configurations, and code refactoring.

### **2. Application & Workflow**

  * **Architecture Planning:** I used AI to act as a "Senior Architect" to help brainstorm the MVC folder structure and decide on the strict separation of concerns between the `InventoryService` and `SweetService`.
  * **Boilerplate Generation:** I leveraged AI to generate initial Entity models and DTOs (Data Transfer Objects), ensuring standard Java naming conventions were followed without manual typing errors.
  * **Frontend-Backend Integration:** AI assisted in writing the **Axios Interceptors** to automatically attach JWT tokens to every request, ensuring secure communication between the React frontend and Spring Boot backend.
  * **Debugging & Deployment:** When encountering CORS errors and Docker deployment issues on Render, I used AI to analyze the error logs. It suggested precise configuration fixes for `CorsConfig.java` and my `Dockerfile`, saving hours of troubleshooting.

### **3. Reflection**

Using AI significantly accelerated the "setup" and "debugging" phases of this project. Instead of getting stuck on configuration errors, I could focus on the core business logic, such as inventory calculations and state management. However, I ensured that I manually reviewed, tested, and understood every file—especially the security configurations—to ensure I wasn't just copy-pasting code blindly.

-----

## ⚙️ Local Setup Instructions

Follow these steps to run the complete project on your local machine.

### **Prerequisites**

  * **Node.js** (v18+)
  * **Java JDK** (v17+)
  * **Maven**
  * **PostgreSQL** (Local or Cloud URL)

### **1. Backend Setup (Spring Boot)**

1.  Clone the repository and navigate to the backend folder.
2.  Open `src/main/resources/application.properties`.
3.  Update the database configuration with your **PostgreSQL** credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/sweetshop
    spring.datasource.username=postgres
    spring.datasource.password=your_password
    ```
    *(Alternatively, you can use the Neon Console URL if you have one).*
4.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
5.  The backend server will start on **port 8081** (or 8080, depending on your configuration).

### **2. Frontend Setup (React)**

1.  Navigate to the frontend directory:
    ```bash
    cd sweet-shop-frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Create a `.env` file in the root folder to connect to your local backend:
    ```env
    VITE_API_BASE_URL=http://localhost:8081/api
    ```
4.  Start the development server:
    ```bash
    npm run dev
    ```
5.  Open your browser to `http://localhost:5173`.

-----

## 📬 Contact

**Prakhar Sakhare**

  * **Portfolio:** [prakhar.life](https://www.prakhar.life/)
  * **LinkedIn:** [linkedin.com/in/prakhar2712](https://linkedin.com/in/prakhar2712)
  * **GitHub:** [github.com/Prakhar4749](https://github.com/Prakhar4749/Sweet-Shop)
  * **Email:** prakharsakhare2226@gmail.com
  * **Phone:** +91 6232625599
