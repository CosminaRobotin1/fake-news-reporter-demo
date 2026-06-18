from crewai import Agent, Task, Crew, LLM
import os


def _parse_confidence(output: str) -> float | None:
    if "CONFIDENCE:" not in output.upper():
        return None

    try:
        lines = output.splitlines()

        for line in lines:
            if line.upper().startswith("CONFIDENCE:"):
                raw_value = line.split(":", 1)[1].strip()
                confidence = float(raw_value)

                if confidence < 0:
                    return 0.0

                if confidence > 1:
                    return 1.0

                return confidence

    except Exception:
        return None

    return None


def run_crewai_factcheck(text: str) -> dict:

    """
    OLD VERSION - Groq cloud model

    llm = LLM(
        model="groq/llama-3.3-70b-versatile",
        api_key=os.getenv("GROQ_API_KEY")
    )
    """

    # NEW VERSION - local Ollama model
    llm = LLM(
        model="ollama/llama3.2:1b",
        base_url="http://localhost:11434"
    )

    # AGENT 1 - pregătește textul
    extractor_agent = Agent(
        role="Content Preparation Agent",
        goal="Extract and prepare the main claim from the provided text",
        backstory="You clean and simplify user input so that another agent can fact-check it correctly.",
        verbose=False,
        llm=llm
    )

    # AGENT 2 - verifică afirmația
    fact_checker_agent = Agent(
        role="Fact Checker Agent",
        goal="Decide if a claim is TRUE, FALSE or UNCERTAIN and provide a confidence score",
        backstory="You are an expert in verifying information and identifying misinformation.",
        verbose=False,
        llm=llm
    )

    # AGENT 3 - formatează răspunsul final
    formatter_agent = Agent(
        role="Response Formatter Agent",
        goal="Format the fact-checking result in the exact required output format",
        backstory="You transform analysis results into a clean, strict and machine-readable response.",
        verbose=False,
        llm=llm
    )

    extract_task = Task(
        description=f"""
You receive a user input that may contain a claim, a longer text, or extracted article content.

Your job:
- identify the main claim that should be fact-checked
- remove irrelevant details
- keep the meaning unchanged
- do not decide if it is true or false

Input:
{text}

Return ONLY the cleaned claim text.
""",
        expected_output="A single cleaned claim that can be fact-checked.",
        agent=extractor_agent
    )

    factcheck_task = Task(
        description="""
Analyze the cleaned claim from the previous task.

Classify it as exactly one of:
TRUE
FALSE
UNCERTAIN

Also provide a confidence score between 0.0 and 1.0.

Return your analysis with:
VERDICT
CONFIDENCE
REASON
""",
        expected_output="""
VERDICT: TRUE/FALSE/UNCERTAIN
CONFIDENCE: number between 0.0 and 1.0
REASON: short explanation
""",
        agent=fact_checker_agent,
        context=[extract_task]
    )

    format_task = Task(
        description="""
Take the fact-check result from the previous task and format it strictly.

Return ONLY in this exact format:
VERDICT: TRUE/FALSE/UNCERTAIN
CONFIDENCE: number between 0.0 and 1.0
REASON: short explanation

Do not add markdown.
Do not add extra text.
Do not add bullet points.
""",
        expected_output="""
VERDICT: TRUE/FALSE/UNCERTAIN
CONFIDENCE: number between 0.0 and 1.0
REASON: short explanation
""",
        agent=formatter_agent,
        context=[factcheck_task]
    )

    crew = Crew(
        agents=[extractor_agent, fact_checker_agent, formatter_agent],
        tasks=[extract_task, factcheck_task, format_task],
        verbose=False
    )

    result = crew.kickoff()
    output = str(result).strip()

    verdict = "UNCERTAIN"
    confidence = None
    reason = output
    upper_output = output.upper()

    if "VERDICT: FALSE" in upper_output:
        verdict = "FALSE"

    elif "VERDICT: TRUE" in upper_output:
        verdict = "TRUE"

    elif "VERDICT: UNCERTAIN" in upper_output:
        verdict = "UNCERTAIN"

    confidence = _parse_confidence(output)

    if "REASON:" in upper_output:
        reason_lines = output.splitlines()

        for i, line in enumerate(reason_lines):
            if line.upper().startswith("REASON:"):
                first_reason_part = line.split(":", 1)[1].strip()

                remaining_lines = reason_lines[i + 1:]
                extra_text = "\n".join(remaining_lines).strip()

                if first_reason_part and extra_text:
                    reason = f"{first_reason_part}\n{extra_text}"

                elif first_reason_part:
                    reason = first_reason_part

                else:
                    reason = extra_text

                break

    return {
        "verdict": verdict,
        "confidence": confidence,
        "reason": reason
    }