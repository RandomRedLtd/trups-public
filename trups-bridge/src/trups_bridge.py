import zipfile
import requests
import numpy
from pathlib import Path
from os import (mkdir, path, remove)
from shutil import rmtree
from pandas import DataFrame
from concrete.ml.deployment.fhe_client_server import FHEModelClient
from pickle import load


class TrupsBridge:
    __slots__ = ("api_key", "root_dir", "keys_dir", "models_dir")

    def __init__(self):
        self.__bootstrap()

    def clean(self):
        rmtree(self.root_dir, ignore_errors=True)
        self.__bootstrap()

    def save_api_key(self, api_key):
        (self.root_dir / "api-key").open("wb").write(api_key.encode())
        self.api_key = self.__load_api_key()

    def inference(self, model_id, inputs):
        model_path = self.models_dir / model_id.__str__()

        if not path.exists(model_path):
            mkdir(model_path)
            self.__download_client_files(model_id, model_path)

        client = FHEModelClient(model_path, self.keys_dir)

        evaluation_key_path = model_path / "evaluation_key"

        if not path.exists(evaluation_key_path):
            evaluation_key_path.open("wb").write(client.get_serialized_evaluation_keys())
            self.__upload_evaluation_key(model_id, evaluation_key_path)

        encrypted_inputs = client.quantize_encrypt_serialize(self.__build_inputs(model_path, inputs))

        inference_response = requests.post(
            url=f"https://trups.app/api/mlmodels/{model_id}/inference",
            headers={ "Content-Type": "application/octet-stream", "X-Api-Key": self.api_key },
            data=encrypted_inputs,
            stream=True
        )

        if inference_response.status_code != 200:
            if inference_response.status_code == 408:
                return "TIMEOUT"

            return f"Inference unsuccessful, status code: {inference_response.status_code}"

        encrypted_output = inference_response.raw.data

        decrypted_output = client.deserialize_decrypt_dequantize(encrypted_output).tolist()

        return self.__transform_output(model_path, decrypted_output)

    def __transform_output(self, model_path, output):
        CLASS_MAPPING_PATH = model_path / "class_mapping.pkl"

        if not Path.exists(CLASS_MAPPING_PATH):
            return output[0]

        class_mapping = load((CLASS_MAPPING_PATH).open("rb"))

        mapped_output = class_mapping[numpy.argmax(output, axis=1).squeeze()]

        if type(mapped_output) == "str":
            return mapped_output

        return mapped_output.__str__()

    def __build_inputs(self, model_path, inputs):
        mapped_inputs = dict(map(lambda kv: (kv[0], [kv[1]]), inputs.items()))

        pre_processor = load((model_path / "pre_processor.pkl").open("rb"))

        return pre_processor.transform(DataFrame(mapped_inputs))

    def __download_client_files(self, model_id, model_path):
        zipfile_path = model_path / "client-files.zip"

        open(zipfile_path, "wb").write(requests.get(url=f"https://trups.app/api/mlmodels/{model_id}/client", headers={"X-Api-Key": self.api_key}, stream=True).raw.data)

        zip_file = zipfile.ZipFile(zipfile_path, 'r')

        zip_file.extractall(model_path)

        zip_file.close()

        remove(zipfile_path)

    def __upload_evaluation_key(self, model_id, evaluation_key_path):
        requests.post(
            url=f"https://trups.app/api/mlmodels/{model_id}/evaluation-key",
            headers={ "X-Api-Key": self.api_key },
            files={"eval-key": open(evaluation_key_path, "rb").read()},
            stream=True)

    def __load_api_key(self):
        if Path.exists(self.root_dir / "api-key"):
            return (self.root_dir / "api-key").open("rb").read().decode()

        return None

    def __bootstrap(self):
        self.root_dir = Path.home() / ".trups"
        self.__create_dir(self.root_dir)

        self.keys_dir = self.root_dir / "fhe-keys"
        self.__create_dir(self.keys_dir)

        self.models_dir = self.root_dir / "models"
        self.__create_dir(self.models_dir)

        self.api_key = self.__load_api_key()

    def __create_dir(self, dir_path):
        if not path.exists(dir_path):
            mkdir(dir_path)
