# 📦 Estoque API

API RESTful para gerenciamento de estoque, implementada em **Java com Spring Boot 3.2**, que utiliza arquitetura de segurança baseada em JSON Web Tokens (JWT) e integração com banco de dados MySQL.

---

## 🌟 Funcionalidades Principais

* **Autenticação JWT:** Rotas protegidas por token (Bearer Token) para acesso seguro.
* **CRUD Completo:** Criação, Leitura, Atualização e Exclusão de produtos.
* **Lógica de Negócio:** Endpoint dedicado para verificação de estoque mínimo (`/alerta`).
* **Documentação:** Utilização do `springdoc-openapi` para documentação interativa via Swagger UI.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.2
* **Banco de Dados:** MySQL
* **ORM:** Spring Data JPA / Hibernate
* **Segurança:** Spring Security, JWT (JJWT)
* **Outras:** Lombok, Maven
---

## ⚙️ Configuração do Projeto
### ATENÇÃO:
* As credenciais (segredo JWT e senha do banco) são lidas através de variáveis de ambiente por motivos de segurança.
* Crie um arquivo local chamado .env (garanta que ele esteja no seu .gitignore) e adicione as suas chaves.
* O arquivo `application.properties` está configurado para ler as variáveis de ambiente e definir a conexão com o banco estoque_db.
* A API estará disponível em `http://localhost:8080`.

---

## 🌐 Endpoints da API
A documentação completa da API, incluindo esquemas de requisição e resposta, está disponível interativamente no Swagger UI.
URL da Documentação: `http://localhost:8080/swagger-ui.html`.

### I. Autenticação e Autorização
| Método |	Caminho |	Descrição |	Segurança |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/signin` | Gera um token JWT para acesso | Pública |

Obtenha o token JWT após o login e use-o no cabeçalho Authorization: Bearer <token> para todas as rotas protegidas.

### II. Gerenciamento de Produtos (CRUD)
| Método |	Caminho |	Descrição |	Segurança |
| :--- | :--- | :--- | :--- |
| POST | `/api/produtos` | Cadastra um novo produto | Protegida (JWT) |
| GET | `/api/produtos/{sku}` | Busca um produto pelo SKU | Protegida (JWT) |
| PUT | `/api/produtos/{id}` | Atualiza todos os dados de um produto pelo ID | Protegida (JWT) |
| DELETE | `/api/produtos/{id}` | Exclui um produto pelo ID | Protegida (JWT) |

### III. Lógica de Negócio
| Método |	Caminho |	Descrição |	Segurança |
| :--- | :--- | :--- | :--- |
| GET | `/api/produtos/{sku}/alerta` | Retorna `true` se a quantidade atual estiver abaixo do `estoqueMinimo` | Protegida (JWT) |

---

## 🧑‍💻 Como Contribuir
#### I. Faça o fork do projeto.
#### II. Crie uma branch para sua funcionalidade (git checkout -b feature/NovaFeature).
#### III. Faça suas alterações e garanta que todos os testes passem.
#### IV. Comite suas alterações (git commit -m 'feat: Adiciona nova feature').
#### V. Faça o push para a branch (git push origin feature/NovaFeature).
#### VI. Abra um Pull Request detalhado.
