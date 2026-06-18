# 📰 Fake News Detection & AI Agents

This repository brings together several projects focused on **fake news detection, automated fact-checking and AI-powered information processing**.

The repository is organized into **two main sections**:

* **📰 Fake News Detection** – applications responsible for collecting, managing and verifying suspicious news reports.
* **🤖 AI Agents** – independent AI-based services that automate tasks such as fact-checking, web content extraction and article summarization using Large Language Models.

Together, these projects demonstrate how traditional software development can be combined with modern AI technologies to build intelligent systems for information verification.

---

# 📂 Repository Structure

```text
Fake-News-Detection/
│
├── fake-news-reporter
│
└── factcheck-standalone


AI-Agents/
│
├── crewai-factcheck-service
│
└── mcp-agent-demo
```

---

# 📰 Fake News Detection

The **Fake News Detection** module contains the applications responsible for reporting and verifying suspicious news sources.

## Fake News Reporter

The **Fake News Reporter** application is the main Spring Boot web application of the project.

It allows users to submit suspicious news articles or websites, while administrators can review submitted reports and decide whether they should become publicly visible.

The application provides:

* news reporting functionality;
* administrator dashboard;
* report approval and rejection;
* user authentication with Spring Security;
* report categorization;
* PostgreSQL and H2 database support;
* Docker deployment support.

Technologies used:

* Java 17
* Spring Boot
* Spring Security
* Thymeleaf
* Maven
* Docker
* PostgreSQL

---

## FactCheck Standalone

The **FactCheck Standalone** application is an independent fact-checking service developed to automatically verify online claims and news articles.

Instead of relying on a manually trained machine learning model, the service combines **Google Fact Check Tools API** with **Google Gemini** in order to analyze the provided content and generate a verification result.

When a previously verified claim is found through Google Fact Check, the service retrieves the available information together with its original source. If no matching result exists, the text is analyzed using **Gemini**, which provides an estimated verdict, a confidence score and a short explanation of the decision.

The application exposes REST endpoints and can easily be integrated into larger systems requiring automated fact-checking capabilities.

Main functionalities include:

* automatic claim verification;
* integration with Google Fact Check Tools API;
* AI-assisted verification using Google Gemini;
* confidence score estimation;
* explanation of the generated verdict;
* REST API for external integrations.

---

# 🤖 AI Agents

The second part of the repository focuses on **Artificial Intelligence and autonomous agents**.

These projects explore how local and cloud-based Large Language Models can automate information processing tasks and interact with external tools.

---

## CrewAI FactCheck Service

This project implements a multi-agent architecture using **CrewAI**.

Different agents collaborate to perform specialized tasks such as:

* extracting the main claim from a text;
* preparing the information for verification;
* generating a fact-checking verdict;
* formatting the final response.

The project also explores different execution approaches, including cloud-hosted LLMs and locally running models through **Ollama**.

Technologies used:

* Python
* CrewAI
* Ollama
* Llama
* Groq
* Qwen
* LangChain

---

## MCP Agent Demo

The **MCP Agent Demo** demonstrates how an AI agent can interact with external tools through the **Model Context Protocol (MCP)**.

The application receives a news article URL, retrieves its content through an MCP server, extracts the relevant text and generates a summary using a local **Qwen** model running with **Ollama**.

Workflow:

```text
User URL
      │
      ▼
MCP Client
      │
      ▼
MCP Server
      │
      ▼
Fetch Web Page
      │
      ▼
Extract Text
      │
      ▼
Qwen (Ollama)
      │
      ▼
Generated Summary
```

This project demonstrates a simple agentic workflow where an LLM collaborates with external tools instead of relying only on the prompt provided by the user.

---

# 🚀 Installation

## Requirements

Before running the projects, install:

* Java 17+
* Maven
* Python 3.11+
* Git
* Ollama
* Docker (optional)



# ▶ Running Fake News Reporter

```bash
cd Fake-News-Detection/fake-news-reporter

mvn spring-boot:run
```

The application will be available at:

```
http://localhost:8080
```

---

# ▶ Running FactCheck Standalone

```bash
cd Fake-News-Detection/factcheck-standalone

mvn spring-boot:run
```

---

# ▶ Running CrewAI FactCheck Service

```bash
cd AI-Agents/crewai-factcheck-service

python -m venv venv

venv\Scripts\activate

pip install -r requirements.txt

python app.py
```

---

# ▶ Running MCP Agent Demo

```bash
cd AI-Agents/mcp-agent-demo

python -m venv venv

venv\Scripts\activate

pip install -r requirements.txt
```

Start Ollama:

```bash
ollama serve
```

Download the required model:

```bash
ollama pull qwen2.5:3b
```

Run the application:

```bash
python main.py
```

---

# 💻 Technologies

* Java
* Spring Boot
* Spring Security
* Python
* CrewAI
* MCP
* Ollama
* Qwen
* Llama
* Groq
* Google Gemini
* Google Fact Check Tools API
* Docker
* PostgreSQL
* Thymeleaf
