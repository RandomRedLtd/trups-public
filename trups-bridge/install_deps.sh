source .venv/bin/activate

pip install torch==2.3.1 --index-url https://download.pytorch.org/whl/cpu
pip install -r requirements.txt --no-deps

rm -rf .venv/lib/python3.11/site-packages/concrete/ml/sklearn/xgb.py

sed -i -e '/from .xgb import XGBClassifier, XGBRegressor/d' .venv/lib/python3.11/site-packages/concrete/ml/sklearn/__init__.py
sed -i -e '/from xgboost.sklearn import XGBModel/d' .venv/lib/python3.11/site-packages/concrete/ml/sklearn/base.py
sed -i -e 's/"xgboost" if isinstance(sklearn_model, XGBModel) else //g' .venv/lib/python3.11/site-packages/concrete/ml/sklearn/base.py
