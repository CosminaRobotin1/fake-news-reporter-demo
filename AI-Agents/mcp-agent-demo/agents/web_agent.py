import asyncio
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client


async def _fetch_url_with_mcp_async(url: str) -> str:
    server_params = StdioServerParameters(
        command="python",
        args=["mcp_server.py"]
    )

    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()

            result = await session.call_tool(
                "fetch_url",
                arguments={"url": url}
            )

            return result.content[0].text


def fetch_url_with_mcp(url: str) -> str:
    return asyncio.run(_fetch_url_with_mcp_async(url))
#pornește MCP Serverul, apelează tool-ul fetch_url și primește textul extras din pagina web