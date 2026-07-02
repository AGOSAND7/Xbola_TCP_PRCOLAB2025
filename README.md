# XBOLA - Jogo da Velha em Rede (TCP)

Este repositório contém uma implementação de um **Jogo da Velha Multiplayer** utilizando a arquitetura **Cliente-Servidor**, desenvolvida em **Java** com comunicação via **Sockets TCP**.

O sistema permite que dois jogadores conectados pela rede disputem uma partida em tempo real através do terminal, com o servidor responsável por controlar as conexões, sincronizar as jogadas e coordenar o fluxo da partida.

---

# 🎯 Objetivo do Projeto

O projeto tem como objetivo demonstrar a aplicação prática dos conceitos de:

- Programação em Rede;
- Arquitetura Cliente-Servidor;
- Comunicação TCP;
- Programação Concorrente com Threads;
- Sincronização entre múltiplos clientes.

---

# 🏗️ Arquitetura do Sistema

O projeto está dividido em dois módulos principais.

## 1. Servidor (`servidor/`)

Responsável por toda a comunicação entre os jogadores.

### Funcionalidades

- Aceita conexões TCP;
- Controla até dois jogadores simultaneamente;
- Distribui automaticamente os símbolos **X** e **O**;
- Define qual jogador inicia a partida;
- Repassa as jogadas entre os clientes;
- Sincroniza o andamento do jogo;
- Libera novas conexões quando um jogador se desconecta.

---

## 2. Cliente (`cliente/`)

Aplicação utilizada pelos jogadores.

### Funcionalidades

- Conectar ao servidor;
- Receber identificação do jogador;
- Exibir o tabuleiro em tempo real;
- Realizar jogadas;
- Validar posições disponíveis;
- Detectar vitória, derrota ou empate.

---

# ⚙️ Tecnologias Utilizadas

- **Java (JDK 8+)**
- **Sockets TCP**
- **ServerSocket**
- **Socket**
- **Threads**
- **BufferedReader**
- **PrintWriter**
- **ConcurrentHashMap**
- **CopyOnWriteArrayList**

---

# 🚀 Como Executar

## 1. Compilar o projeto

Na pasta raiz execute:

```bash
javac servidor/*.java cliente/*.java
```

---

## 2. Iniciar o servidor

```bash
java servidor.App
```

O servidor ficará aguardando conexões na porta **12345**.

---

## 3. Executar os clientes

Abra dois terminais diferentes e execute:

```bash
java cliente.App
```

Ao iniciar, informe:

- IP do servidor (ou `localhost`)
- Porta (pressione Enter para utilizar **12345**)

Quando os dois jogadores estiverem conectados, ambos deverão selecionar a opção **Jogar** para iniciar a partida.

---

# 🎮 Funcionamento do Jogo

Após a conexão:

1. Cada jogador recebe automaticamente:
   - seu ID;
   - seu símbolo (**X** ou **O**);
   - informação sobre quem inicia.

2. Ambos escolhem a opção **Jogar**.

3. O servidor inicia a partida.

4. Os jogadores realizam jogadas alternadas.

5. O sistema verifica automaticamente:

- Vitória;
- Empate;
- Fim da partida.

---

# 📂 Estrutura do Projeto

```text
.
├── cliente/
│   ├── App.java
│   ├── Gameplay.java
│   ├── Jogador.java
│   └── Tabuleiro.java
│
├── servidor/
│   ├── App.java
│   └── Servidor.java
│
└── README.md
```

---

# 🧠 Conceitos Aplicados

- Arquitetura Cliente-Servidor
- Comunicação TCP/IP
- Programação em Rede
- Programação Concorrente
- Threads em Java
- Sincronização de Clientes
- Estruturas Concorrentes
- Protocolo de Comunicação por Mensagens
- Manipulação de Sockets

---

# 📡 Protocolo de Comunicação

O servidor e os clientes utilizam mensagens de texto simples para sincronizar o jogo.

| Mensagem | Descrição |
|----------|-----------|
| `DADOS:id,simbolo,comeca` | Envia as informações iniciais do jogador |
| `PRONTO` | Jogador pronto para iniciar |
| `INICIAR` | Início da partida |
| `JOGADA:posicao,simbolo` | Envia uma jogada ao adversário |
| `OK` | Confirma recebimento da jogada |
| `PROSSEGUIR` | Autoriza a próxima jogada |

---

# 👨‍💻 Desenvolvedor

**Agostinho Sande (Agosand)**

*Estudante do 4º Ano de Engenharia Informática*

**Universidade Zambeze**  
Matacuane – Beira, Moçambique 🇲🇿

### GitHub

<https://github.com/AGOSAND7>

---

# 📝 Licença

Este projeto é disponibilizado sob a licença **MIT**.

Sinta-se à vontade para estudar, modificar, utilizar e contribuir com melhorias.
