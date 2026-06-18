from agents.web_agent import fetch_url_with_mcp
from agents.summary_agent import summarize_text
from agents.file_agent import write_summary

url = "https://hotnews.ro/romania-fara-guvern-si-in-recesiune-un-bancher-spune-ce-putem-face-cu-banii-in-cea-mai-proasta-perioada-din-ultimii-ani-2257678"

print("Citesc pagina prin MCP...")
content = fetch_url_with_mcp(url)

print("Generez rezumatul...")
summary = summarize_text(content)

print("Salvez rezumatul...")
path = write_summary(summary)

print("Fișier creat:", path)
#citește pagina prin MCP, rezumă conținutul cu AI și salvează rezultatul într-un fișier