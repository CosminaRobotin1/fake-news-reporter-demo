import os
import google.generativeai as genai
from dotenv import load_dotenv


load_dotenv()

genai.configure(api_key=os.getenv("GEMINI_API_KEY"))


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


def ask_gemini(text: str) -> dict:
    try:
        model = genai.GenerativeModel("gemini-2.5-flash")

        prompt = f"""
You are a fact-check assistant.

Classify the claim as exactly one of:
TRUE
FALSE
UNCERTAIN

Also provide a confidence score between 0.0 and 1.0.

Return ONLY in this exact format:
VERDICT: TRUE/FALSE/UNCERTAIN
CONFIDENCE: number between 0.0 and 1.0
REASON: short explanation

Claim: {text}
"""

        response = model.generate_content(prompt)
        output = response.text.strip()

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

        if "REASON:" in output.upper():
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

    except Exception as e:
        return {
            "verdict": "UNCERTAIN",
            "confidence": None,
            "reason": f"Gemini error: {str(e)}"
        }