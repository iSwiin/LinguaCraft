
import os

from dotenv import load_dotenv
from fastapi import FastAPI
from pydantic import BaseModel
from openai import OpenAI

# Load the API key from .env
load_dotenv()

api_key = os.getenv("MODEL_API_KEY")

if not api_key:
    raise RuntimeError(
        "MODEL_API_KEY is missing. Check your .env file."
    )

client = OpenAI(
    base_url="https://api.meta.ai/v1",
    api_key=api_key,
)

app = FastAPI()


class TalkRequest(BaseModel):
    message: str
    level: str = "beginner"


@app.post("/talk")
def talk(req: TalkRequest):
    prompt = f"""
    You are a friendly Japanese shopkeeper in Minecraft.

    The player is learning Japanese at the {req.level} level.
    Respond naturally in Japanese.
    Keep your replies short and easy to understand.
    Stay in character as a shopkeeper.
    Help the player learn without translating everything
    automatically.

    Player says: {req.message}
    """

    response = client.responses.create(
        model="muse-spark-1.3",
        input=prompt,
    )

    return {"reply": response.output_text}
