#!/bin/bash
source .venv/bin/activate

rm -rf __pycache__ build dist

pyinstaller main.spec
