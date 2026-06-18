"""
OLD VERSION - Google Fact Check + CrewAI Groq + Gemini fallback
from models.requests import FactCheckRequest
from models.responses import FactCheckResponse
from tools.content_extractor import extract_content
from tools.google_factcheck_tool import search_google_factcheck, extract_best_google_result
from tools.gemini_tool import ask_gemini
from agents.factcheck_agent import run_crewai_factcheck


def normalize_verdict(verdict: str) -> str:
    if not verdict:
        return "UNCERTAIN"

    verdict = verdict.lower()

    if "false" in verdict or "flawed" in verdict or "misleading" in verdict:
        return "FALSE"

    if "true" in verdict:
        return "TRUE"

    return "UNCERTAIN"


def run_factcheck(request: FactCheckRequest) -> FactCheckResponse:
    prepared_text = extract_content(request.text)

    google_raw_response = search_google_factcheck(prepared_text)
    google_result = extract_best_google_result(google_raw_response)

    if google_result and google_result.get("verdict"):
        normalized_verdict = normalize_verdict(google_result.get("verdict"))

        return FactCheckResponse(
            requestId=request.requestId,
            reportId=request.reportId,
            status="DONE",
            verdict=normalized_verdict,
            confidence=google_result.get("confidence"),
            provider="GOOGLE_FACTCHECK",
            publisher=google_result.get("publisher"),
            url=google_result.get("url"),
            rationale=f"Matched claim: {google_result.get('claim')}",
            whatToVerify="Review the linked fact-check source.",
            errorMessage=None
        )

    try:
        ai = run_crewai_factcheck(prepared_text)
        normalized_verdict = normalize_verdict(ai.get("verdict"))

        return FactCheckResponse(
            requestId=request.requestId,
            reportId=request.reportId,
            status="DONE",
            verdict=normalized_verdict,
            confidence=ai.get("confidence"),
            provider="CREWAI",
            publisher=None,
            url=None,
            rationale=ai.get("reason"),
            whatToVerify=prepared_text[:500],
            errorMessage=None
        )

    except Exception as crew_error:
        try:
            ai = ask_gemini(prepared_text)
            normalized_verdict = normalize_verdict(ai.get("verdict"))

            return FactCheckResponse(
                requestId=request.requestId,
                reportId=request.reportId,
                status="DONE",
                verdict=normalized_verdict,
                confidence=ai.get("confidence"),
                provider="GEMINI",
                publisher=None,
                url=None,
                rationale=ai.get("reason"),
                whatToVerify=prepared_text[:500],
                errorMessage=None
            )

        except Exception as gemini_error:
            return FactCheckResponse(
                requestId=request.requestId,
                reportId=request.reportId,
                status="FAILED",
                verdict="UNCERTAIN",
                confidence=None,
                provider="AI_FALLBACK",
                publisher=None,
                url=None,
                rationale="CrewAI and Gemini direct both failed.",
                whatToVerify=prepared_text[:500],
                errorMessage=f"CrewAI error: {crew_error}; Gemini error: {gemini_error}"

            )

"""


from models.requests import FactCheckRequest
from models.responses import FactCheckResponse
from tools.content_extractor import extract_content
from agents.factcheck_agent import run_crewai_factcheck


def normalize_verdict(verdict: str) -> str:
    if not verdict:
        return "UNCERTAIN"

    verdict = verdict.lower()

    if "false" in verdict or "flawed" in verdict or "misleading" in verdict:
        return "FALSE"

    if "true" in verdict:
        return "TRUE"

    return "UNCERTAIN"


def run_factcheck(request: FactCheckRequest) -> FactCheckResponse:
    prepared_text = extract_content(request.text)

    try:
        ai = run_crewai_factcheck(prepared_text)
        normalized_verdict = normalize_verdict(ai.get("verdict"))

        return FactCheckResponse(
            requestId=request.requestId,
            reportId=request.reportId,
            status="DONE",
            verdict=normalized_verdict,
            confidence=ai.get("confidence"),
            provider="CREWAI_OLLAMA_LOCAL",
            publisher=None,
            url=None,
            rationale=ai.get("reason"),
            whatToVerify=prepared_text[:500],
            errorMessage=None
        )

    except Exception as error:
        return FactCheckResponse(
            requestId=request.requestId,
            reportId=request.reportId,
            status="FAILED",
            verdict="UNCERTAIN",
            confidence=None,
            provider="CREWAI_OLLAMA_LOCAL",
            publisher=None,
            url=None,
            rationale="Local CrewAI/Ollama fact-check failed.",
            whatToVerify=prepared_text[:500],
            errorMessage=str(error)
        )

