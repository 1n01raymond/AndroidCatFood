#!/usr/bin/python

import sys
import logging


activate_this = '/home/public/Service/AndroidCatFood/API_Server/venv/bin/activate_this.py'
with open(activate_this) as file_:
    exec(file_.read(), dict(__file__=activate_this))

logging.basicConfig(stream=sys.stderr)
sys.path.insert(0,"/home/public/Service/AndroidCatFood/API_Server/")

from main import app as application
