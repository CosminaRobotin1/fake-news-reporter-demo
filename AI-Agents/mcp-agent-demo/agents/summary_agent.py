from crewai import Agent, Task, Crew, LLM


def summarize_text(text: str) -> str:
    llm = LLM(
        model="ollama/qwen2.5:3b",
        base_url="http://localhost:11434"
    )

    summary_agent = Agent(
        role="Summary Agent",
        goal="Create a short and clear summary from a given text",
        backstory="You summarize long texts into simple and useful summaries.",
        verbose=False,
        llm=llm
    )

    summary_task = Task(
        description=f"""
Summarize the following text in Romanian.
Keep it short and clear.

Text:
{text}
""",
        expected_output="A short summary in Romanian.",
        agent=summary_agent
    )

    crew = Crew(
        agents=[summary_agent],
        tasks=[summary_task],
        verbose=False
    )

    result = crew.kickoff()

    return str(result).strip()
#primește textul citit de pe site și folosește Qwen prin Ollama ca să genereze un rezumat