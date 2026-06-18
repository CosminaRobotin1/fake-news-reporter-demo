from pydantic import BaseModel
from typing import Optional


class FactCheckResponse(BaseModel):
    requestId: str
    reportId: int
    status: str
    verdict: Optional[str] = None
    confidence: Optional[float] = None
    provider: Optional[str] = None
    publisher: Optional[str] = None
    url: Optional[str] = None
    rationale: Optional[str] = None
    whatToVerify: Optional[str] = None
    errorMessage: Optional[str] = None