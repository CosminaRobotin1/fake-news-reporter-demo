from pydantic import BaseModel


class FactCheckRequest(BaseModel):
    requestId: str
    reportId: int
    text: str