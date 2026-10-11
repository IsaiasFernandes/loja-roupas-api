# Loja de Roupas – API REST

API REST simples para uma loja de roupas fictícia, com cadastro de **clientes**, **produtos** (com controle de estoque simples) e **pedidos**.

Feita com **Java 17+, Spring Boot 3, Spring Data JPA e banco H2**, seguindo o padrão MVC do Spring. Todos os endpoints recebem e devolvem **JSON**.

## Estrutura do projeto

```
src/main/java/br/com/lojaroupas/
├── LojaRoupasApplication.java   # classe principal
├── model/        # entidades JPA: Produto, Cliente, Pedido
├── repository/   # interfaces Spring Data JPA
└── controller/   # endpoints REST
src/main/resources/application.properties   # configuração do banco H2
postman_collection.json                      # coleção pronta para importar no Postman
diagrama-caso-de-uso.png                     # diagrama UML de caso de uso
```

## Como rodar

Precisa só do **Java 17 ou mais novo** instalado (o Maven vem junto pelo `mvnw`).

```bash
# Linux / Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Ou abra a pasta no IntelliJ / Eclipse / VS Code e rode a classe `LojaRoupasApplication`.

A API sobe em **http://localhost:8080**.

### Banco de dados (H2)

- O banco é criado sozinho na pasta `data/` do projeto (os dados ficam salvos mesmo reiniciando).
- As tabelas são criadas automaticamente a partir das entidades.
- Para ver as tabelas no navegador: **http://localhost:8080/h2-console**
  - JDBC URL: `jdbc:h2:file:./data/lojaroupas`
  - Usuário: `sa` / Senha: (em branco)

## Entidades

| Entidade | Campos |
|---|---|
| **Produto** | `id` (Long), `nome` (String), `preco` (BigDecimal), `estoque` (Boolean) |
| **Cliente** | `id` (Long), `nome` (String), `clienteDesde` (LocalDate) |
| **Pedido**  | `id` (Long), `clienteId` (Long), `produtoId` (Long), `quantidade` (Integer) |

Regra do pedido: um cliente compra **um único produto** em uma quantidade. Ao criar um pedido, a API confere se o cliente e o produto existem, se o produto está em estoque (`estoque = true`) e se a quantidade é maior que zero. Se algo falhar, devolve `400` com a mensagem do erro.

## Endpoints

| Método | Clientes | Produtos | Pedidos | O que faz |
|---|---|---|---|---|
| POST | `/clientes` | `/produtos` | `/pedidos` | Cria um registro (`201`) |
| GET | `/clientes` | `/produtos` | `/pedidos` | Lista todos (`200`) |
| GET | `/clientes/{id}` | `/produtos/{id}` | `/pedidos/{id}` | Consulta por ID (`200` ou `404`) |
| PUT | `/clientes/{id}` | `/produtos/{id}` | `/pedidos/{id}` | Atualiza (opcional) (`200` ou `404`) |
| DELETE | `/clientes/{id}` | `/produtos/{id}` | `/pedidos/{id}` | Apaga (`204` ou `404`) |

## Exemplos de requisição (JSON)

**POST /clientes**
```json
{ "nome": "Isaias123456", "clienteDesde": "2026-10-10" }
```
Resposta: `{ "id": 1, "nome": "Isaias123456", "clienteDesde": "2026-10-10" }`

**POST /produtos**
```json
{ "nome": "Camiseta Básica Algodão", "preco": 59.90, "estoque": true }
```

**POST /pedidos**
```json
{ "clienteId": 1, "produtoId": 1, "quantidade": 3 }
```

## Testando no Postman

1. Rode a aplicação.
2. No Postman: **Import** → selecione `postman_collection.json`.
3. Execute na ordem: *Clientes → POST*, *Produtos → POST*, *Pedidos → POST*, depois os GETs e o DELETE.
4. Tire os prints de cada requisição para o PDF.

> **Atenção:** troque `Isaias123456` pelo seu **nome + RU** de verdade, como o enunciado pede.

---

## Material de apoio para o PDF da entrega

### 1. Situação fictícia (modelo)

> A Loja de Roupas é uma pequena loja que vende roupas básicas. Para melhorar a organização do negócio, foi criado um sistema simples para controlar clientes, produtos (com controle de estoque) e pedidos.
> Um cliente chamado **Isaias123456** realizou seu cadastro no sistema em 10/10/2026.
> O produto vendido pela loja chama-se **Camiseta Básica Algodão** e é vendido por unidade, ao preço de R$ 59,90, e está disponível em estoque.
> Em um determinado momento, o cliente realizou um pedido de **3 unidades** do produto.
> O sistema registra o cliente, o produto comprado e a quantidade solicitada, facilitando o controle da loja.

### 2. Diagrama de caso de uso

Ver `diagrama-caso-de-uso.png` (o código PlantUML está em `caso-de-uso.puml`, caso queira editar em https://www.plantuml.com/plantuml).

### 3. Especificação da API

Use as tabelas das seções **Entidades** e **Endpoints** acima.
