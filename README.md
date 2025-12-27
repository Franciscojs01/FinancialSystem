# 💰 Financial System API (Incomplete)

A RESTful API for complete personal finance management, developed in Java with Spring Boot.
The architecture is designed to handle user authentication and detailed control of expenses and investments, following the RESTful API standard.

## 🌟 Overview & Architecture

The system implements a **RESTful** architecture and uses **Bearer Token (JWT)** for authentication and security.
The project focuses on modeling complex entities and designing clear and functional endpoints.

## 🛠️ Key Technologies
- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Security (JWT)
- MapStruct
- Jakarta Validation
- MySQL/PostgresSQL
- Lombok 
- Scheduler (Spring Scheduling)
## 🔑 Authentication & User Management

Endpoints

* **`POST /auth/login`**: Authenticates a user and returns a JWT token.
* **`POST /user/register`**: Registers a new user.
* **`GET /user/list`**: Returns a list of all users (Requires authentication).
* **`PUT /user/{id}`**: Updates user profile data.
* **`PUT /user/{id}/deactivate`**: Deactivates a user.
* **`PUT /user/{id}/active`**: Reactivates a user.

## 📈 Investment Module

Manages different types of investments:
STOCK, FUND, CRYPTO, FIXED_INCOME, TREASURY.

* **`POST /investments/create`**: Creates a new investment.
* **`GET /investments/list` / `GET /investments/{id}`**: Returns all investments and return details of a specific investment.
* **`PUT /investments/edit/{id}` / `DELETE /investments/delete/{id}`**: Updates an investment and deletes an investment.
* **Funcionalidade Avançada:** **`GET /investments/{id}/simulate?days={dias}`**: Simulates the future value of an investment after a specified period.

## 💸 Expense Module (Expenses)

Permite o registro e categorização de gastos para controle financeiro:

* **Tipos de Despesa:** `FOOD`, `TRANSPORT`, `HOUSING`, `HEALTH`, `LEISURE`, `OTHER`.
* **`POST /expense/create`**: Creates a new expense.
* **`PUT /expense/edit/{id}`**: Updates an existing expense.
* **`GET /expense/list `**: Lists all expenses.
* **`GET /expense/{id}`**: Retrieves an expense by ID.
* **`DELETE /expense/delete/{id}`**: Deletes an expense.

## 💸 Cost Module
* **`POST /cost/create`**: – Creates a new cost.
* **`GET /cost/list`**: – Lists all costs.
* **`GET /cost/{id}`**: – Retrieves a cost by ID.
* **`PUT /cost/edit/{id}`**: – Updates a cost.
* **`DELETE /cost/delete/{id}`**: – Deletes a cost.


## 🤝 Contact
* **Developer:** Francisco José da Silva Mendes
* **GitHub:** [Franciscojs01](https://github.com/Franciscojs01)
