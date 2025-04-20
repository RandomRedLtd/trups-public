import uvicorn
import multiprocessing
import logging
import typing as t
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from trups_bridge import TrupsBridge

trups_bridge = TrupsBridge()

app = FastAPI()
app.add_middleware(CORSMiddleware, allow_origins=["http://localhost:3000", "https://ngrok-free.app", "https://trups.app"], allow_methods=["*"], allow_headers=["*"])

@app.get("/health")
def healthcheck():
    return None

@app.get("/clean")
def clean():
    return trups_bridge.clean()

@app.get("/api-key")
def get_api_key():
    return {"apiKey": trups_bridge.api_key}

@app.post("/api-key")
def save_api_key(api_key: dict):
    trups_bridge.save_api_key(api_key["apiKey"])

@app.post("/inference/{model_id}")
def inference(model_id, input: dict):
    return {"output": trups_bridge.inference(model_id, input)}

class EndpointFilter(logging.Filter):
    def __init__(
            self,
            path: str,
            *args: t.Any,
            **kwargs: t.Any,
    ):
        super().__init__(*args, **kwargs)
        self._path = path

    def filter(self, record: logging.LogRecord) -> bool:
        return record.getMessage().find(self._path) == -1

if __name__ == "__main__":
    multiprocessing.freeze_support()
    logging.getLogger("uvicorn.access").addFilter(EndpointFilter(path="/health"))
    uvicorn.run(app, host="0.0.0.0", port=8282, reload=False, workers=1, timeout_keep_alive=-1)
