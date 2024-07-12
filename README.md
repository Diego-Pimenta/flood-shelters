# Sistema de Apoio a Desabrigados por Enchentes

## Descrição

O projeto é uma aplicação backend para auxiliar desabrigados em enchentes. Ele controla
entradas e saídas de doações, mostra a duração do estoque do abrigo e dos centros de
distribuição, e a quantidade de itens recebidos pelos centros. Os requisitos incluem controle de
estoque dos abrigos e dos centros de distribuição, transferência de doações entre centros, ordens
de pedido e checkout de itens.

## Requisitos

- Certifique-se de ter o Docker instalado para a execução da aplicação.
- Também tenha baixado no sistema o Java 17 e a última versão do Maven.

## Instruções de Uso

1. Clone o repositório com o Git.
```bash
git clone https://github.com/Diego-Pimenta/flood-shelters.git
```

2. Entre na pasta raiz do projeto e inicie os containers definidos no "compose.yaml".
```bash
cd flood-shelters
docker compose up -d
```

3. Limpe os arquivos gerados e compile o código fonte do projeto.
```bash
mvn clean install
```

4. Executa a aplicação especificando a classe principal.
```bash
mvn exec:java -Dexec.mainClass="com.compass.Main"
```
