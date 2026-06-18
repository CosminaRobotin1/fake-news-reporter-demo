import requests
from bs4 import BeautifulSoup
from mcp.server.fastmcp import FastMCP

mcp = FastMCP("WebDemo")


@mcp.tool()
def fetch_url(url: str) -> str:
    response = requests.get(
        url,
        headers={"User-Agent": "Mozilla/5.0"},
        timeout=10
    )

    response.raise_for_status()

    soup = BeautifulSoup(response.text, "html.parser")

    for tag in soup(["script", "style", "nav", "footer", "header"]):
        tag.decompose()

    for tag in soup(["aside", "form"]):
        tag.decompose()

    text = soup.get_text(separator=" ", strip=True)

    if len(text) > 4000:
        text = text[:4000]

    return text


if __name__ == "__main__":
    mcp.run()

#creează un server MCP care oferă tool-ul fetch_url, folosit pentru citirea și curățarea #conținutului unei pagini web