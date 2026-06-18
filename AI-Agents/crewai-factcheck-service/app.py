from fastapi import FastAPI
from models.requests import FactCheckRequest
from models.responses import FactCheckResponse
from services.factcheck_service import run_factcheck

app = FastAPI()


@app.get("/")
def root():
    return {"message": "crewai-factcheck-service is running"}


@app.post("/factcheck", response_model=FactCheckResponse)
def factcheck(request: FactCheckRequest):
    return run_factcheck(request)