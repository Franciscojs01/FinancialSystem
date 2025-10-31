# 💰 Financial System API (Incompleto)

API RESTful para gestão completa de finanças pessoais, desenvolvida em **Java** com **Spring Boot**. [cite_start]A arquitetura foi projetada para lidar com autenticação de usuários e o controle detalhado de **despesas** e **investimentos**, utilizando o padrão **API RESTful**.

## 🌟 Visão Geral e Arquitetura

[cite_start]O sistema implementa uma arquitetura **API RESTful** e utiliza o **Bearer Token (JWT)** para autenticação e segurança. O foco do projeto foi na modelagem de entidades complexas e no design de endpoints claros e funcionais.

## 🛠️ Tecnologias Chave

* **Linguagem:** Java
* **Framework:** Spring Boot
* **Persistência de Dados:** JPA/Hibernate
* **Segurança:** Autenticação via Bearer Token (JWT)
* **Banco de Dados:** PostgreSQL (Recomendado)

## 🔑 Autenticação e Gestão de Usuários

O sistema oferece as seguintes operações de usuário:

* **`POST /auth/login`**: Autentica o usuário e retorna o token JWT para acesso seguro.
* **`POST /user/register`**: Permite o registro de novos usuários.
* **`GET /user/list`**: Retorna a lista de usuários (Requer autenticação).
* **`PUT /user/{id}`**: Atualiza os dados do perfil de usuário.
* **`PUT /user/{id}/deactivate` / `PUT /user/{id}/active`**: Controla o status de ativação/desativação do usuário.

## 📈 Módulo de Investimentos

Este módulo gerencia o portfólio, suportando tipos como `STOCK`, `FUND`, `CRYPTO`, `FIXED_INCOME` e `TREASURY`.

* **`POST /investments/create`**: Registra novos investimentos com detalhes como valor, moeda (`BRL`, `USD`, `EUR`) e corretora.
* **`GET /investments/list` / `GET /investments/{id}`**: Visualiza a lista ou detalhes de um investimento específico.
* **`PUT /investments/edit/{id}` / `DELETE /investments/delete/{id}`**: Edita e remove investimentos.
* **Funcionalidade Avançada:** **`GET /investments/{id}/simulate?days={dias}`**: Simula o valor futuro do investimento após um período especificado.

## 💸 Módulo de Despesas (Expenses)

Permite o registro e categorização de gastos para controle financeiro:

* **Tipos de Despesa:** `FOOD`, `TRANSPORT`, `HOUSING`, `HEALTH`, `LEISURE`, `OTHER`.
* **`POST /expense/create`**: Registra novas despesas, incluindo tipo, valor, data, método de pagamento e flag para despesas fixas (`isFixed`).
* **`PUT /expense/edit/{id}`**: Atualiza os detalhes de uma despesa existente.

## 🤝 Contato

* **Desenvolvedor:** Francisco José da Silva Mendes
* **GitHub:** [Franciscojs01](https://github.com/Franciscojs01)
