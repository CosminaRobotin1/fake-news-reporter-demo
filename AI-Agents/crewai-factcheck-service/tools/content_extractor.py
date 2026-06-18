"""
OLD VERSION - extracts text from URLs using internet

import requests
from bs4 import BeautifulSoup


MAX_TEXT_LENGTH = 5000


def is_url(text: str) -> bool:
    if not text:
        return False
    return text.startswith("http://") or text.startswith("https://")


def extract_content(input_text: str) -> str:
    if not input_text:
        return ""

    if not is_url(input_text):
        return input_text

    try:
        response = requests.get(
            input_text,
            headers={"User-Agent": "Mozilla/5.0"},
            timeout=10
        )
        response.raise_for_status()

        soup = BeautifulSoup(response.text, "html.parser")
        body = soup.body

        if body is None:
            return input_text

        extracted_text = body.get_text(separator=" ", strip=True)

        if len(extracted_text) > MAX_TEXT_LENGTH:
            extracted_text = extracted_text[:MAX_TEXT_LENGTH]

        return extracted_text

    except Exception:
        return input_text

"""

def extract_content(input_text: str) -> str:
    if not input_text:
        return ""

    return input_text