import sys
from concrete.ml.deployment.fhe_client_server import FHEModelServer
from pathlib import Path
import base64

def main(app_user_id, model_id, ciphertext):
    DIR = Path(__file__).parent / "trups-data"
    MODEL_PATH = DIR / "models" / model_id.__str__() / "model"
    EVALUATION_KEY_PATH = DIR / "evaluation-keys" / app_user_id.__str__() / model_id.__str__()

    evaluation_key = EVALUATION_KEY_PATH.open("rb").read()

    fhe_server = FHEModelServer(MODEL_PATH)

    print(base64.b64encode(fhe_server.run(base64.standard_b64decode(ciphertext), evaluation_key)).decode("utf-8"), end="")

if __name__ == "__main__":
    main(app_user_id=sys.argv[1], model_id=sys.argv[2], ciphertext=sys.argv[3])
