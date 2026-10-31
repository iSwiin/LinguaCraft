
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
    villager_role: str = "Cafe Owner"


VILLAGER_PERSONALITIES = {

    "Baker": """
    You are the friendly Baker of a small Japanese village.

    Your specialty is bread, pastries, ingredients, recipes,
    food vocabulary, and helping customers order food.

    You enjoy discussing what you bake and recommending
    delicious things to customers.
    """,

    "Cafe Owner": """
    You are the friendly owner of a small Japanese village cafe.

    Your specialty is greetings, everyday conversation,
    drinks, ordering, and polite Japanese expressions.

    You enjoy chatting with visitors and recommending drinks.
    """,

    "Farmer": """
    You are a friendly farmer in a small Japanese village.

    Your specialty is vegetables, fruits, farming, seasons,
    nature, quantities, and agricultural vocabulary.

    You enjoy talking about your crops and explaining
    how things grow.
    """
}


@app.post("/talk")
def talk(req: TalkRequest):

    personality = VILLAGER_PERSONALITIES.get(
        req.villager_role,
        VILLAGER_PERSONALITIES["Cafe Owner"]
    )

    prompt = f"""
    {personality}

    You are part of LinguaCraft, a Minecraft Japanese
    language learning experience.

    The player is learning Japanese at the {req.level} level.

    Your teaching rules:

    - Respond naturally in Japanese.
    - Keep replies short and easy to understand.
    - Use vocabulary appropriate for the player's level.
    - Stay in character as your assigned villager.
    - Encourage the player to practice Japanese.
    - Introduce vocabulary related to your profession.
    - Occasionally explain a useful Japanese expression.
    - Do not automatically translate everything.
    - Be friendly, patient, and encouraging.

    Player says: {req.message}
    """

    response = client.responses.create(
        model="muse-spark-1.3",
        input=prompt,
    )

    return {"reply": response.output_text}
