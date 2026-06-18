import os
import requests
from dotenv import load_dotenv


load_dotenv()


GOOGLE_FACTCHECK_API_KEY = os.getenv("GOOGLE_FACTCHECK_API_KEY")
GOOGLE_FACTCHECK_BASE_URL = "https://factchecktools.googleapis.com/v1alpha1/claims:search"


def search_google_factcheck(query: str) -> dict | None:
    if not query or not GOOGLE_FACTCHECK_API_KEY:
        return None

    try:
        response = requests.get(
            GOOGLE_FACTCHECK_BASE_URL,
            params={
                "query": query,
                "key": GOOGLE_FACTCHECK_API_KEY
            },
            timeout=10
        )
        response.raise_for_status()
        return response.json()

    except Exception:
        return None


def extract_best_google_result(api_response: dict) -> dict | None:
    if not api_response:
        return None

    claims = api_response.get("claims")
    if not claims:
        return None

    first_claim = claims[0]
    claim_text = first_claim.get("text")

    claim_reviews = first_claim.get("claimReview")
    if not claim_reviews:
        return {
            "claim": claim_text,
            "verdict": None,
            "publisher": None,
            "url": None
        }

    first_review = claim_reviews[0]

    publisher_name = None
    publisher = first_review.get("publisher")
    if publisher:
        publisher_name = publisher.get("name")

    return {
        "claim": claim_text,
        "verdict": first_review.get("textualRating"),
        "publisher": publisher_name,
        "url": first_review.get("url")
    }