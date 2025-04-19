import os
import sys
import pandas
import numpy
import pickle
import zipfile
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from concrete.ml.deployment.fhe_client_server import FHEModelDev
from concrete.ml.sklearn import LinearRegression, LogisticRegression, DecisionTreeRegressor, DecisionTreeClassifier
from pathlib import Path
from sklearn.model_selection import train_test_split


def main(model_id, model_type):
    DIR = Path(__file__).parent / "trups-data" / "models" / model_id.__str__()
    DATA_PATH = DIR / "data.csv"
    MODEL_PATH = DIR / "model"
    PRE_PROCESSOR_PATH = DIR / "model" / "pre_processor.pkl"
    CLIENT_FILES = DIR / "client-files.zip"

    data_x = pandas.read_csv(DATA_PATH, encoding="utf-8")
    data_y = data_x.pop("Target").copy().to_frame()

    X_train, X_test, y_train, y_test = train_test_split(data_x, data_y, train_size=0.8, random_state=1)

    one_hot_columns = []
    standard_scaler_columns = []

    for i, val in enumerate(data_x.iloc[1].values):
        if isinstance(val, (numpy.int_, numpy.float_)):
            standard_scaler_columns.append(data_x.columns[i])
        else:
            one_hot_columns.append(data_x.columns[i])

    pre_processor = ColumnTransformer(
        transformers=[
            ("one_hot", OneHotEncoder(sparse_output=False), one_hot_columns),
            ("standard_scaler", StandardScaler(), standard_scaler_columns)
        ],
        remainder="passthrough",
        verbose_feature_names_out=False
    )

    pre_processed_X_train = pre_processor.fit_transform(X_train)
    pre_processed_X_test = pre_processor.fit_transform(X_test)

    if model_type == "LOGISTIC_REGRESSION":
        model = LogisticRegression()
    elif model_type == "LINEAR_REGRESSION":
        model = LinearRegression()
    elif model_type == "DECISION_TREE_REGRESSOR":
        model = DecisionTreeRegressor()
    else:
        model = DecisionTreeClassifier()

    if model_type == "LOGISTIC_REGRESSION" or model_type == "DECISION_TREE_CLASSIFIER":
        CLASS_MAPPING_PATH = DIR / "class_mapping.pkl"
        class_mapping = list(pandas.unique(data_y["Target"].to_list()))
        y_train["Target"] = y_train["Target"].map(lambda x: class_mapping.index(x))
        y_test["Target"] = y_test["Target"].map(lambda x: class_mapping.index(x))
        pickle.dump(class_mapping, CLASS_MAPPING_PATH.open("wb"))

    model.fit(pre_processed_X_train, y_train)

    score = model.score(pre_processed_X_test, y_test)

    model.compile(pre_processed_X_train)

    if not os.path.exists(MODEL_PATH):
        os.mkdir(MODEL_PATH)

    fhe_model = FHEModelDev(MODEL_PATH, model)

    fhe_model.save(via_mlir = True)

    pickle.dump(pre_processor, PRE_PROCESSOR_PATH.open("wb"))

    zf = zipfile.ZipFile(CLIENT_FILES, mode="w")

    zf.write(PRE_PROCESSOR_PATH, "pre_processor.pkl", compress_type=zipfile.ZIP_DEFLATED)
    zf.write(MODEL_PATH / "client.zip", "client.zip", compress_type=zipfile.ZIP_DEFLATED)

    if model_type == "LOGISTIC_REGRESSION" or model_type == "DECISION_TREE_CLASSIFIER":
        zf.write(DIR / "class_mapping.pkl", "class_mapping.pkl", compress_type=zipfile.ZIP_DEFLATED)

    zf.close()

    print(str(score), end="")

if __name__ == "__main__":
    main(model_id=sys.argv[1], model_type=sys.argv[2])
